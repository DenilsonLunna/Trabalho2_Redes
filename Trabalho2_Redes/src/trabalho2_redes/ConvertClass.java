/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package trabalho2_redes;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * 
 * @author Sammy Guergachi <sguergachi at gmail.com>
 */
public class ConvertClass {
    static ObjectInputStream ois;
    static ObjectOutputStream ous;
    public static Package convertByteToPackage(byte[] pack) {

        try {
            ByteArrayInputStream bao = new ByteArrayInputStream(pack);
            ois = new ObjectInputStream(bao);
            return (Package) ois.readObject();

        } catch (IOException e) {
            System.out.println("Erro I/O from the transform byte to Package. cod = 12");
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            System.out.println("Erro ClassNotFound in ConvertClass. cod = 13");
        }

        return null;
    }
    public static byte[] convertPackageToByte(Package pkt) {
        try {
            //cria um  array de byte  que irei passar para o objectOutput para retornar o byte[] , 
            //o pacote tem q implementar o Serializable
            ByteArrayOutputStream bao = new ByteArrayOutputStream();
           
            ous = new ObjectOutputStream(bao);
            ous.writeObject(pkt);
            return bao.toByteArray();
        } catch (IOException e) {
            System.out.println("Erro I/O from the transform package to Byte. cod = 14");
        }

        return null;
    }
}
