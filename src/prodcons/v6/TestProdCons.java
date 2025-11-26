package prodcons.v6;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

public class TestProdCons {

    // --------- Moniteur FIFO pour les threads producteurs ---------
    private static final List<String> requestOrder = new ArrayList<>();
    private static final List<String> enterOrder   = new ArrayList<>();

    // appelé par les threads AVANT l'appel à put()
    public static synchronized void onThreadRequest(String threadName) {
        requestOrder.add(threadName);
    }

    // appelé depuis ProdConsBuffer.put(...) au moment d'entrer en section critique
    public static synchronized void onThreadEnter(String threadName) {
        enterOrder.add(threadName);
    }

    private static synchronized void resetFifoMonitor() {
        requestOrder.clear();
        enterOrder.clear();
    }

    private static synchronized boolean isFifoThreads() {
        if (requestOrder.size() != enterOrder.size()) return false;
        for (int i = 0; i < requestOrder.size(); i++) {
            if (!requestOrder.get(i).equals(enterOrder.get(i))) {
                return false;
            }
        }
        return true;
    }

    private static synchronized void printFifoOrders() {
        System.out.println("Ordre des demandes  : " + requestOrder);
        System.out.println("Ordre des entrées   : " + enterOrder);
    }

    // ---------------------------------------------------------------
    //                      main : test global
    // ---------------------------------------------------------------
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
        int nProd    = Integer.parseInt(properties.getProperty("nProd"));
        int nCons    = Integer.parseInt(properties.getProperty("nCons"));
        int bufSz    = Integer.parseInt(properties.getProperty("bufSz"));
        int prodTime = Integer.parseInt(properties.getProperty("prodTime"));
        int consTime = Integer.parseInt(properties.getProperty("consTime"));
        int minProd  = Integer.parseInt(properties.getProperty("minProd"));
        int maxProd  = Integer.parseInt(properties.getProperty("maxProd"));

        System.out.printf("""
            Configuration :
              - Producteurs : %d
              - Consommateurs : %d
              - Taille du buffer : %d
              - prodTime = %d ms, consTime = %d ms
              - Messages/producer : [%d ; %d]
            %n""",
            nProd, nCons, bufSz, prodTime, consTime, minProd, maxProd);

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
            assert (totalMsg == totMsgBuffer) : "Incohérence: totalMsg != buffer.totmsg()";

            System.out.println("Test global OK ✅");
        }

        // ---------- Test supplémentaire : FIFO des threads producteurs ----------
        testThreadFIFO();
    }

    // ---------------------------------------------------------------
    //              Test : FIFO des threads producteurs
    // ---------------------------------------------------------------
    private static void testThreadFIFO() {
    System.out.println("\n=== Test FIFO des threads producteurs ===");

    resetFifoMonitor();

    final int N = 5;
    final ProdConsBuffer buf = new ProdConsBuffer(1); // petit buffer pour forcer la contention
    final CountDownLatch startGate = new CountDownLatch(1);

    Thread[] threads = new Thread[N];

    // Producteurs
    for (int i = 0; i < N; i++) {
        final int id = i;
        threads[i] = new Thread(() -> {
            try {
                startGate.await();

                String name = Thread.currentThread().getName();
                // on enregistre l'ordre de demande
                onThreadRequest(name);

                // un seul put par thread
                buf.put(new Message("T" + id, id));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "P" + i);
        threads[i].start();
    }

    // Consommateur qui va consommer exactement N messages
    Thread consumer = new Thread(() -> {
        try {
            for (int i = 0; i < N; i++) {
                buf.get(); // on se fiche du contenu ici, on veut juste débloquer les producteurs
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }, "C");
    consumer.start();

    // on lance tous les producteurs en même temps
    startGate.countDown();

    // on attend la fin des producteurs
    for (Thread t : threads) {
        try {
            t.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // on attend aussi le consommateur
    try {
        consumer.join();
    } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
    }

    // affichage + verdict
    printFifoOrders();
    boolean fifo = isFifoThreads();
    System.out.println("FIFO threads = " + fifo);
    assert fifo : "Accès non FIFO au buffer pour les threads producteurs";
}

}
