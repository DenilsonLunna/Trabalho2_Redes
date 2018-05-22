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
public class TestClass {
    public static void main(String[] args) {
        Package pkt = new Package();
        ConvertClass c = new ConvertClass();
        
        byte[] b = c.convertPackageToByte(pkt);
        System.out.println(b.length);
        
    }
}
