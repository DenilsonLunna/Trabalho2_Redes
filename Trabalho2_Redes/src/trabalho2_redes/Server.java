/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package trabalho2_redes;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import sun.security.x509.IPAddressName;





/**
 * 
 * @author Sammy Guergachi <sguergachi at gmail.com>
 */
public class Server {
    //last code = 20
    
    private static ArrayList<Assistent> assistentList = new ArrayList<>();
    
    public static void main(String[] args) {
        ConvertClass convert = new ConvertClass();
        int assistentPort = 7000;
        short idClient = 0;
        try {
            DatagramSocket serverUDP = new DatagramSocket(6000);
            
            while(true){
                //______________________________________________________________________________ Waiting package SYN
                byte packageFile[] = new byte[692]; 
                DatagramPacket pktReceive = new DatagramPacket(packageFile, packageFile.length);
                System.out.println("Server waiting packages from the clients");
                serverUDP.receive(pktReceive);
                System.out.println("Package SYN received");
                //__________________________________________________________________________________________________
                
                Package pack = convert.convertByteToPackage(packageFile);//converting from the byte for package
                
             
                if("SYN".equals(pack.getTypePackage())){
                    //create new Thread for to Administer Client
                    System.out.println(" ____PACKAGE RECEIVED_____");
                    System.out.println("|                         ");
                    System.out.println("| Sequence Number = "+pack.sequenceNumber);
                    System.out.println("| Ack Number = "+pack.ackNumber);
                    System.out.println("| Port = "+pktReceive.getPort());
                    System.out.println("| Id = "+pack.idClientNumber);
                    System.out.println("| IP client = "+pktReceive.getAddress());
                    System.out.println("|_________________________");
                    InfoClient clientInformations = new InfoClient(pack.sequenceNumber, pktReceive.getPort(), pack.idClientNumber, pktReceive.getAddress());
                    new Assistent(clientInformations, assistentPort++);    
                }
                
            }
            
            
        } catch (SocketException ex) {
            System.out.println("Erro to create DatagramSocket in Servidor. cod = 0");
        } catch (IOException ex) {
            System.out.println("Erro to receive package in Server. cod = 1");
        }
        
           
       
    }
    private static Package converterByteParaPacote(byte[] pack) {

        try {
            ByteArrayInputStream bao = new ByteArrayInputStream(pack);
            ObjectInputStream ous;
            ous = new ObjectInputStream(bao);
            return (Package) ous.readObject();

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return null;
    }
}

