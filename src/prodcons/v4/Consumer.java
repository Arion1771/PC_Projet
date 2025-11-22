package prodcons.v4;
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
            msg = this.buff.get();
            if (msg.getID()==-1) {
                System.out.println("Thread " + this.getId() + " exiting: no more producers and no more messages\n");
        
                return;
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
