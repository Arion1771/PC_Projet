package prodcons.v1;

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

    public ProdConsBuffer() {
        Properties properties = new Properties();
    try {
        properties.loadFromXML(
        TestProdCons.class.getClassLoader().getResourceAsStream("options.xml"));
    } catch (IOException e) {
        System.err.println("Error loading options.xml");
        e.printStackTrace();
    }
    Bufs = Integer.parseInt(properties.getProperty("Bufsz"));
    buffer = new Message[Bufs];
    
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
        }
        Message m = buffer[nc%Bufs];
        nc++;
        nempty++;
        nfull--;
        notifyAll();
        return m;
    }


}
