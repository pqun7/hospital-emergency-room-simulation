// important rules

// WaitingRoom      0 <= numberOfPatients <= capacity
// TreatmentRoom    0 <= permits <= 2

public class WaitingRoom {

    // 1- determine the capacity of the waiting room
    private final Patient[] patients;
    private int nextPatient = 0;
    private int nextDoctor = 0;
    private int numberOfPatients = 0;

    // Total number of patients in this simulation.
    private final int totalPatients;

    // Track patient arrival and enforce entry order by patient ID.
    private int patientsArrived = 0;
    private int nextPatientIdToEnter = 1;

    // Prevent another doctor from taking a patient
    // before the waiting room gets refilled.
    private boolean refillInProgress = false;

    public WaitingRoom(int capacity, int totalPatients) {
        if (capacity <= 0) {
            throw new IllegalArgumentException(
                    "Waiting room capacity must be positive.");
        }

        if (totalPatients <= 0) {
            throw new IllegalArgumentException(
                    "Number of patients must be positive.");
        }

        // using array to store patients in the waiting room, with a fixed capacity
        patients = new Patient[capacity];
        this.totalPatients = totalPatients;
    }

    // 2- prevent a patient from entering when the room is already full

    public synchronized void enter(Patient patient)
            throws InterruptedException {

        patientsArrived++;
        notifyAll();

        // Patients enter according to their IDs.
        // A patient must wait if:
        // 1- The waiting room is full.
        // 2- It is not this patient's turn.
        if (numberOfPatients >= patients.length
                || patient.getPatientId() != nextPatientIdToEnter) {

            if (numberOfPatients >= patients.length) {
                Hospital.logEvent(
                        "Waiting room is FULL ("
                                + numberOfPatients + "/"
                                + patients.length + ").");
            }

            Hospital.logEvent(
                    "Patient " + patient.getPatientId()
                            + " is waiting outside.");
        }

        while (numberOfPatients >= patients.length
                || patient.getPatientId() != nextPatientIdToEnter) {

            wait();
        }

        addPatient(patient);

        nextPatientIdToEnter++;

        Hospital.logEvent(
                "Patient " + patient.getPatientId()
                        + " entered the waiting room ("
                        + numberOfPatients + "/"
                        + patients.length + ").");

        notifyAll();
    }

    // The doctor takes a patient from the waiting room.
    public synchronized Patient take(int doctorId)
            throws InterruptedException {

        // Doctors wait until:
        // 1- The first group of patients is ready.
        // 2- The waiting room is not empty.
        // 3- Another doctor is not waiting for the room to be refilled.
        while (numberOfPatients == 0
                || !initialPatientsReady()
                || refillInProgress) {

            wait();
        }

        Patient patient = patients[nextDoctor];

        patients[nextDoctor] = null;

        nextDoctor = (nextDoctor + 1) % patients.length; // Circular Queue

        numberOfPatients--;

        // Do not log the STOP signal as a real patient.
        if (patient != Doctor.STOP) {

            Hospital.logEvent(
                    "Doctor " + doctorId + " takes Patient "
                            + patient.getPatientId() + ".");

            // If more patients are waiting to enter,
            // reserve this turn until the waiting room is refilled.
            if (nextPatientIdToEnter <= totalPatients) {

                refillInProgress = true;
                notifyAll();

                while (numberOfPatients < patients.length
                        && nextPatientIdToEnter <= totalPatients) {

                    wait();
                }

                refillInProgress = false;
                notifyAll();
            }
        }

        // A removed patient creates space for another patient.
        notifyAll();

        return patient;
    }

    // Check whether the first group of patients is ready.
    private boolean initialPatientsReady() {

        // If the room is full, doctors can start.
        // If there are fewer patients than the capacity,
        // doctors wait until all patients have entered.
        return numberOfPatients == patients.length
                || (patientsArrived == totalPatients
                        && nextPatientIdToEnter > totalPatients);
    }

    public synchronized void addStopSignal()
            throws InterruptedException {

        while (numberOfPatients == patients.length) {
            wait();
        }

        addPatient(Doctor.STOP);
        notifyAll();
    }

    private void addPatient(Patient patient) {

        patients[nextPatient] = patient;
        nextPatient = (nextPatient + 1) % patients.length;
        numberOfPatients++;

        notifyAll();
    }
}