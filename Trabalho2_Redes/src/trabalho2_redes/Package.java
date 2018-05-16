/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package trabalho2_redes;

/**
 * 
 * @author Sammy Guergachi <sguergachi at gmail.com>
 */
public class Package {
    private int sequenceNumber;
    private int ackNumber;
    private boolean typePackage[]; // [ACK][SYN][FYN]
    private short idClientNumber;
    
    public Package(){
        super();
        typePackage = new boolean[3];
        
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

    public boolean[] getTypePackage() {
        return typePackage;
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
    
}
