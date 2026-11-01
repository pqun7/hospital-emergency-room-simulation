public class WaitingRoom {

    private final Patient[] patients;
    private int nextPatient = 0;
    private int nextDoctor = 0;
    private int numberOfPatients = 0;

    public WaitingRoom(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Waiting room capacity must be positive.");
        }
        patients = new Patient[capacity];
    }

    public synchronized void enter(Patient patient) throws InterruptedException {
        while (numberOfPatients == patients.length) {
            Hospital.log("The waiting room is full.");
            Hospital.log("Patient " + patient.getPatientId()
                    + " waits outside for an available seat.");
            // Wait outside until a seat becomes free.
            wait();
        }

        addPatient(patient);
        Hospital.log("Patient " + patient.getPatientId() + " enters the waiting room.");
    }

    public synchronized Patient take() throws InterruptedException {
        while (numberOfPatients == 0) {
            // Doctors wait until a patient arrives.
            wait();
        }

        Patient patient = patients[nextDoctor];
        patients[nextDoctor] = null;
        nextDoctor = (nextDoctor + 1) % patients.length;
        numberOfPatients--;
        // A removed patient creates space for another patient.
        notifyAll();
        return patient;
    }

    public synchronized void addStopSignal() throws InterruptedException {
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
