package prodcons.v1;

import java.io.IOException;
import java.util.Properties;
import prodcons.IProdConsBuffer;
import prodcons.Message;
import prodcons.TestProdCons;

public class ProdConsBuffer implements IProdConsBuffer{


    public ProdConsBuffer() {
        Properties properties = new Properties();
    try {
        properties.loadFromXML(
        TestProdCons.class.getClassLoader().getResourceAsStream("options.xml"));
    } catch (IOException e) {
        // TODO Auto-generated catch block
        System.err.println("Error loading options.xml");
        e.printStackTrace();
    }
    int nProd = Integer.parseInt(properties.getProperty("nProd"));
    int nCons = Integer.parseInt(properties.getProperty("nCons"));
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


}
