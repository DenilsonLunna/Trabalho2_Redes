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
    public int portAssistent;
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
        Package lastPackage = client.handShake(client, IPAddressServer, receiveACKs);
        
        Package closeReceiveAssistent = new Package();//close receiveACKs of Assistent
        client.sendPackage(client, closeReceiveAssistent, client.addressAssistent, client.portAssistent);
        
        
        
        
        int sequenceNumberStart = lastPackage.ackNumber;
        short idClient = lastPackage.idClientNumber;
        int ackForServer = lastPackage.sequenceNumber + 1;
        File file = findFile(client.getFileName());
        toFillList(file, client, sequenceNumberStart, idClient, ackForServer);
        client.lenghtFile = client.packagesFileList.size();
        
        
        

      

        
        int baseNumber = 0;
        int lenghtCWND = 1;
        int ssthresh = 100;
        int confirm = 12347;
        receiveACKs.getPackagesListACK().clear();

        client.sendWindow(baseNumber, lenghtCWND, client, receiveACKs, ssthresh, false, false,confirm);

        client.FYN(client, receiveACKs);

        System.out.println("Client Exit");
        receiveACKs.finishThread();

    }

    public void FYN(Client client, ReceiveACKs receive) {
        System.out.println(client.lenghtFile);

        Package lastPackage = client.packagesFileList.get(client.lenghtFile - 1);

        Package fyn = new Package(lastPackage.sequenceNumber + 1, 0, client.packagesFileList.get(0).idClientNumber, false, false, true);
      
        sendPackage(client, fyn, client.addressAssistent, client.portAssistent);//send FYN
        System.out.println("FYN sended");

        int qtdACK = 0;
        Package p = null;
        Package fynAssistent = null;
        TimeOut time = new TimeOut(500);
        while (!time.timeout) {
            if (receive.getPackagesListACK().size() > 0) {
                p = receive.getPackagesListACK().remove(0); // get FYNACK
                qtdACK++;
            }
            if (p != null && p.getTypePackage() == "FYN") {
                fynAssistent = p;
                System.out.println("FYN Received");

            }
            if (p != null && p.getTypePackage() == "FYNACK") {
                System.out.println("FYNACK Received");

            }
            if (qtdACK >= 2) {
                break;
            }

        }
        if (time.timeout) {
            System.out.println("======================= TIME OUT");
            FYN(client, receive);
        }
        if (fynAssistent != null) {
            Package ACK = new Package(lastPackage.sequenceNumber, fynAssistent.sequenceNumber + 1, client.packagesFileList.get(0).idClientNumber, true, false, false);
            sendPackage(client, ACK, client.addressAssistent, client.portAssistent);
        }else{
            System.out.println("ACK FYN Null");
        }
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

    public void sendWindow(int base,int cwnd, Client client, ReceiveACKs receiveACKs, int ssthresh, boolean resend, boolean stop, int confirm) {
        receiveACKs.getPackagesListACK().clear();
        int qtdPackagesSended = 0;
        int qtdACkReceiveds = 0;
        if (stop == false) {
            for (int i = base; i < (base + cwnd); i++) {
                if (i <= client.packagesFileList.size() - 1) {
                    Package pack = client.packagesFileList.get(i);
                    sendPackage(client, pack, client.addressAssistent, client.portAssistent);
                    qtdPackagesSended++;
                } else {
                    break;
                }

            }

            try {
                Thread.sleep(50);
            } catch (InterruptedException ex) {
                System.out.println("Erro Sleep. cod = 21");
            }

            
            TimeOut time = new TimeOut(500);
            Package pack = null;
            while (!time.timeout) {

                if (receiveACKs.getPackagesListACK().size() > 0) {
                    pack = receiveACKs.getPackagesListACK().remove(0); // get Last packageACK
                    if (pack.ackNumber == client.packagesFileList.get(client.lenghtFile - 1).sequenceNumber + 1) {
                        System.out.println("Last ACK of data received");
                        stop = true;
                    }
                    if (pack.ackNumber > confirm) {
                        for (int i = confirm; i < pack.ackNumber; i++) {
                            base++;
                        }
                        confirm = pack.ackNumber;
                        
                        
                    }

                    if (receiveACKs.getPackagesListACK().isEmpty()) {
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

                sendWindow(base,cwnd, client, receiveACKs, ssthresh, true, stop, confirm);

            } else if (!time.timeout) {// no timeout
                time.cond = false;
                System.out.println("No TimeOut");
                System.out.println("Base = " + base);
                if (cwnd <= ssthresh) {
                    System.out.println("SSthreas = " + ssthresh);
                    cwnd *= 2;
                } else {
                    cwnd += 1;
                }

                System.out.println("New Window = " + cwnd);

                sendWindow(base, cwnd, client, receiveACKs, ssthresh, false, stop,confirm);

            }
        }

    }

    public static void toFillList(File file, Client c, int sequenceNumberStart, short idClient, int ackForServer) {

        int n = 0;
        try {
            FileInputStream inFile = new FileInputStream(file);
            BufferedInputStream bufferFile = new BufferedInputStream(inFile);
            int sequenceNumber = sequenceNumberStart;
            int ack = ackForServer;

            while (n != -1) {

                byte[] byteFile = new byte[512];
                n = bufferFile.read(byteFile); // transform file to byte
                Package pkt = new Package(byteFile);

                pkt.sequenceNumber = sequenceNumber;
                pkt.idClientNumber = idClient;
                pkt.ackNumber = ack;
                ack++;
                sequenceNumber ++;
                c.packagesFileList.add(pkt);

            }

        } catch (FileNotFoundException fnfex) {
            System.out.println("File not Found in Client. cod = 2");
        } catch (IOException ioex) {
            System.out.println("Erro Input/Output in Client. cod = 3");
        }

    }

    public Package handShake(Client client, InetAddress ipServer, ReceiveACKs receive) {

        System.out.println("==========================================      HANDSHAKE       ==============================================");

        Package pktSYNACKReceived = sendSYN(client, receive, ipServer);//send SYN and receive SYNACK
        Package packStart = sendACKHS(client, pktSYNACKReceived, receive);//send ACK and receive ACK

        System.out.println("=============================================================================================================\n\n");

        return packStart;
    }

    public Package sendSYN(Client client, ReceiveACKs receive, InetAddress ipServer) {
        receive.getPackagesListACK().clear();
        //___________________________________________________________________________________Send SYN 
        Package SYN = new Package(12345, 0, (short) 0, false, true, false);
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
            pktReceived = sendACKHS(client, pktSYNACKReceived, receive);
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
