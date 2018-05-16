/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trabalho2_redes;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Sammy Guergachi <sguergachi at gmail.com>
 */
public class Client {

    private String id;
    private int port;
    private String fileName;
    private ArrayList<Package> packagesFile = new ArrayList<>();
    public Client() {
        super();
    }

    public Client(String id) {
        this.id = id;
    }
    public Client(String id, int port) {
        this();
        this.port = port;
        
        
    }
    public Client(String id, int port, String fileName) {
        this(id, port);
        this.fileName = fileName;
                
        
        
    }

    public static void main(String[] args) {
        
        
        //create Client
        Scanner input = new Scanner(System.in);
        Client client = new Client();
        System.out.println("Enter with you ID-HostName");
            client.setId(input.next());
        System.out.println("Enter with you Port Connection");
            client.setPort(input.nextInt());
        System.out.println("Enter with the name file");
            client.setFileName(input.next());
            
        File file = findFile(client.getFileName());
        byte[] fileBytes = convertFileToByte(file);
        
        
        
        
        
        
        try {
            DatagramSocket clienteUDP = new DatagramSocket();

            
            
            
            
            
            
            
            
            
            
            
            
        } catch (SocketException ex) {
            System.out.println("erro to create DatagramSocket Client");
        }
    }

    public static byte[] convertFileToByte(File file) {
        int len = (int) file.length();
        byte[] sendBuf = new byte[len];
        FileInputStream inFile = null;
        try {
            inFile = new FileInputStream(file);
            inFile.read(sendBuf, 0, len);
            int n = 0;
            while(n != -1){
                byte[] byteMusic = new byte[524];
                n = bufferMusic.read(byteMusic);
                musicParts.add(byteMusic);
            
            }
            
            
            
        } catch (FileNotFoundException fnfex) {
            System.out.println("File not Found in Client. cod = 2");
        } catch (IOException ioex) {
            System.out.println("Erro Input/Output in Client. cod = 3");
        }
        return sendBuf;
    }
    public static ArrayList<Package> putFileInArray(byte[] file){
        while(file.length > 0){
            
        
        }
        return null;
    }
    public static File findFile(String fileName){
        String url = "C:\\Users\\Denil\\Desktop\\Drive\\UFC\\4º Semestre\\RC\\Trabalho\\Trabalho2_Redes\\Trabalho2_Redes\\src\\trabalho2_redes"+fileName;
        File file = new File(url);
        return file;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    
}
