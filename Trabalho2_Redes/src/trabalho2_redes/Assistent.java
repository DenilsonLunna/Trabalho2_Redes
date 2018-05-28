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
import java.util.ArrayList;

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
            byte[] pktACKSend = convert.convertPackageToByte(ACK);
            DatagramPacket packageACK = new DatagramPacket(pktACKSend, pktACKSend.length, client.getIp(), client.getPort());
            assistentUDP.send(packageACK);
            System.out.println("package ACK sended");
            //____________________________________________________________________________________________________ACK sending
            //26
            int i = 0;
            int n = 0;
            int numSeqWait = 0;
            while (true) {

                byte[] pktBytes = new byte[692];
                DatagramPacket pktReceiveX = new DatagramPacket(pktBytes, pktBytes.length);
                assistentUDP.receive(pktReceiveX);
                Package pktReceived = convert.convertByteToPackage(pktBytes);
                System.out.println("Pacakge Waited = " + numSeqWait);
                System.out.println("Package received = " + pktReceived.sequenceNumber + " - ACK = " + pktReceived.ackNumber + " - Ref: " + pktReceived);

                if (pktReceived.sequenceNumber == numSeqWait) {
                    packagesList.add(pktReceived);
                    numSeqWait += 1;

                    
                    
                    
                    //___________________________________________________________________________________________________Send ACK 
                    Package ACKN = new Package(pktReceived.ackNumber, pktReceived.sequenceNumber + 1, true, false, false);
                    byte[] pktACKNSend = convert.convertPackageToByte(ACK);
                    DatagramPacket packageACKN = new DatagramPacket(pktACKNSend, pktACKNSend.length, client.getIp(), client.getPort());
                    assistentUDP.send(packageACK);
                    System.out.println("Package Sended = " + ACKN.sequenceNumber + " ACk = " + ACKN.ackNumber);
                }

                if(packagesList.size() == 26){
                    break;
                }
            }
        } catch (IOException ex) {
            System.out.println("Erro I/O to send package to client. cod = 10");
        }
    }

    public void sendPackageACK(DatagramSocket ds, Package pack, InetAddress ip, int port) {

        try {
            byte[] packB = ConvertClass.convertPackageToByte(pack);
            DatagramPacket packetACK = new DatagramPacket(packB, packB.length, ip, port);
            ds.send(packetACK);

        } catch (IOException ex) {
            System.out.println("Erro I/O to send pakcage ACK after SYNACK in Client. cod = 15");
        }

    }

}
