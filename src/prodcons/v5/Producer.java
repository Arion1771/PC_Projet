package prodcons.v5;

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
        this.mess=new Message(getName(), 0);
    }

    @Override
    public void run() {
        int i=0;
        while (i<nbMess) {
            this.mess=new Message("Message from Thread "+this.getId(), i);
            try {
                this.buff.put(mess);
            } catch (InterruptedException e) {
                System.out.println("Thread "+this.getId()+" was interrupted while putting message\n");
            }
            try {
                sleep(prodTime);
            } catch (InterruptedException e) {
                System.out.println("Thread "+this.getId()+" was interrupted while sleeping\n");
            }
            System.out.println("Thread "+this.getId()+" sent message: "+mess);
            i++;
        }
        
    }
}
