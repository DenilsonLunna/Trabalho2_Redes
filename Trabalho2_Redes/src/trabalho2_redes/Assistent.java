/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trabalho2_redes;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Timer;

/**
 *
 * @author Sammy Guergachi <sguergachi at gmail.com>
 */
public class Assistent extends Thread {

    //this class will can receive the package and send for server
    File file;
    private InfoClient client;
    private int port;
    ConvertClass convert = new ConvertClass();
    private ArrayList<Package> packagesList = new ArrayList<>();
    public static short idClient = 1;

    public Assistent() {

    }

    public Assistent(InfoClient client, int port) {
        this.start();
        this.client = client;
        this.port = port;
    }

    public void run() {
        try {

            //____________________________________________________________________________________________________Send SYNACK
            DatagramSocket assistentUDP = new DatagramSocket(port);
            Package SYNACK = new Package(0, client.getSequenceNumber() + 1, idClient++, true, true, false);//SYNACK
            byte[] pktSend = convert.convertPackageToByte(SYNACK);
            DatagramPacket packageSYNACK = new DatagramPacket(pktSend, pktSend.length, client.getIp(), client.getPort());

            assistentUDP.send(packageSYNACK);
            System.out.println("package SYNACK sended");
            //____________________________________________________________________________________________________SYNACK sending

            System.out.println("Assistent Waiting ACK from the Client...");

            //____________________________________________________________________________________________________Receive ACK
            byte[] pktACKReceive = new byte[692];
            DatagramPacket pktReceiveACK = new DatagramPacket(pktACKReceive, pktACKReceive.length);
            assistentUDP.receive(pktReceiveACK);
            Package pktACKReceived = convert.convertByteToPackage(pktACKReceive);
            System.out.println("ACK received from the client.");
            //_____________________________________________________________________________________________________ ACK received

            //____________________________________________________________________________________________________Send ACK
            Package ACK = new Package(pktACKReceived.ackNumber, pktACKReceived.sequenceNumber + 1, pktACKReceived.idClientNumber, true, false, false);
            this.sendPackageACK(assistentUDP, ACK, client.getIp(), client.getPort());
            System.out.println("package ACK sended");
            //____________________________________________________________________________________________________ACK sending

            System.out.println("==============================================================================================\n\n");
            //26
            int i = 0;
            int n = 0;
            int numSeqWait = 0;
           

            while (true) {
                if(packagesList.size() == 11709){
                    System.out.println("Finish");
                    break;
                    
                }
                byte[] pktBytes = new byte[692];
                DatagramPacket pktReceiveX = new DatagramPacket(pktBytes, pktBytes.length);
                assistentUDP.receive(pktReceiveX);
                Package pktReceived = convert.convertByteToPackage(pktBytes);
               
                if (pktReceived.getTypePackage() == "Data Package") {
                    
                    System.out.println("Package received = " + pktReceived.sequenceNumber + " - ACK = " + pktReceived.ackNumber);
                    if (pktReceived.sequenceNumber == numSeqWait) {
                        packagesList.add(pktReceived);
                        numSeqWait += 1;
                        
                        
                    }
                    Package ACKN = new Package(numSeqWait-1, numSeqWait, true, false, false);
                    this.sendPackageACK(assistentUDP, ACKN, client.getIp(), client.getPort());
                    
                }else{
                    break;
                }
                
                
            }
            ArrayList<byte[]> array = new ArrayList<>();
            for (Package b : packagesList) {
                array.add(b.getData());
            }
            byte[] together = new byte[array.size() * 512];
            int position = 0;
            File arq = new File("C:\\Users\\Denil\\Desktop\\arq.mp3");
            for (int j = 0; j < array.size(); j++) {
                for (int k = 0; k < array.get(j).length; k++) {
                    together[position] = array.get(j)[k];
                    position++;
                    
                }
                
            }
            System.out.println(together.length/512);
            Files.write(arq.toPath(), together);
            System.out.println("File created");
            
            
            
            //Execution FYN

        } catch (IOException ex) {
            System.out.println("Erro I/O to send package to client. cod = 10");
        }
    }

    public void sendPackageACK(DatagramSocket ds, Package pack, InetAddress ip, int port) {

        try {
            byte[] packB = ConvertClass.convertPackageToByte(pack);
            DatagramPacket packetACK = new DatagramPacket(packB, packB.length, ip, port);
            ds.send(packetACK);
            System.out.println("Package Sended : Sequence Number = " + pack.sequenceNumber + " ACk = " + pack.ackNumber);
        } catch (IOException ex) {
            System.out.println("Erro I/O to send pakcage ACK after SYNACK in Client. cod = 15");
        }

    }

}
