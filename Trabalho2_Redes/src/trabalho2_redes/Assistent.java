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
import java.util.logging.Level;
import java.util.logging.Logger;

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

    public Package sendSYNACK(InfoClient client, DatagramSocket assistentUDP, ReceiveACKs receive) {
        receive.getPackagesListACK().clear();
        //___________________________________________________________________________________Send SYNACK 
        Package SYNACK = new Package(4321, client.getSequenceNumber() + 1, idClient++, true, true, false);//SYNACK
        sendPackageACK(assistentUDP, SYNACK, client.getIp(), client.getPort());
        //___________________________________________________________________________________SYNACK sending
        Package pktReceived = null;
        TimeOut time = new TimeOut(500);
        while (!time.timeout) {
            if (receive.getPackagesListACK().size() > 0) {
                pktReceived = receive.getPackagesListACK().remove(0);
            }
            if (pktReceived != null) {
                break;
            }

        }
        if (time.timeout) {
            time.cond = false;
            System.out.println("=================================     TIME OUT");
            pktReceived = sendSYNACK(client, assistentUDP, receive);
        }
        time.cond = false;
        receive.finishThread();
        return pktReceived;

    }
    public void FYNExecution(DatagramSocket assistentUDP, Package packFYN, ReceiveACKs receive){
        
        Package SYNACK = new Package(4322, packFYN.sequenceNumber + 1, idClient, true, false, true);//FYNACK
        sendPackageACK(assistentUDP, SYNACK, client.getIp(), client.getPort());
        
        
        Package FYN = new Package(4322, 0, idClient, false,false,true);//FYN
        sendPackageACK(assistentUDP, FYN, client.getIp(), client.getPort());
        
        TimeOut time = new TimeOut(500);
        Package pktReceived = null;
        while (!time.timeout) {
            if (receive.getPackagesListACK().size() > 0) {
                pktReceived = receive.getPackagesListACK().remove(0);
            }
            if (pktReceived != null) {
                break;
            }

        }
        if (time.timeout) {
            time.cond = false;
            System.out.println("=================================     TIME OUT");
            FYNExecution(assistentUDP, packFYN, receive);
        }
       
    
    }
    public void sendACK(InfoClient client, DatagramSocket assistentUDP,Package pktACKReceived) {
     
        //___________________________________________________________________________________Send SYN 
        Package ACK = new Package(pktACKReceived.ackNumber, pktACKReceived.sequenceNumber + 1, pktACKReceived.idClientNumber, true, false, false);
        sendPackageACK(assistentUDP, ACK, client.getIp(), client.getPort());
        //___________________________________________________________________________________SYN sending
        
    }

    public void run() {

        try {
            DatagramSocket assistentUDP = new DatagramSocket(port);
            ReceiveACKs receive = new ReceiveACKs(assistentUDP, 692);

            Package pktACKReceived = sendSYNACK(client, assistentUDP, receive); // send SYNACK and receive ACK
            sendACK(client, assistentUDP, pktACKReceived);//send ACK

            receive.finishThread();
           
            System.out.println("==============================================================================================\n\n");
            //26
            
            int numSeqWait = pktACKReceived.sequenceNumber+1;
            int mySequenceNumber = 4322;
            Package FYN = null;
            while (true) {
               
                byte[] pktBytes = new byte[692];
                DatagramPacket pktReceiveX = new DatagramPacket(pktBytes, pktBytes.length);
                assistentUDP.receive(pktReceiveX);
                Package pktReceived = convert.convertByteToPackage(pktBytes);
                if(pktReceived.getTypePackage() == "FYN"){
                    FYN = pktReceived;
                    System.out.println("FYN RECEIVED");
                    receive.finishThread();
                    break;
                }else
                if (pktReceived.getTypePackage() == "Data Package") {

                    System.out.println("Package received = " + pktReceived.sequenceNumber + " - ACK = " + pktReceived.ackNumber);
                    if (pktReceived.sequenceNumber == numSeqWait) {
                        packagesList.add(pktReceived);
                        numSeqWait += 1;
                        mySequenceNumber++;

                    } else {
                        System.out.println("ACK discarted");

                    }
                    Package ACKN = new Package(mySequenceNumber, numSeqWait, true, false, false);
                    this.sendPackageACK(assistentUDP, ACKN, client.getIp(), client.getPort());

                }
                
            }
            ReceiveACKs rec = new ReceiveACKs(assistentUDP, 692);
            FYNExecution(assistentUDP, FYN, rec);
            rec.finishThread();
            ArrayList<byte[]> array = new ArrayList<>();
            for (Package b : packagesList) {
                array.add(b.getData());
            }
            byte[] together = new byte[array.size() * 512];
            int position = 0;
            File arq = new File("C:\\Users\\Denil\\Desktop\\arq.pdf");
            for (int j = 0; j < array.size(); j++) {
                for (int k = 0; k < array.get(j).length; k++) {
                    together[position] = array.get(j)[k];
                    position++;

                }

            }
            System.out.println(together.length / 512);
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
            System.out.println("Package Sended : Sequence Number = " + pack.sequenceNumber + " ACk = " + pack.ackNumber+" type = "+pack.getTypePackage());
        } catch (IOException ex) {
            System.out.println("Erro I/O to send pakcage ACK after SYNACK in Client. cod = 15");
        }

    }

}
