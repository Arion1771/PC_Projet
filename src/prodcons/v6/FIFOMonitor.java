package prodcons.v6;

import java.util.ArrayList;
import java.util.List;

public class FIFOMonitor {

    private static final List<String> requestOrder = new ArrayList<>();
    private static final List<String> enterOrder   = new ArrayList<>();

    // appelé AVANT l'appel à put (dans le thread producteur)
    public static synchronized void onRequest(String threadName) {
        requestOrder.add(threadName);
    }

    // appelé au moment où le thread ENTRE dans la section critique de put
    public static synchronized void onEnter(String threadName) {
        enterOrder.add(threadName);
    }

    // vérifie si l'ordre des entrées == ordre des demandes
    public static synchronized boolean isFIFO() {
        if (requestOrder.size() != enterOrder.size()) {
            return false;
        }
        for (int i = 0; i < requestOrder.size(); i++) {
            if (!requestOrder.get(i).equals(enterOrder.get(i))) {
                return false;
            }
        }
        return true;
    }

    public static synchronized void reset() {
        requestOrder.clear();
        enterOrder.clear();
    }

    public static synchronized void printOrders() {
        System.out.println("Ordre des demandes  : " + requestOrder);
        System.out.println("Ordre des entrées   : " + enterOrder);
    }
}
