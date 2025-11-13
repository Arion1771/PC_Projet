package prodcons.v3;
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
            msg = this.buff.get();
            try {
                sleep(consTime);
            } catch (InterruptedException e) {
                System.out.println("Thread " + this.getId() + " was interrupted while sleeping\n");
            }
            System.out.println("Thread " + this.getId() + " received message: " + msg);
        }
    }
}
