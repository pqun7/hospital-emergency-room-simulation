// ==========================================
// 2. PATIENT
// ==========================================

class Patient extends Thread {

    int id;
    Hospital hospital;

    public Patient(int id, Hospital hospital) {

        this.id = id;
        this.hospital = hospital;
    }

    @Override
    public void run() {

        try {

            hospital.log("P" + id + " arrived.");

            hospital.waitingRoom.put(this);

        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();
        }
    }
}


