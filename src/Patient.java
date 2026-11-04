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
            Hospital.log("P" + id + " arrived.");
            hospital.getWaitingRoom().enter(this);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}