package com.proyecto_final.ftpappandroid;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.SocketException;

public class Conexion_ftp implements Serializable {

    private String server = "";
    private int port = 21;
    private String user = "";
    private String password = "";
    private FTPClient ftp;
    private String directorioLocal = "";
    private final String ficheroCCcam = "CCcam.cfg";
    private boolean conectado = false;

    public String getDirectorioLocal() {
        return directorioLocal;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isConectado() {
        return conectado;
    }

    /**
     * Método que realiza la conexón al servidor. Ingresamos en modo pasivo para no
     * tener problemas con firewall.
     *
     * @throws SocketException
     * @throws IOException
     */

    public boolean conexionFtp() throws SocketException, IOException {

        ftp = new FTPClient();
        ftp.connect(server, port);
        int respuesta = ftp.getReplyCode();

        if (!FTPReply.isPositiveCompletion(respuesta)) {
            ftp.disconnect();
            throw new IOException("Algo salió mal. El servidor responde: " + respuesta);
        }

        ftp.enterLocalPassiveMode();
        if (ftp.login(user, password)) {
            conectado = true;
        } else {
            conectado = false;
        }
        ftp.setControlEncoding("UTF-8");

        return conectado;
    }

    /**
     * Método que descarga el archivo CCcam.cfg del decodificador.
     *
     * @return File, el archivo CCcam.cfg
     * @throws Exception
     */

    public File descargaArchivo() throws Exception {
        File descarga = new File(directorioLocal);
        ftp.changeWorkingDirectory("/etc");

        OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(descarga));
        InputStream inputStream = ftp.retrieveFileStream("CCcam.cfg");

        byte[] bytesArray = new byte[4096];
        int bytesRead = -1;
        while ((bytesRead = inputStream.read(bytesArray)) != -1) {
            outputStream.write(bytesArray, 0, bytesRead);
        }

        boolean success = ftp.completePendingCommand();
        if (!success) {
            throw new Exception("ERROR EN LA DESCARGA DEL FICHERO");
        }
        outputStream.close();
        inputStream.close();

        return descarga;
    }

    /**
     * Método que cierra la conexión y hace el deslogueo del usuario.
     *
     * @throws IOException
     */

    void close() throws IOException {
        ftp.logout();
        ftp.disconnect();
    }

    /**
     * Método que lee el archivo descargado CCcam.cfg.
     *
     * @param archivo, el fichero descargado del decodificador.
     * @throws IOException
     */

    public BufferedReader leerArchivo(File archivo) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(archivo));
        return reader;
    }

    /**
     * Método que sube el fichero modificado con los datos que introducimos en el
     * textarea con las nuevas líneas CCcam.
     *
     * @throws Exception
     */

    public void subirFichero() throws Exception {
        FileInputStream fis = new FileInputStream(directorioLocal);

        ftp.setBufferSize(4096);
        ftp.setFileType(2);     //Usamos directamente el valor del campo de clase FTP.BINARY_TYPE_FILE
        ftp.changeWorkingDirectory("/etc");
        boolean uploadFile = ftp.storeFile(ficheroCCcam, fis);

        if (uploadFile == false) {
            throw new Exception("ERROR AL SUBIR EL FICHERO");
        }

        fis.close();
    }

    /**
     * Método con el que creamos una carpeta temporal en la carpeta temporal del
     * sistema para la descaga del fichero CCcam.cfg del decodificador.
     */

    public void crearCarpeta() throws IOException {
        String temp = System.getProperty("java.io.tmpdir");
        File carpeta = new File(temp + File.separator + "CCcam");
        carpeta.mkdir();
        directorioLocal = carpeta.getPath() + File.separator + "CCcam.cfg";
    }

    /**
     * Método que comprueba que el archivo CCCam.cfg existe en el directorio del
     * decodificador.
     *
     * @return existe, nos devuelve true si existe y false si no.
     * @throws IOException
     */

    public boolean existeFichero() throws IOException {
        ftp.changeWorkingDirectory("/etc");
        FTPFile[] archivos;
        boolean existe = false;

        archivos = ftp.listFiles();
        for (FTPFile archivo : archivos) {
            if (ficheroCCcam.equals(archivo.getName())) {
                existe = true;
            }
        }

        return existe;
    }

}
