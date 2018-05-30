/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trabalho2_redes;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.sql.Time;
import java.util.ArrayList;

/**
 *
 * @author Sammy Guergachi <sguergachi at gmail.com>
 */
public class ReceiveACKs extends Thread {

    public ArrayList<Package> ACKList = new ArrayList<>();
    private DatagramSocket client;
    private int packageSize;
    public boolean cycle = true;
    public boolean braid;

    public ReceiveACKs(DatagramSocket client, int packageSize) {
        this.client = client;
        this.packageSize = packageSize;
        this.start();
    }

    @Override
    public synchronized void run(){
       
        while (cycle) {
            try {
                byte pktB[] = new byte[packageSize];
                DatagramPacket pkt = new DatagramPacket(pktB, pktB.length);
                client.receive(pkt);
                Package pktReceived = ConvertClass.convertByteToPackage(pktB);
                ACKList.add(pktReceived);
                System.out.println("Client Received Sequence Number : "+pktReceived.sequenceNumber+" - ACK: "+pktReceived.ackNumber+" - type: "+pktReceived.getTypePackage());
            } catch (IOException ex) {
                System.out.println("Erro I/O receive package SYN-ACK of server. cod = 20");
            }
        }
        System.out.println("Thread ReceiveACKs finish");
    }
    public void finishThread(){
        this.cycle = false;
    
    }

}
