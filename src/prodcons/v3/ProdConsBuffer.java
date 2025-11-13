package prodcons.v3;

import prodcons.Message;

import java.util.concurrent.Semaphore;

import prodcons.IProdConsBuffer;

public class ProdConsBuffer implements IProdConsBuffer{
    int size;
    int NbProducers = 0;
    int NbMsg = 0;
    private Message[] buffer;
    private int np;
    private int nc;
    Message message = new Message("", 0);
    Semaphore empty;
    Semaphore full;
    Semaphore mutex = new Semaphore(1);

    public ProdConsBuffer(int bufSz) {
        this.size = bufSz;
        this.empty = new Semaphore(bufSz);
        this.full = new Semaphore(0);
    }

    @Override
    public void put(Message m){
        try {
            empty.acquire();
            mutex.acquire();
            this.buffer[np] = m;
            np = (np + 1) % size;
            NbMsg++;
            mutex.release();
            full.release();
        } catch (InterruptedException e) {
            System.out.println("Thread " + Thread.currentThread().getId() + " was interrupted\n");
        }
    }

    @Override
    public Message get() {
        try {
            full.acquire();
            mutex.acquire();
            this.message = this.buffer[nc];
            nc = (nc + 1) % size;
            mutex.release();
            empty.release();
        } catch (InterruptedException e) {
            System.out.println("Thread " + Thread.currentThread().getId() + " was interrupted\n");
        }
        return this.message;
    }

    @Override
    public int nmsg() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'nmsg'");
    }

    @Override
    public int totmsg() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'totmsg'");
    }

    public void RegisterProducer(int nbProducers) {
        this.NbProducers = nbProducers;
    }

    public void UnregisterProducer() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'UnregisterProducer'");
    }

    public boolean NoMoreProducers() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'NoMoreProducers'");
    }

}
