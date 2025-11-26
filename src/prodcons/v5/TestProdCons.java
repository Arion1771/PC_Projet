package prodcons.v5;

import java.io.IOException;
import java.util.Properties;
import java.util.Random;

public class TestProdCons {

    public static void main(String[] args) {
        Random rand = new Random();
        Properties properties = new Properties();
        System.out.println("Chemin trouvé : " + 
        TestProdCons.class.getClassLoader().getResource("prodcons/options.xml"));
        try {
            properties.loadFromXML(
            TestProdCons.class.getClassLoader().getResourceAsStream("prodcons/options.xml"));
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

        // Lancer les consommateurs
        for (int i = 0; i < nCons; i++) {
            consumers[i] = new Consumer(buffer, consTime);
            consumers[i].start();
        }

        // Lancer les producteurs
        Producer[] producers = new Producer[nProd];
        int totalMsg = 0;
        
        for (int i = 0; i < nProd; i++) {
            int nMsg = rand.nextInt((maxProd - minProd) + 1) + minProd;
            totalMsg += nMsg;
            producers[i] = new Producer(buffer, prodTime, nMsg);
            producers[i].start();
        }
                // Attendre la fin de tous les producteurs
        for (int i = 0; i < nProd; i++) {
            try {
                producers[i].join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        // Envoyer les messages de fin pour les consommateurs
        for (int i = 0; i < nCons; i++) {
            try {
                buffer.put(new Message("END", -1));
                

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // Attendre la fin de tous les consommateurs
        for (int i = 0; i < nCons; i++) {
            try {
                consumers[i].join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        // Résumé principal
        synchronized (System.out) {
            int totMsgBuffer = buffer.totmsg();
            System.out.println("=== Résumé ===");
            System.out.println("Total messages demandés aux producteurs : " + totalMsg);
            System.out.println("Total messages comptés par le buffer   : " + totMsgBuffer);
            System.out.println(totalMsg == totMsgBuffer ? "✅ OK" : "❌ ERREUR");
        }
    }
}

