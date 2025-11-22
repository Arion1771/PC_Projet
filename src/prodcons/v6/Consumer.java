package prodcons.v6;

public class Consumer extends Thread {
    
    ProdConsBuffer buff;
    Message msg;
    int consTime;
    int id_prev;

    public Consumer (ProdConsBuffer b, int time) {
        this.buff=b;
        this.consTime=time;
        this.id_prev = -1;
    }

    @Override
    public void run() {
        while (true) {
            try {
                msg=this.buff.get();
            } catch (InterruptedException e) {
                System.out.println("Thread " + this.getId() + " was interrupted while getting message\n");
            }
            if (msg.getID()==-1) {
                System.out.println("Thread " + this.getId() + " exiting: no more producers and no more messages\n");
                return;
            } else {
                    System.out.println("Thread " + this.getId() + " received message: " + msg);
            }
            
                try {       
                sleep(consTime);
            } catch (InterruptedException e) {
                System.out.println("Thread " + this.getId() + " was interrupted while sleeping\n");
            }
            
        }
    }
}

