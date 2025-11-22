package prodcons.v6;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ProdConsBuffer implements IProdConsBuffer{

    private class Cell {
        Message message;
        int nb_msg;      // nb initial (optionnel, pour info)
        int nb_restant;  // nb restant à consommer

        final ReentrantLock lock;
        final Condition allconsumed_p; // pour le producteur qui a créé cette cellule

        public Cell(int n, Message m) {
            this.nb_msg = n;
            this.message = m;
            this.nb_restant = n;
            
            this.lock = new ReentrantLock();
            this.allconsumed_p = lock.newCondition();
        }

        boolean all_consumed() {
            return nb_restant == 0;
        }

        /** Appelé côté consommateur : consomme 1 exemplaire du message */
        Message consumeOne() {
            lock.lock();
            try {
                nb_restant--;
                if (nb_restant == 0) {
                    // on réveille le producteur qui attend la fin de la conso
                    allconsumed_p.signalAll();
                }
                return message;
            } finally {
                lock.unlock();
            }
        }

        /** Appelé par le producteur après avoir mis la Cell dans le buffer */
        void waitAllConsumed() throws InterruptedException {
            lock.lock();
            try {
                while (!all_consumed()) {
                    allconsumed_p.await();
                }
            } finally {
                lock.unlock();
            }
        }
    
}

    

    private int Bufs;
    private Cell[] buffer;
    
    private int np;
    private int nc;
    private int nmsg;
    private int nfull;
    private int totmsg;
    

    public ProdConsBuffer(int bufs) {
        Bufs = bufs;
        buffer = new Cell[Bufs];
        
        nfull = 0;
        np = 0;
        nc = 0;
        totmsg = 0;
        nmsg = 0;
        
    }

    @Override
    public int nmsg() {
        return nmsg;
    }

    @Override
    public int totmsg() {
        return totmsg;
    }

    @Override
    public void put(Message m) throws InterruptedException {
      put(1,m);

    }

    @Override
    public void put(int n, Message m) throws InterruptedException {
        Cell c;
        // 1) On insère la Cell dans le buffer (zone critique : synchronized(this))
        synchronized (this) {
            while (nfull == Bufs) {
                wait();
            }

            c = new Cell(n, m);
            buffer[np % Bufs] = c;
            np = (np + 1) % Bufs;

            nfull++;
            
            nmsg += n;
            totmsg += n;

            // on réveille les consommateurs potentiels
            notifyAll();
        }

        // 2) On attend que TOUS les exemplaires du message soient consommés
        //    IMPORTANT : en dehors du synchronized(this) pour éviter les deadlocks
        c.waitAllConsumed();
        // Quand on sort d'ici, ce producteur pourra produire à nouveau.
    }
        
    

    @Override
    public synchronized  Message get() throws InterruptedException {
        return get(1)[0];
    }

    @Override
    public synchronized Message[] get(int k) throws InterruptedException {
        Message[] msgs = new Message[k];
        int i = 0;

        while (i < k) {
            // attendre qu'il y ait au moins un message à consommer
            while (nmsg == 0) {
                wait();
            }

            Cell c = buffer[nc % Bufs];

            // Par sécurité, si cette cellule est vide, on passe à la suivante
            if (c == null || c.all_consumed()) {
                buffer[nc % Bufs] = null;
                nc = (nc + 1) % Bufs;
                nfull--;
                
                notifyAll(); // des producteurs peuvent se réveiller
                continue;
            }

            // On consomme autant qu'on peut dans CETTE cellule
            // -> on ne passe à la suivante que quand tous ses exemplaires sont consommés
            int rest_a_lire = k - i;
            int possible_sur_cette_cellule = Math.min(rest_a_lire, c.nb_restant);

            for (int j = 0; j < possible_sur_cette_cellule; j++) {
                msgs[i] = c.consumeOne();
                i++;
                nmsg--;
                }

            // Si la cellule est entièrement consommée, on avance nc
            if (c.all_consumed()) {
                buffer[nc % Bufs] = null;
                nc = (nc + 1) % Bufs;
                nfull--;
                
                notifyAll(); // réveille les producteurs si besoin
            }
        }

        return msgs;
    }
}

            

