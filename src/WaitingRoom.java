
// ==========================================
// 3. BOUNDED BUFFER (WAITING ROOM)
// ==========================================

class Buffer {

    private Patient[] store;

    private int in = 0;
    private int out = 0;

    private int size;

    // Three semaphores
    private Semaphore spaces;
    private Semaphore elements;
    private Semaphore mutex;

    public Buffer(int size) {

        this.size = size;

        store = new Patient[size];

        spaces = new Semaphore(size);
        elements = new Semaphore(0);
        mutex = new Semaphore(1);
    }

    // Producer: Patient enters waiting room
    public void put(Patient patient)
            throws InterruptedException {

        // Check whether the waiting room has a free seat
        boolean acquired = spaces.tryAcquire(0);

        if (!acquired) {

            Hospital.log("The waiting room is full.");

            Hospital.log("P" + patient.id
                    + " is waiting outside for an available seat.");

            spaces.P();
        }

        try {

            mutex.P();

        } catch (InterruptedException e) {

            spaces.V();
            throw e;
        }

        store[in] = patient;

        in = (in + 1) % size;

        mutex.V();

        // Notify consumers that a patient is available
        elements.V();

        Hospital.log("P" + patient.id
                + " entered the waiting room.");
    }

    // Consumer: Doctor takes a patient
    public Patient take() throws InterruptedException {

        elements.P();

        try {

            mutex.P();

        } catch (InterruptedException e) {

            elements.V();
            throw e;
        }

        Patient patient = store[out];

        store[out] = null;

        out = (out + 1) % size;

        mutex.V();

        // Notify producers that a seat is available
        spaces.V();

        return patient;
    }
}
