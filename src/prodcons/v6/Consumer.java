package prodcons.v6;
import java.util.Random;

import prodcons.Message;
public class Consumer extends Thread {
    
    ProdConsBuffer buff;
    Message msg;
    Message[] mult_msg;
    int consTime;
    Random rand;

    public Consumer (ProdConsBuffer b, int time) {
        this.buff=b;
        this.consTime=time;
        this.rand=new Random();
    }

    @Override
    public void run() {
        while (true) {
            try {
                mult_msg = this.buff.get(rand.nextInt(6));
            } catch (InterruptedException e) {
                System.out.println("Thread " + this.getId() + " was interrupted while getting message\n");
            }

            for (Message msg : mult_msg) {
                if (msg.getID()==-1) {
                    System.out.println("Thread " + this.getId() + " exiting: no more producers and no more messages\n");
                    return;
                }
                else {
                    System.out.println("Thread " + this.getId() + " received message: " + msg);
                }
            }
            
            try {
                sleep(consTime);
            } catch (InterruptedException e) {
                System.out.println("Thread " + this.getId() + " was interrupted while sleeping\n");
            }
            
        }
    }
}
