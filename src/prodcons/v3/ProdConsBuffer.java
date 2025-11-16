package prodcons.v3;

import java.util.concurrent.Semaphore;
import prodcons.IProdConsBuffer;
import prodcons.Message;

public class ProdConsBuffer implements IProdConsBuffer{
    int size;
    int NbProducers = 0;
    int NbMsg = 0;
    private Message[] buffer;
    private int np;
    private int nc;
    int count=0;
    Semaphore empty;
    Semaphore full;
    Semaphore mutex = new Semaphore(1);

    public ProdConsBuffer(int bufSz) {
        this.size = bufSz;
        this.empty = new Semaphore(bufSz);
        this.full = new Semaphore(0);
        this.buffer = new Message[bufSz];
        this.np = 0;
        this.nc = 0;
    }

    @Override
    public void put(Message m){
        try {
            empty.acquire();
            mutex.acquire();
            this.buffer[np] = m;
            np = (np + 1) % size;
            NbMsg++;
            count++;
            mutex.release();
            full.release();
        } catch (InterruptedException e) {
            System.out.println("Thread " + Thread.currentThread().getId() + " was interrupted\n");
        }
    }

    @Override
    public Message get() {
        Message message = null;
        try {
            full.acquire();
            mutex.acquire();
            message = this.buffer[nc];
            count--;
            nc = (nc + 1) % size;
            mutex.release();
            empty.release();
        } catch (InterruptedException e) {
            System.out.println("Thread " + Thread.currentThread().getId() + " was interrupted\n");
        }
        return message;
    }

 @Override
public int nmsg() {
    try {
        mutex.acquire();
        return count;
    } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        return count;
    } finally {
        mutex.release();
    }
}


    @Override
    public int totmsg() {
        return NbMsg;
    }

    public void RegisterProducer(int nbProducers) {

        this.NbProducers = nbProducers;
    }

    public void UnregisterProducer() {
        try {
            mutex.acquire();
            this.NbProducers--;
           
        } catch (InterruptedException ex) {
        }
        finally {mutex.release();}
    }

    public boolean NoMoreProducers() {
        try {
            mutex.acquire();
        
         
        return this.NbProducers == 0;
    }
         catch (InterruptedException ex) {
            return false;
        }
        
        finally {mutex.release();}     
    }

}
