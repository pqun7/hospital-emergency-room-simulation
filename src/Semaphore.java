
// ==========================================
// 1. CUSTOM SEMAPHORE
// ==========================================

class Semaphore {

    private int value;

    public Semaphore(int initialValue) {
        value = initialValue;
    }

    // P operation
    public synchronized void P() throws InterruptedException {

        while (value == 0) {
            wait();
        }

        value--;
    }

    // V operation
    public synchronized void V() {

        value++;

        notifyAll();
    }

    // Try to acquire a resource within a time limit
    public synchronized boolean tryAcquire(long timeout)
            throws InterruptedException {

        long deadline = System.nanoTime()
                + timeout * 1_000_000L;

        while (value == 0) {

            long remaining = deadline - System.nanoTime();

            if (remaining <= 0) {
                return false;
            }

            long millis = remaining / 1_000_000L;
            int nanos = (int) (remaining % 1_000_000L);

            wait(millis, nanos);
        }

        value--;

        return true;
    }
}

