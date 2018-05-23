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

    private String idServer;
    private static int portRandom = 8000;

    private String fileName;
    private static ArrayList<Package> packagesFileList = new ArrayList<>();
    public DatagramSocket clienteUDP = null;

    public Client(String id, int port, String fileName) {
        this.idServer = id;
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
        System.out.println(packagesFileList.size());
        InetAddress IPAddress = null;
        try {
            //___________________________________________________________________________________Send SYN 
            IPAddress = InetAddress.getByName("localhost");
            Package SYN = new Package(12345, 0, (short) 0, false, true, false);
            Client.sendPackage(client, SYN, IPAddress, 6000);
            //___________________________________________________________________________________SYN sending
        } catch (UnknownHostException ex) {
            System.out.println("Erro ao Pegar IP do servidor. cod = 6");
        }

        //___________________________________________________________________________________Receive SYNACK
        Package pktSYNACKReceived = null;
        int portAssistent = 0;
        InetAddress addressAssistent = null;
        try {

            byte packageFile[] = new byte[692];
            DatagramPacket packageSYNACK = new DatagramPacket(packageFile, packageFile.length);
            System.out.println("Waiting SYNACK...na porta");
            client.clienteUDP.receive(packageSYNACK);// receive package SYN-ACK
            portAssistent = packageSYNACK.getPort();
            addressAssistent = packageSYNACK.getAddress();
            pktSYNACKReceived = convert.convertByteToPackage(packageFile);
            System.out.println("Package SYNACK Received.");
            System.out.println("Server Package : SeqNum( " + pktSYNACKReceived.sequenceNumber + " )  ACKNumber( " + pktSYNACKReceived.ackNumber + " )  ID-Client( " + pktSYNACKReceived.idClientNumber + " )  Type-Package( " + pktSYNACKReceived.getTypePackage() + " )");
            System.out.println("---------------------------------------------------------------------------------------------->\n");

        } catch (IOException ex) {
            System.out.println("Erro I/O receive package SYN-ACK of server. cod = 8");
        }
        //____________________________________________________________________________________SYNACK Received

        
        
        //____________________________________________________________________________________Send ACK 
        Package ACK = new Package(pktSYNACKReceived.ackNumber, pktSYNACKReceived.sequenceNumber+1, pktSYNACKReceived.idClientNumber, true, false, false);
        Client.sendPackage(client, ACK, addressAssistent, portAssistent);
        //_____________________________________________________________________________________ACK sending   

        
        
        //_____________________________________________________________________________________Receive Package from the server
        Package pktReceived = null;
        try {
            byte pktB[] = new byte[692];
            DatagramPacket pkt = new DatagramPacket(pktB, pktB.length);
            client.clienteUDP.receive(pkt);// receive package SYN-ACK
            portAssistent = pkt.getPort();
            addressAssistent = pkt.getAddress();
            pktReceived = convert.convertByteToPackage(pktB);
            System.out.println("Server Package : SeqNum( " + pktReceived.sequenceNumber + " )  ACKNumber( " + pktReceived.ackNumber + " )  ID-Client( " + pktReceived.idClientNumber + " )  Type-Package( " + pktReceived.getTypePackage() + " )");
            System.out.println("---------------------------------------------------------------------------------------------->\n");
        } catch (IOException ex) {
            System.out.println("Erro I/O receive package SYN-ACK of server. cod = 17");
        }

        //_____________________________________________________________________________________Package Received
        System.exit(0);
        boolean havePackagesToSend = true;
        int CWND = 512;
        int cont = 1; // for each package sending cont increment * 2
        int nextSecNum;
        int sendBase;
        int ssThresh = 10000;
        while (havePackagesToSend) {
            int qtdPacketsForSend = 0;
            if (cont < ssThresh) {// grow exponentially

                while (qtdPacketsForSend < cont) {// send packages 
                    
                    Package packForSend = packagesFileList.remove(packagesFileList.size() - 1);
                    
                    //packForSend.ackNumber = pktSYNACKReceived.sequenceNumber+1;
                    Client.sendPackage(client, packForSend, addressAssistent, portAssistent);
                    qtdPacketsForSend++;
                }
                
                
                int receiveACKs = 0;
                while (receiveACKs < cont) {// receive acks
                    try {

                        byte packageFile[] = new byte[692];
                        DatagramPacket packageSYNACK = new DatagramPacket(packageFile, packageFile.length);
                        client.clienteUDP.receive(packageSYNACK);// receive package SYN-ACK
                        portAssistent = packageSYNACK.getPort();
                        addressAssistent = packageSYNACK.getAddress();
                        pktSYNACKReceived = convert.convertByteToPackage(packageFile);
                        System.out.println("Server Package : SeqNum( " + pktSYNACKReceived.sequenceNumber + " )  ACKNumber( " + pktSYNACKReceived.ackNumber + " )  ID-Client( " + pktSYNACKReceived.idClientNumber + " )  Type-Package( " + pktSYNACKReceived.getTypePackage() + " )");
                        System.out.println("---------------------------------------------------------------------------------------------->\n");

                    } catch (IOException ex) {
                        System.out.println("Erro I/O receive package SYN-ACK of server. cod = 16");
                    }
                    receiveACKs++;
                }
                cont *= 2;
                
                
            } else { //grow linearly
                Package packForSend = packagesFileList.remove(packagesFileList.size() - 1);
                Client.sendPackage(client, packForSend, addressAssistent, portAssistent);
            }
            if (packagesFileList.isEmpty()) {
                havePackagesToSend = false;
            }

        }

        //send packages from the file
    }

    public static void sendPackage(Client client, Package pack, InetAddress ip, int port) {

        try {
            byte[] packB = ConvertClass.convertPackageToByte(pack);
            DatagramPacket packetACK = new DatagramPacket(packB, packB.length, ip, port);
            client.clienteUDP.send(packetACK);
            System.out.println("Send Pakcage " + pack.getTypePackage() + " for the Server");
            System.out.println("Client Package : SeqNum( " + pack.sequenceNumber + " )  ACKNumber( " + (pack.ackNumber) + " )  "
                    + "ID-Client( " + pack.idClientNumber + " )  Type-Package( " + pack.getTypePackage() + " )");
            System.out.println("<----------------------------------------------------------------------------------------------\n");
        } catch (IOException ex) {
            System.out.println("Erro I/O to send pakcage ACK after SYNACK in Client. cod = 11");
        }

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
                packagesFileList.add(pkt);
            }

        } catch (FileNotFoundException fnfex) {
            System.out.println("File not Found in Client. cod = 2");
        } catch (IOException ioex) {
            System.out.println("Erro Input/Output in Client. cod = 3");
        }

    }

    public static File findFile(String fileName) {
        String url = "C:\\Users\\Denil\\Desktop\\Drive\\UFC\\4º Semestre\\RC\\Trabalho\\Trabalho2_Redes\\Trabalho2_Redes\\src\\trabalho2_redes\\" + fileName;

        File file = new File(url);
        return file;
    }

    public static void serializable(Package pkt) {

    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

}
