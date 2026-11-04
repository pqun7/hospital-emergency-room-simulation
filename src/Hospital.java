import java.util.Scanner;
import java.util.concurrent.Semaphore;

public class Hospital {

    private WaitingRoom waitingRoom;
    private TreatmentRoom treatmentRoom;

    // Semaphore to count the number of patients who finished treatment.
    private Semaphore completedPatients;

    public static synchronized void log(String message) {
        System.out.println(message);
    }

    public static synchronized void logEvent(String message) {
        System.out.println(message);
    }

    public void startSimulation() throws InterruptedException {

        Scanner scanner = new Scanner(System.in);

        int waitingRoomCapacity = readPositiveInteger(
                scanner, "Waiting room capacity: ");

        int numberOfDoctors = readPositiveInteger(
                scanner, "Number of doctors: ");

        int numberOfPatients = readPositiveInteger(
                scanner, "Number of patients: ");

        scanner.close();

        // create the waiting room with the specified capacity.
        waitingRoom = new WaitingRoom(
                waitingRoomCapacity, numberOfPatients);

        // create the treatment room.
        treatmentRoom = new TreatmentRoom();

        // initialize the semaphore
        completedPatients = new Semaphore(0);

        Doctor[] doctors = new Doctor[numberOfDoctors];
        Patient[] patients = new Patient[numberOfPatients];

        // 2- start all doctors.
        for (int i = 0; i < numberOfDoctors; i++) {
            doctors[i] = new Doctor(i + 1, this);
            doctors[i].start();
        }

        // 3- start all patients.
        for (int i = 0; i < numberOfPatients; i++) {

            patients[i] = new Patient(i + 1, this);

            // Display patient arrivals in ID order.
            logEvent(
                    "Patient " + (i + 1)
                            + " arrives at the hospital.");

            patients[i].start();
        }

        // 4- Wait until all patient threads finish entering the waiting room
        for (Patient patient : patients) {
            patient.join();
        }

        // 5- Wait until all patients complete their treatment.
        for (int i = 0; i < numberOfPatients; i++) {
            completedPatients.acquire();
        }

        // 6- Send a stop signal to each doctor.
        // This allows doctors to stop waiting for new patients.
        for (int i = 0; i < numberOfDoctors; i++) {
            waitingRoom.addStopSignal();
        }

        // 7- Wait until all doctor threads finish execution.
        for (Doctor doctor : doctors) {
            doctor.join();
        }

        // Display the final simulation messages.
        log("All patients have been treated successfully.");
        log("Hospital simulation completed.");
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

            System.out.println(
                    "Please enter a whole number greater than zero.");
        }
    }

    // called when a patient finishes treatment.
    public synchronized void patientFinished() {
        completedPatients.release();
    }

    // return the waiting room object.
    public WaitingRoom getWaitingRoom() {
        return waitingRoom;
    }

    // return the treatment room object.
    public TreatmentRoom getTreatmentRoom() {
        return treatmentRoom;
    }
}