import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class TreatmentRoom {

    private static final int NUMBER_OF_ROOMS = 2;

    private final Semaphore availableRooms = new Semaphore(NUMBER_OF_ROOMS, true);

    public boolean tryAcquire(long timeoutMilliseconds)
            throws InterruptedException {

        return availableRooms.tryAcquire(
                timeoutMilliseconds,
                TimeUnit.MILLISECONDS);
    }

    public void release() {
        availableRooms.release();
    }
}
