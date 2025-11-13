package prodcons.v3;

import prodcons.Message;

import java.util.concurrent.Semaphore;

import prodcons.IProdConsBuffer;

public class ProdConsBuffer implements IProdConsBuffer{
    int size;
    int NbProducers = 0;
    int NbMsg = 0;
    Semaphore empty = new Semaphore(0);
    Semaphore full = new Semaphore(0);
    Semaphore mutex = new Semaphore(1);

    public ProdConsBuffer(int bufSz) {
        this.size = bufSz;
    }

    @Override
    public void put(Message m) throws InterruptedException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'put'");
    }

    @Override
    public Message get() throws InterruptedException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'get'");
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
