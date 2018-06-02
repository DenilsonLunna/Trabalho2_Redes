/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trabalho2_redes;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Sammy Guergachi <sguergachi at gmail.com>
 */
public class Client {

    private String idServer;
    private static int portRandom = 8000;
    private int lenghtFile = 0;
    private String fileName;
    private ArrayList<Package> packagesFileList = new ArrayList<>();
    public DatagramSocket clienteUDP = null;
    public int portAssistent = 0;
    public InetAddress addressAssistent = null;

    public Client(String id, int port, String fileName) {
        this.idServer = id;
        this.fileName = fileName;
        try {
            clienteUDP = new DatagramSocket(port);
        } catch (SocketException ex) {
            System.out.println("erro to create DatagramSocket Client. cod = 5");
        }

    }

    public static void main(String[] args) {

        //create Client
        //Scanner input = new Scanner(System.in);
        /*
        arq_13KB.png == 26
        arq_168KB.obj
        arq_5855KB.mp3 == 11709
        arq_35969KB.mp4
        arq_180KB.pdf == 362
        
         */
        Client client = new Client("localHost", portRandom++, "arq_180KB.pdf");

        /*System.out.println("Enter with ID-HostName from the Server");
        client.setId(input.next());
        System.out.println("Enter with you Port Connection");
        client.setPort(input.nextInt());
        System.out.println("Enter with the name file");
        client.setFileName(input.next());*/
        ReceiveACKs receiveACKs = new ReceiveACKs(client.clienteUDP, 692);
        InetAddress IPAddressServer = null;
        try {
            IPAddressServer = IPAddressServer = InetAddress.getByName("localhost");
        } catch (UnknownHostException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
        //____________________________________________________________________________________________ HandShake
        
        client.handShake(client,IPAddressServer, receiveACKs);

        
        
        
        
        
        File file = findFile(client.getFileName());
        toFillList(file, client);

        System.out.println(client.packagesFileList.size());

        int ackForServer = 0;
        int baseNumber = 0;
        int lenghtCWND = 1;
        int ssthresh = 20;
        receiveACKs.getPackagesListACK().clear();
        client.sendWindow(baseNumber, ackForServer, lenghtCWND, client, receiveACKs, ssthresh, false);

        //Execution FYN
    }

    public void sendPackage(Client client, Package pack, InetAddress ip, int port) {

        try {

            byte[] packB = ConvertClass.convertPackageToByte(pack);
            DatagramPacket packetACK = new DatagramPacket(packB, packB.length, ip, port);
            client.clienteUDP.send(packetACK);
            System.out.println("Client Send Sequence Number : " + pack.sequenceNumber + " - ACK: " + pack.ackNumber + " - type: " + pack.getTypePackage());
        } catch (IOException ex) {
            System.out.println("Erro I/O to send pakcage ACK after SYNACK in Client. cod = 11");
        }

    }

    public synchronized void sendWindow(int base, int ackForServer, int cwnd, Client client, ReceiveACKs receiveACKs, int ssthresh, boolean resend) {
        boolean stop = false;
        int qtdPackagesSended = 0;
        int qtdACkReceiveds = 0;
        receiveACKs.getPackagesListACK().clear();
        for (int i = base; i < (base + cwnd); i++) {
            if (i <= client.packagesFileList.size() - 1) {
                Package pack = client.packagesFileList.get(i);
                if (!resend) {
                    pack.ackNumber = ackForServer;
                }
                sendPackage(client, pack, client.addressAssistent, client.portAssistent);
                qtdPackagesSended++;
            } else {
                System.out.println("Finish");
                stop = true;
                break;
            }

        }

        if (stop == false) {

            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
                System.out.println("Erro Sleep. cod = 21");
            }

            TimeOut time = new TimeOut(500);
            while (!time.timeout) {

                if (receiveACKs.getPackagesListACK().size() > 0) {
                    Package pack = receiveACKs.getPackagesListACK().remove(0); // get Last packageACK
                    qtdACkReceiveds++;
                    //if(pack.ackNumber == (base+1)){
                    base = pack.ackNumber;
                    ackForServer = pack.ackNumber;
                    //}
                    if (qtdACkReceiveds == qtdPackagesSended) {
                        break;
                    }
                }

            }

            if (time.timeout) {//timeout 
                time.cond = false;
                System.out.println("=============================================================================== TimeOut\n\n");
                System.out.println("Base = " + base);
                System.out.println("Window = " + cwnd);
                ssthresh = cwnd;
                cwnd = 1;
                sendWindow(base, ackForServer, cwnd, client, receiveACKs, ssthresh, true);

            } else if (!time.timeout) {// no timeout
                time.cond = false;
                System.out.println("No TimeOut");
                System.out.println("Base = " + base);
                if (cwnd < ssthresh) {
                    cwnd *= 2;
                } else {
                    cwnd += 1;
                }

                System.out.println("New Window = " + cwnd);

                sendWindow(base, ackForServer, cwnd, client, receiveACKs, ssthresh, false);

            }
        }
        receiveACKs.finishThread();

    }

    public static void toFillList(File file, Client c) {

        int n = 0;
        try {
            FileInputStream inFile = new FileInputStream(file);
            BufferedInputStream bufferFile = new BufferedInputStream(inFile);
            int sequenceNumber = 0;

            while (n != -1) {

                byte[] byteFile = new byte[512];
                n = bufferFile.read(byteFile); // transform file to byte
                Package pkt = new Package(byteFile);

                pkt.sequenceNumber = sequenceNumber;
                sequenceNumber += 1;
                c.packagesFileList.add(pkt);

            }

        } catch (FileNotFoundException fnfex) {
            System.out.println("File not Found in Client. cod = 2");
        } catch (IOException ioex) {
            System.out.println("Erro Input/Output in Client. cod = 3");
        }

    }

    public Package handShake(Client client,InetAddress ipServer, ReceiveACKs receive) {

        System.out.println("==========================================      HANDSHAKE       ==============================================");

        Package pktSYNACKReceived = sendSYN(client, receive, ipServer);
        Package packStart = sendACKHS(client, pktSYNACKReceived, receive);

        System.out.println("=============================================================================================================\n\n");

        return packStart;
    }

    public Package sendSYN(Client client, ReceiveACKs receive, InetAddress ipServer) {
        receive.getPackagesListACK().clear();
        //___________________________________________________________________________________Send SYN 
        Package SYN = new Package(0, 0, (short) 0, false, true, false);
        client.sendPackage(client, SYN, ipServer, 6000);

        //___________________________________________________________________________________SYN sending
        Package pktSYNACKReceived = null;
        TimeOut time = new TimeOut(500);
        while (!time.timeout) {
            if (receive.getPackagesListACK().size() > 0) {
                pktSYNACKReceived = receive.getPackagesListACK().remove(0);
            }
            if (pktSYNACKReceived != null) {
                break;
            }

        }
        if (time.timeout) {
            time.cond = false;
            System.out.println("=================================     TIME OUT");
            pktSYNACKReceived = sendSYN(client, receive, ipServer);
        }
        time.cond = false;
        client.addressAssistent = receive.dp.getAddress();
        client.portAssistent = receive.dp.getPort();
        return pktSYNACKReceived;

    }

    public Package sendACKHS(Client client, Package pktSYNACKReceived, ReceiveACKs receive) {
        receive.getPackagesListACK().clear();
        //____________________________________________________________________________________Send ACK 
        Package ACK = new Package(pktSYNACKReceived.ackNumber, pktSYNACKReceived.sequenceNumber + 1, pktSYNACKReceived.idClientNumber, true, false, false);
        client.sendPackage(client, ACK, client.addressAssistent, client.portAssistent);

        //_____________________________________________________________________________________ACK sending   
        //_____________________________________________________________________________________Receive Package from the server
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
            pktReceived = sendACKHS(client, pktReceived, receive);
        }
        return pktReceived;
    }

    public static File findFile(String fileName) {
        String url = "C:\\Users\\Denil\\Desktop\\Drive\\UFC\\4º Semestre\\RC\\Trabalho\\Trabalho2_Redes\\Trabalho2_Redes\\src\\trabalho2_redes\\" + fileName;

        File file = new File(url);
        return file;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

}
