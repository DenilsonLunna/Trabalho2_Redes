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


/**
 *
 * @author Sammy Guergachi <sguergachi at gmail.com>
 */
public class Client {

    private String id;
    private static int portRandom = 8000;
   
    private String fileName;
    private static ArrayList<Package> packagesFile = new ArrayList<>();
    public DatagramSocket clienteUDP = null;
    

    public Client(String id, int port, String fileName) {
        this.id = id;
        this.fileName = fileName;
         try {
            clienteUDP = new DatagramSocket(port);
        } catch (SocketException ex) {
            System.out.println("erro to create DatagramSocket Client. cod = 5");
        }

    }

    public static void main(String[] args) {

        //create Client
        Scanner input = new Scanner(System.in);
        Client client = new Client("localHost", portRandom++, "arq1.mp3");
        ConvertClass convert = new ConvertClass();
       
        /*System.out.println("Enter with ID-HostName from the Server");
        client.setId(input.next());
        System.out.println("Enter with you Port Connection");
        client.setPort(input.nextInt());
        System.out.println("Enter with the name file");
        client.setFileName(input.next());*/
        
        File file = findFile(client.getFileName());
        toFillList(file);

        
        
        
        InetAddress IPAddress = null;
        Package SYN = null;
        try {
        
            //___________________________________________________________________________________SYN 
            IPAddress = InetAddress.getByName("localhost");
            SYN = new Package(12345, 0, (short) 0, false, true, false);
            byte[] SYNB = convert.convertPackageToByte(SYN);
            DatagramPacket packetSYN = new DatagramPacket(SYNB, SYNB.length, IPAddress, 6000);
            client.clienteUDP.send(packetSYN);
            System.out.println("Send Pakcage SYN for the Server");
            System.out.println("Client Package : SeqNum( "+SYN.sequenceNumber+" )  ACKNumber( "+SYN.ackNumber+" )  ID-Client( "+SYN.idClientNumber+" )  Type-Package( "+SYN.getTypePackage()+" )");
            System.out.println("<----------------------------------------------------------------------------------------------\n");
            //___________________________________________________________________________________Send Package SYN
            
            
        } catch (UnknownHostException ex) {
            System.out.println("Erro ao Pegar IP do servidor. cod = 6");
        } catch (IOException ex) {
            System.out.println("Erro I/O send package to server. cod = 7");
        }
        
        

        //___________________________________________________________________________________wait pakcage from the verification SYN-ACK
        
        Package pktSYNACKReceived = null;
        int portAssistent = 0;
        InetAddress addressAssistent = null;
        
        try {
            
            //_______________________________________________________________________________Receive SYNACK
            byte packageFile[] = new byte[703]; 
            DatagramPacket packageSYNACK = new DatagramPacket(packageFile, packageFile.length);
            System.out.println("Waiting SYNACK...na porta");
            client.clienteUDP.receive(packageSYNACK);// receive package SYN-ACK
            portAssistent = packageSYNACK.getPort();
            addressAssistent = packageSYNACK.getAddress();
            pktSYNACKReceived = convert.convertByteToPackage(packageFile);
            System.out.println("Package SYNACK Received.");
            System.out.println("Server Package : SeqNum( "+pktSYNACKReceived.sequenceNumber+" )  ACKNumber( "+pktSYNACKReceived.ackNumber+" )  ID-Client( "+pktSYNACKReceived.idClientNumber+" )  Type-Package( "+pktSYNACKReceived.getTypePackage()+" )");
            System.out.println("---------------------------------------------------------------------------------------------->\n");
            //_______________________________________________________________________________SYNACK
            
        } catch (IOException ex) {
            System.out.println("Erro I/O receive package SYN-ACK of server. cod = 8");
        }    
        
        
        //____________________________________________________________________________________Send ACK from the SYNACK
        
        try {
            Package ACK = new Package(pktSYNACKReceived.sequenceNumber, pktSYNACKReceived.ackNumber, pktSYNACKReceived.idClientNumber, true, false, false);
            byte[] SYNB = convert.convertPackageToByte(SYN);
            DatagramPacket packetACK = new DatagramPacket(SYNB, SYNB.length,addressAssistent,portAssistent);
            client.clienteUDP.send(packetACK);
            System.out.println("Send Pakcage ACK for the Server");
            System.out.println("Client Package : SeqNum( "+ACK.ackNumber+" )  ACKNumber( "+(ACK.sequenceNumber+1)+" )  ID-Client( "+ACK.idClientNumber+" )  Type-Package( "+ACK.getTypePackage()+" )");
            System.out.println("<----------------------------------------------------------------------------------------------\n");
        } catch (IOException ex) {
            System.out.println("Erro I/O to send pakcage ACK after SYNACK in Client. cod = 11");
        }
        //_____________________________________________________________________________________    
            
            
            
           //send packages from the file
        

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
        String url = "C:\\Users\\Denil\\Desktop\\Drive\\UFC\\4º Semestre\\RC\\Trabalho\\Trabalho2_Redes\\Trabalho2_Redes\\src\\trabalho2_redes\\"+fileName;

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
