package prodcons.v1;

import java.io.IOException;
import java.util.Properties;
import java.util.Random;

import prodcons.Message;

public class TestProdCons {

    public static void main(String[] args) {
        Random rand = new Random();
        Properties properties = new Properties();
        try {
            properties.loadFromXML(
            TestProdCons.class.getClassLoader().getResourceAsStream("options.xml"));
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

        ProdConsBuffer buffer = new ProdConsBuffer();

        while (nCons >0) {
            Consumer c = new Consumer(buffer,consTime);
            c.start();
            nCons--;
        }
    }

}
