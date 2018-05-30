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
public class TimeOut extends Thread {

    public double time;
    public boolean timeout;
    boolean cond = true;
    public int lenghtTime;

    public TimeOut(int time) {
        this.lenghtTime = time;
        this.start();
    }

    @Override
    public void run() {


            while (cond) {
                try {
                    time++;
                    Thread.sleep(1);
                } catch (InterruptedException ex) {
                    System.out.println("InterruptionException Thread TimeOut. cod = 18");
                }
                if(time >= lenghtTime){
                    timeout = true;
                    cond = false;
                }else{
                    timeout = false;
                }
                
            }
        

    }

    public void inicialize() {
        System.out.println("Inicializado");
        time = 0;
        cond = true;
        

    }
    public void setTime(int time){
        this.lenghtTime = time;
    }
    public void stopTime() {
        System.out.println("Finalizado");
        cond = false;
        time = 0;
    }

}
