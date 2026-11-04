import java.util.Random;

public class Doctor extends Thread {
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

                Hospital.log("Doctor " + id + " is assigned to P" + patient.getPatientId() + ".");
                Hospital.log("Doctor " + id + " is trying to acquire a treatment room.");

                // Wait up to 3 seconds to acquire a treatment room
                boolean acquired = hospital.getTreatmentRoom().tryAcquire(3000);

                if (!acquired) {
                    Hospital.log("Doctor " + id + " could not acquire a treatment room.");
                    Hospital.log("Doctor " + id + " is sending P" + patient.getPatientId() + " back to the waiting room.");
                    hospital.getWaitingRoom().enter(patient);
                    continue;
                }

                try {
                    Hospital.log("Doctor " + id + " acquired a treatment room.");
                    Hospital.log("Doctor " + id + " begins treating P" + patient.getPatientId() + ".");

                    // Treatment duration longer than 3s to guarantee timeouts when all rooms are busy
                    Thread.sleep(3500 + random.nextInt(1000));

                    Hospital.log("Doctor " + id + " finished treating P" + patient.getPatientId() + ".");
                } finally {
                    hospital.getTreatmentRoom().release();
                    Hospital.log("Doctor " + id + " released the treatment room.");
                    Hospital.log("Doctor " + id + " is now available.");
                    Hospital.log("P" + patient.getPatientId() + " left.");
                    hospital.patientFinished();
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}