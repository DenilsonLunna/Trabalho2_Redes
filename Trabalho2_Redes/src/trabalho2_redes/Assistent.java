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
import java.net.SocketException;



/**
 * 
 * @author Sammy Guergachi <sguergachi at gmail.com>
 */
public class Assistent extends Thread{
    //this class will can receive the package and send for server
    File file;
    private InfoClient client;
    private int port;
    ConvertClass convert = new ConvertClass();
    public static short idClient = 0;
    public Assistent(){
        
    }
    
    public Assistent(InfoClient client, int port) {
        this.start();
        this.client = client;
        this.port = port;
    }

    public void run() {
        try {
            //____________________________________________________________________________________________________handShake send SYNACK
            DatagramSocket assistentUDP = new DatagramSocket(port);
            Package SYNACK = new Package(4321, client.getSequenceNumber()+1,idClient++,true,true,false,port);//SYNACK
            byte[] pktSend = convert.convertPackageToByte(SYNACK);
            DatagramPacket packageSYNACK = new DatagramPacket(pktSend, pktSend.length, client.getIp(), client.getPort());
            assistentUDP.send(packageSYNACK);
            System.out.println("package SYNACK sended");
            //____________________________________________________________________________________________________send SYNACK
            
            
            System.out.println("Assistent Waiting ACK from the Client...");
            
            
            //_______________________________________________________________________________wait ACK from the client
            byte[] pktReceive = new byte[703];
            DatagramPacket pktReceiveACK = new DatagramPacket(pktReceive, pktReceive.length);
            assistentUDP.receive(pktReceiveACK);
            Package pktACKReceived = convert.convertByteToPackage(pktSend);
            System.out.println("ACK received from the client.");
            //_______________________________________________________________________________________________________
            
            //to be continued
            
            
        } catch (SocketException ex) {
            System.out.println("Erro to create datagramSocket in Assisten. cod = 9");
        } catch (IOException ex) {
            System.out.println("Erro I/O to send package SYNACK to client. cod = 10");
        }
    }
   
    
    
}
