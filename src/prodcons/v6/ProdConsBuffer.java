    package prodcons.v6;

    import java.util.concurrent.locks.Condition;
    import java.util.concurrent.locks.ReentrantLock;

    public class ProdConsBuffer implements IProdConsBuffer {

        // ----------- Classe Cell inchangée -----------
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

        // ----------- Champs du buffer -----------
        private final int Bufs;
        private final Cell[] buffer;

        private int np;
        private int nc;
        private int nmsg;
        private int nfull;
        private int totmsg;

        // 🔑 Lock global équitable + conditions
        private final ReentrantLock lock = new ReentrantLock(true); // fair = FIFO
        private final Condition notFull  = lock.newCondition();
        private final Condition notEmpty = lock.newCondition();

        public ProdConsBuffer(int bufs) {
            this.Bufs = bufs;
            this.buffer = new Cell[Bufs];

            this.nfull = 0;
            this.np = 0;
            this.nc = 0;
            this.totmsg = 0;
            this.nmsg = 0;
        }

        @Override
        public int nmsg() {
            lock.lock();
            try {
                return nmsg;
            } finally {
                lock.unlock();
            }
        }

        @Override
        public int totmsg() {
            lock.lock();
            try {
                return totmsg;
            } finally {
                lock.unlock();
            }
        }

        @Override
        public void put(Message m) throws InterruptedException {
            put(1, m);
        }

        @Override
        public void put(int n, Message m) throws InterruptedException {
            // On crée la Cell dès maintenant
            Cell c = new Cell(n, m);

            // 1) On insère la Cell dans le buffer sous lock global "fair"
            lock.lock();
            try {
                TestProdCons.onThreadEnter(Thread.currentThread().getName());
                while (nfull == Bufs) {
                    notFull.await();
                }

                buffer[np] = c;
                np = (np + 1) % Bufs;

                nfull++;
                nmsg  += n;
                totmsg += n;

                // on réveille les consommateurs potentiels
                notEmpty.signalAll();
            } finally {
                lock.unlock();
            }

            // 2) Le producteur attend que TOUS les exemplaires soient consommés
            //    (hors du lock global pour ne pas bloquer les autres threads)
            c.waitAllConsumed();
        }

        @Override
        public Message get() throws InterruptedException {
            return get(1)[0];
        }

        @Override
        public Message[] get(int k) throws InterruptedException {
            Message[] msgs = new Message[k];
            int i = 0;

            lock.lock();
            try {
                while (i < k) {
                    // attendre qu'il y ait au moins un message à consommer
                    while (nmsg == 0) {
                        notEmpty.await();
                    }

                    Cell c = buffer[nc];

                    // Case vide → on passe à la suivante sans toucher nfull
                    if (c == null) {
                        nc = (nc + 1) % Bufs;
                        continue;
                    }

                    // Cell présente mais déjà entièrement consommée → on la libère
                    if (c.all_consumed()) {
                        buffer[nc] = null;
                        nfull--;
                        notFull.signal(); // un producteur peut se réveiller
                        nc = (nc + 1) % Bufs;
                        continue;
                    }

                    // On consomme autant qu'on peut dans CETTE cellule
                    int rest_a_lire = k - i;
                    int possible_sur_cette_cellule = Math.min(rest_a_lire, c.nb_restant);

                    for (int j = 0; j < possible_sur_cette_cellule; j++) {
                        msgs[i] = c.consumeOne();
                        i++;
                        nmsg-=k;
                    }

                    // Si la cellule est entièrement consommée, on la libère
                    if (c.all_consumed()) {
                        buffer[nc] = null;
                        nfull--;
                        notFull.signal(); // de la place pour les producteurs
                        nc = (nc + 1) % Bufs;
                    }
                }
                return msgs;
            } finally {
                lock.unlock();
            }
        }
    }
