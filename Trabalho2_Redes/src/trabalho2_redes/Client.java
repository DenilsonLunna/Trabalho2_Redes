/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trabalho2_redes;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
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
    private static int port;
    private String fileName;
    private static ArrayList<Package> packagesFile = new ArrayList<>();

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
        Client client = new Client("localHost", port++, "");
        DatagramSocket clienteUDP = null;
        ConvertClass convert = new ConvertClass();
        try {
            clienteUDP = new DatagramSocket();
        } catch (SocketException ex) {
            System.out.println("erro to create DatagramSocket Client. cod = 5");
        }
        /*System.out.println("Enter with ID-HostName from the Server");
        client.setId(input.next());
        System.out.println("Enter with you Port Connection");
        client.setPort(input.nextInt());
        System.out.println("Enter with the name file");
        client.setFileName(input.next());*/

        File file = findFile(client.getFileName());
        toFillList(file);

        //___________________________________________________________________________________make connection three way
        //___________________________________________________________________________________send pakcage from the connection SYN
        
        
        
        InetAddress IPAddress = null;
        Package SYN = null;
        try {
        
            //___________________________________________________________________________________SYN 
            IPAddress = InetAddress.getByName("localhost");
            SYN = new Package(12345, 0, (short) 0, false, true, false);
            byte[] SYNB = convert.convertPackageToByte(SYN);
            DatagramPacket packetSYN = new DatagramPacket(SYNB, SYNB.length, IPAddress, 6000);
            clienteUDP.send(packetSYN);
            System.out.println("Send Pakcage SYN for the Server");
            System.out.println("Client Package : SeqNum( "+SYN.getSequenceNumber()+" ) - ACKNumber( "+SYN.getAckNumber()+" ) - ID-Client( "+SYN.getIdClientNumber()+" ) - (Type-Package( "+SYN.getTypePackage()+" )");
            System.out.println("<----------------------------------------------------------------------------------------------\n");
            //___________________________________________________________________________________Send Package SYN
            
            
        } catch (UnknownHostException ex) {
            System.out.println("Erro ao Pegar IP do servidor. cod = 6");
        } catch (IOException ex) {
            System.out.println("Erro I/O send package to server. cod = 7");
        }
        
        

        //___________________________________________________________________________________wait pakcage from the verification SYN-ACK
        
        
        try {
            //_______________________________________________________________________________Receive SYNACK
            byte packageFile[] = new byte[524]; 
            DatagramPacket pkt = new DatagramPacket(packageFile, packageFile.length);
            clienteUDP.receive(pkt);// receive package SYN-ACK
            System.out.println("Package SYNACK Received.");
            //_______________________________________________________________________________SYNACK
            
        } catch (IOException ex) {
            System.out.println("Erro I/O receive package SYN-ACK of server. cod = 8");
        }    
        
        
        //____________________________________________________________________________________Send ACK from the SYNACK
        
            
            
            
            //send first package containing data or not
            //send remaining the packages
            
            /*while (packagesFile.size() > 0) {
            
            verific Acks arrived
            make size the windown for send packages
            while(variable < size windown){
            Package pack = packagesFile.remove(packagesFile.size()-1);
            fill in the remaining data from the pakcage
            Sequence Number
            ID client
            Type Package [ACK][SYN][FYN]
            
            serializable pack
            DatagramPacket pkt = new DatagramPacket(pack, pack.size, ServerAddress, portserver); //create package here
            send package for server
            }
            }*/
        

    }

    public static void toFillList(File file) {

        int n = 0;
        try {
            FileInputStream inFile = new FileInputStream(file);
            BufferedInputStream bufferFile = new BufferedInputStream(inFile);

            while (n != -1) {

                byte[] byteFile = new byte[512];
                n = bufferFile.read(byteFile); // transform file to byte
                Package pkt = new Package(byteFile);
                packagesFile.add(pkt);
            }

        } catch (FileNotFoundException fnfex) {
            System.out.println("File not Found in Client. cod = 2");
        } catch (IOException ioex) {
            System.out.println("Erro Input/Output in Client. cod = 3");
        }

    }

    

    public static File findFile(String fileName) {
        String url = "C:\\Users\\Denil\\Desktop\\Drive\\UFC\\4º Semestre\\RC\\Trabalho\\Trabalho2_Redes\\Trabalho2_Redes\\src\\trabalho2_redes" + fileName;

        File file = new File(url);
        return file;
    }

    public static void serializable(Package pkt) {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    /*public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
    */
    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

}
