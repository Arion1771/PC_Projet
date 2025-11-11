package prodcons.v1;

import java.io.IOException;
import java.util.Properties;
import java.util.Random;

import prodcons.Message;

public class TestProdCons {

    public static void main(String[] args) {
        Random rand = new Random();
        Properties properties = new Properties();
        System.out.println("Chemin trouvé : " + 
        TestProdCons.class.getClassLoader().getResource("prodcons/v1/options.xml"));
        try {
            properties.loadFromXML(
            TestProdCons.class.getClassLoader().getResourceAsStream("prodcons/v1/options.xml"));
        } catch (IOException e) {
            System.err.println("Error loading options.xml");
            e.printStackTrace();
        }
        int nProd = Integer.parseInt(properties.getProperty("nProd"));
        int nCons = Integer.parseInt(properties.getProperty("nCons"));
        int bufSz = Integer.parseInt(properties.getProperty("bufSz"));
        int prodTime = Integer.parseInt(properties.getProperty("prodTime"));
        int consTime = Integer.parseInt(properties.getProperty("consTime"));
        int minProd = Integer.parseInt(properties.getProperty("minProd"));
        int maxProd = Integer.parseInt(properties.getProperty("maxProd"));

        ProdConsBuffer buffer = new ProdConsBuffer(bufSz);

        while (nCons >0) {
            Consumer c = new Consumer(buffer,consTime);
            c.start();
            nCons--;
        }

        while (nProd >0) {
            int nMsg = rand.nextInt((maxProd - minProd) + 1) + minProd;
            Producer p = new Producer(buffer,prodTime,nMsg);
            p.start();
            nProd--;
        }
    }

}
