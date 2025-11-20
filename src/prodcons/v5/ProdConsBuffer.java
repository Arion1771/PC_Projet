package prodcons.v5;

import prodcons.IProdConsBuffer;
import prodcons.Message;


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
    public synchronized  prodcons.Message get() throws InterruptedException {
        while(nempty == Bufs){
            wait();
        }
        Message m = buffer[nc%Bufs];
        nc++;
        nempty++;
        nfull--;
        notifyAll();
        return m;
    }

    @Override
    public synchronized Message[] get(int k) throws InterruptedException {
        int i = 0;
        Message [] m = new Message[k] ;
        while (i < k) {
            while(nempty == Bufs){
                    wait();
            }
            m[i] = buffer[nc%Bufs];
            nc++;
            nempty++;
            nfull--;
            i++;
        }
        notifyAll();
        return m;
    }
}
