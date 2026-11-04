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

                Patient patient = hospital.getWaitingRoom().take(id);

                if (patient == STOP) {
                    return;
                }

                Hospital.logEvent(
                        "Doctor " + id + " is assigned to Patient "
                                + patient.getPatientId() + ".");

                boolean acquired = hospital.getTreatmentRoom().tryAcquire(3000);

                // Keep waiting until a treatment room becomes available.
                while (!acquired) {

                    Hospital.logEvent(
                            "Doctor " + id
                                    + " is waiting for a treatment room.");

                    acquired = hospital.getTreatmentRoom().tryAcquire(3000);
                }

                try {

                    Hospital.logEvent(
                            "Doctor " + id
                                    + " acquired a treatment room.");

                    Hospital.logEvent(
                            "Doctor " + id + " begins treating Patient "
                                    + patient.getPatientId() + ".");

                    Thread.sleep(1000 + random.nextInt(2000));

                    Hospital.logEvent(
                            "Doctor " + id + " finishes treating Patient "
                                    + patient.getPatientId() + ".");

                } finally {

                    hospital.getTreatmentRoom().release();

                    Hospital.logEvent(
                            "Doctor " + id
                                    + " releases the treatment room.");

                    Hospital.logEvent(
                            "Doctor " + id + " is now available.");

                    Hospital.logEvent(
                            "Patient " + patient.getPatientId()
                                    + " leaves the hospital.");

                    hospital.patientFinished();
                }
            }

        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
        }
    }
}