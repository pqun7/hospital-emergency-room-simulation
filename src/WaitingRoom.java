public class WaitingRoom {
    private final Patient[] patients;
    private int nextPatient = 0;
    private int nextDoctor = 0;
    private int numberOfPatients = 0;

    private final Semaphore mutex;
    private final Semaphore empty;
    private final Semaphore full;

    public WaitingRoom(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Waiting room capacity can't be negative value!");
        }
        patients = new Patient[capacity];
        this.mutex = new Semaphore(1);
        this.empty = new Semaphore(capacity);
        this.full = new Semaphore(0);
    }

    public void enter(Patient patient) throws InterruptedException {
        boolean hadToWait = false;

        // بنفحص تحت حماية الـ Mutex هل الكراسي كلها مشغولة حالياً
        mutex.acquire();
        if (numberOfPatients >= patients.length) {
            hadToWait = true;
            Hospital.log("The waiting room is full.");
            Hospital.log("P" + patient.getPatientId() + " is waiting outside for an available seat.");
        }
        mutex.release();

        // لو مفيش مكان هينام هنا لحد ما دكتور يفضيله كرسي فعلياً
        empty.acquire();

        // أول ما الكرسي يفضى فعلياً يدخل ويسجل مكانه ويطبع
        mutex.acquire();
        patients[nextPatient] = patient;
        nextPatient = (nextPatient + 1) % patients.length;
        numberOfPatients++;
        Hospital.log("P" + patient.getPatientId() + " entered the waiting room.");
        mutex.release();

        full.release(); // إشعار الأطباء بوجود مريض
    }

    public Patient take() throws InterruptedException {
        full.acquire(); // ينتظر لحد ما مريض يدخل
        mutex.acquire();

        Patient patient = patients[nextDoctor];
        patients[nextDoctor] = null;
        nextDoctor = (nextDoctor + 1) % patients.length;
        numberOfPatients--;

        mutex.release();
        empty.release(); // تحرير كرسي لمن ينتظر بالخارج

        return patient;
    }

    public void addStopSignal() throws InterruptedException {
        empty.acquire();
        mutex.acquire();
        patients[nextPatient] = Doctor.STOP;
        nextPatient = (nextPatient + 1) % patients.length;
        mutex.release();
        full.release();
    }
}