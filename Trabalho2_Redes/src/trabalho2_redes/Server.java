/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package trabalho2_redes;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;




/**
 * 
 * @author Sammy Guergachi <sguergachi at gmail.com>
 */
public class Server {
    File file;
    public static void main(String[] args) {
        try {
            DatagramSocket serverUDP = new DatagramSocket(6000);
            
            while(true){
                byte file[] = new byte[524]; 
                DatagramPacket pkt = new DatagramPacket(file, file.length);
                serverUDP.receive(pkt);
                
            }
            
            
        } catch (SocketException ex) {
            System.out.println("Erro to create DatagramSocket in Servidor. cod = 0");
        } catch (IOException ex) {
            System.out.println("Erro to receive package in Server. cod = 1");
        }
        
           
       
    }
    public static boolean[] verificPackage(DatagramPacket pkt){
        
        return null;
    }
}

