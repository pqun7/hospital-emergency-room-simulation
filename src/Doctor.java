// ==========================================
// 4. DOCTOR
// ==========================================

class Doctor extends Thread {

    int id;

    Hospital hospital;

    static final Patient STOP = new Patient(-1, null);

    Random random = new Random();

    public Doctor(int id, Hospital hospital) {

        this.id = id;
        this.hospital = hospital;
    }

    @Override
    public void run() {

        try {

            while (true) {

                // Get a patient from the waiting room
                Patient patient = hospital.waitingRoom.take();

                // Stop signal
                if (patient == STOP) {
                    break;
                }

                Hospital.log("Doctor " + id
                        + " is assigned to P" + patient.id + ".");

                Hospital.log("Doctor " + id
                        + " is trying to acquire a treatment room.");

                // Try to acquire a treatment room within 3 seconds
                boolean acquired =
                        hospital.treatmentRooms.tryAcquire(3000);

                if (acquired) {

                    Hospital.log("Doctor " + id
                            + " acquired a treatment room.");

                    Hospital.log("Doctor " + id
                            + " begins treating P" + patient.id + ".");

                    try {

                        // Random treatment duration: 1 to 3 seconds
                        int treatmentTime =
                                1000 + random.nextInt(2000);

                        Thread.sleep(treatmentTime);

                        Hospital.log("Doctor " + id
                                + " finished treating P" + patient.id + ".");

                    } finally {

                        // Release treatment room
                        hospital.treatmentRooms.V();

                        Hospital.log("Doctor " + id
                                + " released the treatment room.");
                    }

                    Hospital.log("P" + patient.id + " left.");

                    Hospital.log("Doctor " + id
                            + " is now available.");

                    // Notify main thread that one patient is finished
                    hospital.completed.V();

                } else {

                    Hospital.log("Doctor " + id
                            + " could not acquire a treatment room.");

                    Hospital.log("Doctor " + id
                            + " is sending P" + patient.id
                            + " back to the waiting room.");

                    // Return patient to waiting room
                    hospital.waitingRoom.put(patient);

                    Hospital.log("Doctor " + id
                            + " is now available.");
                }
            }

        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();
        }
    }
}