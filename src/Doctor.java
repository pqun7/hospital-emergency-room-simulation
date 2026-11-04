import java.util.Random;

public class Doctor extends Thread {

    // tell the doctor that his work is done
    public static final Patient STOP = new Patient(-1, null);

    private final int id;
    private final Hospital hospital;
    private final Random random = new Random();

    public Doctor(int id, Hospital hospital) {
        this.id = id;
        this.hospital = hospital;
    }

    @Override
    public void run() {
        try {
            while (true) {
                Patient patient = hospital.getWaitingRoom().take();
                if (patient == STOP) {
                    return;
                }

                Hospital.log("Doctor " + id + " is assigned to Patient "
                        + patient.getPatientId() + ".");

                Hospital.log("Doctor " + id
                        + " is trying to acquire a treatment room.");
                // Wait no longer than three seconds for a treatment room.
                boolean acquired = hospital.getTreatmentRoom().tryAcquire(3000);
                if (!acquired) {
                    Hospital.log("Doctor " + id
                            + " could not acquire a treatment room.");
                    Hospital.log("Doctor " + id + " returns Patient "
                            + patient.getPatientId() + " to the waiting room.");
                    hospital.getWaitingRoom().enter(patient);
                    continue;
                }

                try {
                    Hospital.log("Doctor " + id + " acquired a treatment room.");
                    Hospital.log("Doctor " + id + " begins treating Patient "
                            + patient.getPatientId() + ".");

                    Thread.sleep(1000 + random.nextInt(2000));

                    Hospital.log("Doctor " + id + " finishes treating Patient "
                            + patient.getPatientId() + ".");
                } finally {
                    // Always return the room, even if treatment is interrupted.
                    hospital.getTreatmentRoom().release();
                    Hospital.log("Doctor " + id
                            + " releases the treatment room.");
                    Hospital.log("Doctor " + id + " is now available.");
                    Hospital.log("Patient " + patient.getPatientId()
                            + " leaves the hospital.");
                    hospital.patientFinished();
                }
            }
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
        }
    }
}
