public class Patient extends Thread {

    private final int id;
    private final Hospital hospital;

    public Patient(int id, Hospital hospital) {
        this.id = id;
        this.hospital = hospital;
    }

    public int getPatientId() {
        return id;
    }

    @Override
    public void run() {
        try {
            Hospital.log("Patient " + id + " arrives at the hospital.");
            hospital.getWaitingRoom().enter(this);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
        }
    }
}
