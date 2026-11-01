public class Semaphore {

    private int permits;

    public Semaphore(int permits) {
        if (permits < 0) {
            throw new IllegalArgumentException("Semaphore permits cannot be negative.");
        }
        this.permits = permits;
    }

    public synchronized void acquire() throws InterruptedException {
        while (permits == 0) {
            // Wait until a permit is released.
            wait();
        }
        permits--;
    }

    public synchronized void release() {
        permits++;
        // Wake threads waiting for a permit.
        notifyAll();
    }

    public synchronized boolean tryAcquire(long timeoutMilliseconds)
            throws InterruptedException {
        if (timeoutMilliseconds < 0) {
            throw new IllegalArgumentException("Timeout cannot be negative.");
        }

        long deadline = System.nanoTime()
                + timeoutMilliseconds * 1_000_000L;

        while (permits == 0) {
            long remaining = deadline - System.nanoTime();
            if (remaining <= 0) {
                // The timeout expired before a permit became available.
                return false;
            }

            long milliseconds = remaining / 1_000_000L;
            int nanoseconds = (int) (remaining % 1_000_000L);
            if (milliseconds == 0 && nanoseconds == 0) {
                return false;
            }
            wait(milliseconds, nanoseconds);
        }

        permits--;
        return true;
    }
}
