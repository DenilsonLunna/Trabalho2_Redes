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
    private int sequenceNumber;
    private int ackNumber;
    private int port;
    private boolean typePackage[] = new boolean[3]; // [ACK][SYN][FYN]
    private short idClientNumber;
    private byte[] data  = new byte[512];
    
    public Package(){
        super();
        
    }
    public Package(byte[] data){
        this.data = data;
    }
    public Package(int sn){
        this.sequenceNumber = sn;
    }
    public Package(int sn, int an){
        this(sn);
        this.ackNumber = an;
        
    }
    public Package(int sn, int an, short idc){
        this(sn,an);
        this.idClientNumber = idc;  
    }
    public Package(boolean ack, boolean syn, boolean fyn){
        this.typePackage[0] = ack;
        this.typePackage[1] = syn;
        this.typePackage[2] = fyn;
        
        
    }
    public Package(int sn, int an, short idc, boolean ack, boolean syn, boolean fyn){
        this(sn,an,idc);
        this.typePackage[0] = ack;
        this.typePackage[1] = syn;
        this.typePackage[2] = fyn;
        
        
    }
    public Package(int sn, int an, short idc, boolean ack, boolean syn, boolean fyn,int port){
        this(sn,an,idc,ack,syn,fyn);
        this.port = port;
        
        
        
    }
    
    
    public int getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(int sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public int getAckNumber() {
        return ackNumber;
    }

    public void setAckNumber(int ackNumber) {
        this.ackNumber = ackNumber;
    }

    public String getTypePackage() {
        if(typePackage[0] == true){
            return "ACK";
        }else
        if(typePackage[1] == true){
            return "SYN";
        }else
        if(typePackage[2] == true){
            return "FYN";
        }else{
            return "Data Package";
        }
    }

    public void setTypePackage(boolean[] typePackage) {
        this.typePackage = typePackage;
    }

    public short getIdClientNumber() {
        return idClientNumber;
    }

    public void setIdClientNumber(short idClientNumber) {
        this.idClientNumber = idClientNumber;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
    
    
}
