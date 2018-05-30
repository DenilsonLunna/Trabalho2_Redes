/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trabalho2_redes;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.TimerTask;

/**
 *
 * @author Sammy Guergachi <sguergachi at gmail.com>
 */
public class TaskAssistentReceivePackages extends TimerTask {

    DatagramSocket assistentUDP;
    
    ArrayList<Package> packagesList;
    InfoClient client;

    public TaskAssistentReceivePackages(DatagramSocket assistentUDP, ArrayList<Package> packagesList, InfoClient client) {
        this.assistentUDP = assistentUDP;
      
        this.packagesList = packagesList;
        this.client = client;
    }

    @Override
    public void run() {
         int numSeqWait = 0;
        try{
        byte[] pktBytes = new byte[692];
        DatagramPacket pktReceiveX = new DatagramPacket(pktBytes, pktBytes.length);
        assistentUDP.receive(pktReceiveX);
        Package pktReceived = ConvertClass.convertByteToPackage(pktBytes);
        System.out.println("Package received = " + pktReceived.sequenceNumber + " - ACK = " + pktReceived.ackNumber);
        if (pktReceived.sequenceNumber == numSeqWait) {
            packagesList.add(pktReceived);
            numSeqWait += 1;
          

        }

        Package ACKN = new Package(pktReceived.ackNumber, pktReceived.sequenceNumber + 1, true, false, false);
        this.sendPackageACK(assistentUDP, ACKN, client.getIp(), client.getPort());
        }catch(IOException ex){
        
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
