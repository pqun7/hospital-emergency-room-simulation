public class TreatmentRoom {
    // Only two treatment rooms exist in the department
    private final Semaphore rooms = new Semaphore(2);

    public boolean tryAcquire(long timeoutMilliseconds) throws InterruptedException {
        return rooms.tryAcquire(timeoutMilliseconds);
    }

    public void release() {
        rooms.release();
    }
}