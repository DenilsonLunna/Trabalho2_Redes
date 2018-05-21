/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package trabalho2_redes;

import java.net.InetAddress;

/**
 * 
 * @author Sammy Guergachi <sguergachi at gmail.com>
 */
public class InfoClient {
    private int sequenceNumber;
    private int port;
    private short id;
    private InetAddress ip;

    public InfoClient() {
    }

    public InfoClient(int sequenceNumber, int port, short id, InetAddress ip) {
        this.sequenceNumber = sequenceNumber;
        this.port = port;
        this.id = id;
        this.ip = ip;
    }

    public int getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(int sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public short getId() {
        return id;
    }

    public void setId(short id) {
        this.id = id;
    }

    public InetAddress getIp() {
        return ip;
    }

    public void setIp(InetAddress ip) {
        this.ip = ip;
    }
    
    
    
    
}
