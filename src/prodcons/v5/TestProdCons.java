package prodcons.v5;

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
        

         Consumer[] consumers = new Consumer[nCons];
    for (int i = 0; i < nCons; i++) {
        consumers[i] = new Consumer(buffer, consTime);
        consumers[i].start();
    }

        Producer[] producers = new Producer[nProd];
    int totalMsg = 0;

    for (int i = 0; i < nProd; i++) {
        int nMsg = rand.nextInt((maxProd - minProd) + 1) + minProd;
        totalMsg += nMsg;
        producers[i] = new Producer(buffer, prodTime, nMsg);
        producers[i].start();
    }

    for (int i = 0; i < nProd; i++) {
        try {
            producers[i].join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }



    for (int i = 0; i < nCons; i++) {
        try {
            buffer.put(new Message("END", -1));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    for (int i = 0; i < nCons; i++) {
        try {
            consumers[i].join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
        
        assert (totalMsg==buffer.totmsg());
    }
}

