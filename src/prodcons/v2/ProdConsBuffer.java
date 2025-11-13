package prodcons.v2;

import java.io.IOException;
import java.util.Properties;
import prodcons.IProdConsBuffer;
import prodcons.Message;
import prodcons.TestProdCons;

public class ProdConsBuffer implements IProdConsBuffer{

    private int Bufs;
    private Message[] buffer;
    private int np;
    private int nc;
    private int nmsg;
    private int nempty;
    private int nfull;
    private int activeProducers = 0;

    public ProdConsBuffer(int bufs) {
        Bufs = bufs;
        buffer = new Message[Bufs];
        nempty = Bufs;
        nfull = 0;
        np = 0;
        nc = 0;
    }

    @Override
    public int nmsg() {
        return nfull;
    }

    @Override
    public int totmsg() {
        return nmsg;
    }

    @Override
    public synchronized void put(prodcons.Message m) throws InterruptedException {
       while(nfull == Bufs){
        wait();
       }
         buffer[np%Bufs] = m;
         np++;
         nmsg++;
         nempty--;
         nfull++;
         notifyAll();
    }

    @Override
    public synchronized prodcons.Message get() throws InterruptedException {
        while(nempty == Bufs){
            wait();
            if (NoMoreProducers() && nmsg() <= 0) {
                break;
            }
        }
        Message m = buffer[nc%Bufs];
        nc++;
        nempty++;
        nfull--;
        notifyAll();
        return m;
    }

    public synchronized void RegisterProducer( int nbProducers ) {
        activeProducers += nbProducers;
    }

    public synchronized void UnregisterProducer() {
        activeProducers--;
        if (activeProducers == 0) {
            notifyAll();
        }
    }

    public synchronized boolean NoMoreProducers() {
        return activeProducers <= 0;
    }
}
