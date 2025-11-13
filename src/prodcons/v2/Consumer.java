package prodcons.v2;
import prodcons.Message;
public class Consumer extends Thread {
    
    ProdConsBuffer buff;
    Message msg;
    int consTime;

    public Consumer (ProdConsBuffer b, int time) {
        this.buff=b;
        this.consTime=time;
    }

    @Override
    public void run() {
        while (true) {
            if (buff.NoMoreProducers() && buff.nmsg() <= 0) {
                break;
            }
            try {
                msg = this.buff.get();
            } catch (InterruptedException e) {
                System.out.println("Thread " + this.getId() + " was interrupted\n");
            }
            try {
                sleep(consTime);
            } catch (InterruptedException e) {
                System.out.println("Thread " + this.getId() + " was interrupted while sleeping\n");
            }
            System.out.println("Thread " + this.getId() + " received message: " + msg);
        }
    }
}
