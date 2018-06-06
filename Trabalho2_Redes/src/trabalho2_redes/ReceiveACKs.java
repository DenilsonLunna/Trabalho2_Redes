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
    private DatagramSocket DS;
    private int packageSize;
    public boolean cycle = true;
    public boolean braid;
    public DatagramPacket dp;
   

    public ReceiveACKs(DatagramSocket DS, int packageSize) {
        this.DS = DS;
        this.packageSize = packageSize;
        
        this.start();
    }

    @Override
    public void run() {
        int ackWaited = 1;
        int cont = 0;
        Package packBack = new Package();
        while (cycle) {
            try {
                byte pktB[] = new byte[packageSize];
                DatagramPacket pkt = new DatagramPacket(pktB, pktB.length);
                DS.receive(pkt);
                Package pktReceived = ConvertClass.convertByteToPackage(pktB);
                if (pktReceived.getTypePackage() == "SYNACK") {
                    dp = pkt;
                }
               
                
                System.out.println("Package Received with Sequence Number : " + pktReceived.sequenceNumber + " - ACK: " + pktReceived.ackNumber + " - type: " + pktReceived.getTypePackage());
                getPackagesListACK().add(pktReceived);

            } catch (IOException ex) {
                System.out.println("Erro I/O receive package. cod = 20");
            }
        }
        System.out.println("Thread ReceiveACKs finish");
    }

    public void finishThread() {
        this.cycle = false;

    }

    public synchronized ArrayList<Package> getPackagesListACK() {
        return this.ACKList;

    }

}
