package prodcons.v1;

import prodcons.Message;

public class Producer extends Thread {
    Message mess;
    ProdConsBuffer buff;
    int prodTime;
    int nbMess;

    public Producer (ProdConsBuffer b, int time, int nbmess) {
        this.buff=b;
        this.prodTime=time;
        this.nbMess=nbmess;
    }

    @Override
    public void run() {
        while (nbMess>0) {
            try {
                this.buff.put(mess);
            } catch (InterruptedException e) {
                System.out.println("Thread "+this.getId()+" was interrupted\n");
            }
            try {
                sleep(prodTime);
            } catch (InterruptedException e) {
                System.out.println("Thread "+this.getId()+" was interrupted while sleeping\n");
            }
            System.out.println("Thread "+this.getId()+" sent message: "+mess);
            nbMess--;
        }
        
    }
    

}
