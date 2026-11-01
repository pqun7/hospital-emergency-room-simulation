public class TreatmentRoom {

    // The assignment requires exactly two shared treatment rooms.
    private static final int NUMBER_OF_ROOMS = 2;
    private final Semaphore availableRooms = new Semaphore(NUMBER_OF_ROOMS);

    public boolean tryAcquire(long timeoutMilliseconds)
            throws InterruptedException {
        return availableRooms.tryAcquire(timeoutMilliseconds);
    }

    public void release() {
        availableRooms.release();
    }
}
