import java.util.Scanner;

public class Hospital {

    private WaitingRoom waitingRoom;
    private TreatmentRoom treatmentRoom;
    private Semaphore completedPatients; // استخدام كلاس الـ Semaphore المخصص بدلاً من الجاهز
    private int numberOfDoctors;
    private int numberOfPatients;

    public static synchronized void log(String message) {
        System.out.println(message);
    }

    public void startSimulation() throws InterruptedException {
        Scanner scanner = new Scanner(System.in);

        int waitingRoomCapacity = readPositiveInteger(
                scanner, "Waiting room capacity: ");
        numberOfDoctors = readPositiveInteger(
                scanner, "Number of doctors: ");
        numberOfPatients = readPositiveInteger(
                scanner, "Number of patients: ");
        scanner.close();

        waitingRoom = new WaitingRoom(waitingRoomCapacity);
        treatmentRoom = new TreatmentRoom();
        completedPatients = new Semaphore(0);

        Doctor[] doctors = new Doctor[numberOfDoctors];
        Patient[] patients = new Patient[numberOfPatients];

        // 1- طباعة قائمة المرضى المبدئية مطابقة للسامبل تماماً
        StringBuilder sb = new StringBuilder("Patients arriving:\n");
        for (int i = 1; i <= numberOfPatients; i++) {
            sb.append("P").append(i).append(i == numberOfPatients ? "" : " ");
        }
        log(sb.toString());

        // 2- تشغيل ثريدات الأطباء في الخلفية
        for (int i = 0; i < numberOfDoctors; i++) {
            doctors[i] = new Doctor(i + 1, this);
            doctors[i].start();
        }

        // 3- تشغيل ثريدات المرضى بالتتابع لضمان وصول P1 ثم P2...
        for (int i = 0; i < numberOfPatients; i++) {
            patients[i] = new Patient(i + 1, this);
            patients[i].start();
            Thread.sleep(150); // فاصل زمني لتنظيم طابور الوصول
        }

        // انتظار وصول جميع ثريدات المرضى لغرفة الانتظار
        for (Patient patient : patients) {
            patient.join();
        }

        // الانتظار حتى انتهاء كشف جميع المرضى
        for (int i = 0; i < numberOfPatients; i++) {
            completedPatients.acquire();
        }

        // إرسال إشارات التوقف للأطباء لإنهاء عملهم بسلام
        for (int i = 0; i < numberOfDoctors; i++) {
            waitingRoom.addStopSignal();
        }

        // انتظار انتهاء ثريدات الأطباء
        for (Doctor doctor : doctors) {
            doctor.join();
        }

        log("All patients have been treated.");
        log("Simulation completed.");
    }

    private int readPositiveInteger(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            if (scanner.hasNextInt()) {
                int value = scanner.nextInt();
                if (value > 0) {
                    return value;
                }
            } else {
                scanner.next();
            }
            System.out.println("Please enter a whole number greater than zero.");
        }
    }

    public void patientFinished() {
        completedPatients.release();
    }

    public WaitingRoom getWaitingRoom() {
        return waitingRoom;
    }

    public TreatmentRoom getTreatmentRoom() {
        return treatmentRoom;
    }
}