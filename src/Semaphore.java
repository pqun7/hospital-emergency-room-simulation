public class Semaphore {
    private int value;

    public Semaphore(int initial) {
        this.value = initial;
    }

    public synchronized void acquire() throws InterruptedException {
        while (value <= 0) {
            wait();
        }
        value--;
    }

    public synchronized boolean tryAcquire(long timeoutMillis) throws InterruptedException {
        long expireTime = System.currentTimeMillis() + timeoutMillis;
        while (value <= 0) {
            long remaining = expireTime - System.currentTimeMillis();
            if (remaining <= 0) {
                return false;
            }
            wait(remaining);
        }
        value--;
        return true;
    }

    public synchronized void release() {
        value++;
        notifyAll();
    }
}