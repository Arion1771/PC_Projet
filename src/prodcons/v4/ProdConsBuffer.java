package prodcons.v4;

import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Condition;
import prodcons.IProdConsBuffer;
import prodcons.Message;

public class ProdConsBuffer implements IProdConsBuffer{
    ReentrantLock lock = new ReentrantLock();
    Condition notFull = lock.newCondition();
    Condition notEmpty = lock.newCondition();
    int size;
    int NbProducers = 0;
    int NbMsg = 0;
    private Message[] buffer;
    private int np;
    private int nc;
    int count=0;
    

    public ProdConsBuffer(int bufSz) {
        this.size = bufSz;
       
        this.buffer = new Message[bufSz];
        this.np = 0;
        this.nc = 0;
    }

    @Override
    public void put(Message m){
         lock.lock();
         try {
          while(count == size){
                notFull.await();
            
              }
            this.buffer[np] = m;
            np = (np + 1) % size;
            NbMsg++;
            count++;
            notEmpty.signalAll();
         } catch (InterruptedException e) {
          System.out.println("Thread " + Thread.currentThread().getId() + " was interrupted\n");
         } finally {
          lock.unlock();
          return;
         }
        
    }

    @Override
    public Message get() {
       
        
    }

 @Override
public int nmsg() {
    
}


    @Override
    public int totmsg() {
        return NbMsg;
    }

    public void RegisterProducer(int nbProducers) {

        this.NbProducers = nbProducers;
    }

    public void UnregisterProducer() {
        
    }

    public boolean NoMoreProducers() {
        
            
    }

}
