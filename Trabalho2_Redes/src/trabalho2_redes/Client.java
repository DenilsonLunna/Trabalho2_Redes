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
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Scanner;

/**
 *
 * @author Sammy Guergachi <sguergachi at gmail.com>
 */
public class Client {

    private String idServer;
    private static int portRandom = 8000;
    private int lenghtFile = 0;
    private String fileName;
    private ArrayList<Package> packagesFileList = new ArrayList<>();
    public DatagramSocket clienteUDP = null;
    public int portAssistent = 0;
    public InetAddress addressAssistent = null;

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
        //Scanner input = new Scanner(System.in);
        /*
        arq_13KB.png == 26
        arq_168KB.obj
        arq_5855KB.mp3 == 11709
        arq_35969KB.mp4
        arq_180KB.pdf == 362
        
         */
        Client client = new Client("localHost", portRandom++, "arq_180KB.pdf");
        ConvertClass convert = new ConvertClass();


        /*System.out.println("Enter with ID-HostName from the Server");
        client.setId(input.next());
        System.out.println("Enter with you Port Connection");
        client.setPort(input.nextInt());
        System.out.println("Enter with the name file");
        client.setFileName(input.next());*/
        File file = findFile(client.getFileName());
        toFillList(file, client);

        System.out.println(client.packagesFileList.size());

        InetAddress IPAddress = null;
        System.out.println("==========================================      HANDSHAKE       ==============================================");
        try {
            //___________________________________________________________________________________Send SYN 
            IPAddress = InetAddress.getByName("localhost");
            Package SYN = new Package(0, 0, (short) 0, false, true, false);
            client.sendPackage(client, SYN, IPAddress, 6000);

            //___________________________________________________________________________________SYN sending
        } catch (UnknownHostException ex) {
            System.out.println("Erro ao Pegar IP do servidor. cod = 6");
        }

        //___________________________________________________________________________________Receive SYNACK
        Package pktSYNACKReceived = null;

        try {

            byte packageFile[] = new byte[692];
            DatagramPacket packageSYNACK = new DatagramPacket(packageFile, packageFile.length);
            client.clienteUDP.receive(packageSYNACK);// receive package SYN-ACK
            client.portAssistent = packageSYNACK.getPort();
            client.addressAssistent = packageSYNACK.getAddress();
            pktSYNACKReceived = convert.convertByteToPackage(packageFile);
            System.out.println("Client Received Sequence Number : " + pktSYNACKReceived.sequenceNumber + " - ACK: " + pktSYNACKReceived.ackNumber + " - type: " + pktSYNACKReceived.getTypePackage());

        } catch (IOException ex) {
            System.out.println("Erro I/O receive package SYN-ACK of server. cod = 8");
        }
        //____________________________________________________________________________________SYNACK Received

        //____________________________________________________________________________________Send ACK 
        Package ACK = new Package(pktSYNACKReceived.ackNumber, pktSYNACKReceived.sequenceNumber + 1, pktSYNACKReceived.idClientNumber, true, false, false);
        client.sendPackage(client, ACK, client.addressAssistent, client.portAssistent);

        //_____________________________________________________________________________________ACK sending   
        //_____________________________________________________________________________________Receive Package from the server
        Package pktReceived = null;

        try {
            byte pktB[] = new byte[692];
            DatagramPacket pkt = new DatagramPacket(pktB, pktB.length);
            client.clienteUDP.receive(pkt);// receive package SYN-ACK
            client.portAssistent = pkt.getPort();
            client.addressAssistent = pkt.getAddress();
            pktReceived = convert.convertByteToPackage(pktB);
            System.out.println("Client Received Sequence Number : " + pktReceived.sequenceNumber + " - ACK: " + pktReceived.ackNumber + " - type: " + pktReceived.getTypePackage());
        } catch (IOException ex) {
            System.out.println("Erro I/O receive package SYN-ACK of server. cod = 17");
        }
        //_____________________________________________________________________________________Package Received
        System.out.println("=============================================================================================================\n\n");

        ReceiveACKs receiveACKs = new ReceiveACKs(client.clienteUDP, 692);

        int ackForServer = 0;
        int baseNumber = 0;
        int lenghtCWND = 1;
        int ssthresh = 20;
        client.sendWindow(baseNumber, ackForServer, lenghtCWND, client, receiveACKs, ssthresh, false);

        //Execution FYN
    }

    public void sendPackage(Client client, Package pack, InetAddress ip, int port) {

        try {

            byte[] packB = ConvertClass.convertPackageToByte(pack);
            DatagramPacket packetACK = new DatagramPacket(packB, packB.length, ip, port);
            client.clienteUDP.send(packetACK);
            System.out.println("Client Send Sequence Number : " + pack.sequenceNumber + " - ACK: " + pack.ackNumber + " - type: " + pack.getTypePackage());
        } catch (IOException ex) {
            System.out.println("Erro I/O to send pakcage ACK after SYNACK in Client. cod = 11");
        }

    }

    public synchronized void sendWindow(int base, int ackForServer, int cwnd, Client client, ReceiveACKs receiveACKs, int ssthresh, boolean resend) {
        boolean stop = false;
        int qtdPackagesSended = 0;
        int qtdACkReceiveds = 0;
        receiveACKs.getPackagesListACK().clear();
        for (int i = base; i < (base + cwnd); i++) {
            if (i <= client.packagesFileList.size()-1){
                Package pack = client.packagesFileList.get(i);
                if(!resend){
                    pack.ackNumber = ackForServer;
                }
                sendPackage(client, pack, client.addressAssistent, client.portAssistent);
                qtdPackagesSended++;
            }else{
                System.out.println("Finish");
                stop = true;
                break;
            }

        }

        if (stop == false) {

            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
                System.out.println("Erro Sleep. cod = 21");
            }
            
            TimeOut time = new TimeOut(500);
            while (!time.timeout) {

                if (receiveACKs.getPackagesListACK().size() > 0) {
                    Package pack = receiveACKs.getPackagesListACK().remove(0); // get Last packageACK
                    qtdACkReceiveds++;
                    //if(pack.ackNumber == (base+1)){
                            base = pack.ackNumber;
                            ackForServer = pack.ackNumber;
                    //}
                    if (qtdACkReceiveds == qtdPackagesSended) {
                        break;
                    }
                }

            }
            
            if (time.timeout) {//timeout 
                time.cond = false;
                System.out.println("=============================================================================== TimeOut\n\n");
                System.out.println("Base = " + base);
                System.out.println("Window = " + cwnd);
                ssthresh = cwnd;
                cwnd = 1;
                sendWindow(base, ackForServer, cwnd, client, receiveACKs,ssthresh,true);

            } else if (!time.timeout) {// no timeout
                time.cond = false;
                System.out.println("No TimeOut");
                System.out.println("Base = " + base);
                if (cwnd < ssthresh) {
                    cwnd *= 2;
                } else {
                    cwnd += 1;
                }

                System.out.println("New Window = " + cwnd);
                
                   
                    sendWindow(base, ackForServer, cwnd, client, receiveACKs,ssthresh,false);
                

            }
        }
        receiveACKs.finishThread();

    }

    public static void toFillList(File file, Client c) {

        int n = 0;
        try {
            FileInputStream inFile = new FileInputStream(file);
            BufferedInputStream bufferFile = new BufferedInputStream(inFile);
            int sequenceNumber = 0;

            while (n != -1) {

                byte[] byteFile = new byte[512];
                n = bufferFile.read(byteFile); // transform file to byte
                Package pkt = new Package(byteFile);

                pkt.sequenceNumber = sequenceNumber;
                sequenceNumber += 1;
                c.packagesFileList.add(pkt);

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

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

}
