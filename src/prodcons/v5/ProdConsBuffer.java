package prodcons.v5;

public class ProdConsBuffer implements IProdConsBuffer{

    private int Bufs;
    private Message[] buffer;
    private int np;
    private int nc;
    private int nmsg;
    private int nempty;
    private int nfull;

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
    public synchronized void put(Message m) throws InterruptedException {
       while(nfull == Bufs){
        wait();
       }
         buffer[np%Bufs] = m;
         np++;
         if (m.getID() != -1) {   // ne compte que les vrais messages
        nmsg++;
    }
         nempty--;
         nfull++;
         notifyAll();
    }

    @Override
    public synchronized  Message get() throws InterruptedException {
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
        while (nfull == 0) {
            wait();
        }
        int toRead = Math.min(k, nfull);
        Message[] res = new Message[toRead];

        for (int i = 0; i < toRead; i++) {
            // ici on sait qu'il reste au moins 1 message
            res[i] = buffer[nc % Bufs];
            nc++;
            nempty++;
        nfull--;
        }
        notifyAll();
        return res;
    }
}



   

