/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package trabalho2_redes;

import java.io.Serializable;

/**
 * 
 * @author Sammy Guergachi <sguergachi at gmail.com>
 */
public class Package implements Serializable{
    public int sequenceNumber;
    public int ackNumber;
    public boolean typePackage[] = new boolean[3]; // [ACK][SYN][FYN]
    public short idClientNumber;
    public byte[] data  = new byte[512];
    
    public Package(){
        super();
        
    }
    public Package(byte[] data){
        this.data = data;
    }
    public Package(int sn, int an, short idc, boolean ack, boolean syn, boolean fyn){
        this.sequenceNumber = sn;
        this.ackNumber = an;
        this.idClientNumber = idc;
        this.typePackage[0] = ack;
        this.typePackage[1] = syn;
        this.typePackage[2] = fyn;
       
        
        
    }
    public Package(int sn, int an, boolean ack, boolean syn, boolean fyn){
        this.sequenceNumber = sn;
        this.ackNumber = an;
        this.typePackage[0] = ack;
        this.typePackage[1] = syn;
        this.typePackage[2] = fyn;
       
        
        
    }
    
    public Package(int sn, int an, short idc, boolean ack, boolean syn, boolean fyn, byte[] data){
        this.sequenceNumber = sn;
        this.ackNumber = an;
        this.idClientNumber = idc;
        this.typePackage[0] = ack;
        this.typePackage[1] = syn;
        this.typePackage[2] = fyn;
        this.data = data;
        
        
    }
    
    public String getTypePackage() {
        if(typePackage[0]&& typePackage[1]){
            return "SYNACK";
        }else
        if(typePackage[0]&& typePackage[2]){
            return "FYNACK";
        }else
        if(typePackage[0]){
            return "ACK";
        }else
        if(typePackage[1]){
            return "SYN";
        }else
        if(typePackage[2]){
            return "FYN";
        }else{
            return "Data Package";
        }
        
    }

   
    
    
}
