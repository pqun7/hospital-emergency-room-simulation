
import java.util.Scanner;
import java.util.concurrent.Semaphore;

public class Hospital {

    private WaitingRoom waitingRoom;
    private TreatmentRoom treatmentRoom;
    private Semaphore completedPatients;

    private int numberOfDoctors;
    private int numberOfPatients;

    public static synchronized void log(String message) {
        System.out.println(message);
    }

    // Print events in a consistent format without sequence numbers.
    public static synchronized void logEvent(String message) {
        System.out.printf("%-14s | %s%n", message);
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

        log("");
        log("------------------------------------------");
        log("Waiting Room Capacity : " + waitingRoomCapacity);
        log("Number of Doctors     : " + numberOfDoctors);
        log("Number of Patients    : " + numberOfPatients);
        log("Treatment Rooms       : " + TreatmentRoom.NUMBER_OF_ROOMS);
        log("");
  

        waitingRoom = new WaitingRoom(waitingRoomCapacity);
        treatmentRoom = new TreatmentRoom();
        completedPatients = new Semaphore(0);

        Doctor[] doctors = new Doctor[numberOfDoctors];
        Patient[] patients = new Patient[numberOfPatients];

        for (int i = 0; i < numberOfDoctors; i++) {
            doctors[i] = new Doctor(i + 1, this);
            doctors[i].start();
        }

        for (int i = 0; i < numberOfPatients; i++) {
            patients[i] = new Patient(i + 1, this);
            patients[i].start();
        }

        for (Patient patient : patients) {
            patient.join();
        }

        for (int i = 0; i < numberOfPatients; i++) {
            completedPatients.acquire();
        }

        for (int i = 0; i < numberOfDoctors; i++) {
            waitingRoom.addStopSignal();
        }

        for (Doctor doctor : doctors) {
            doctor.join();
        }

        log("All patients have been treated successfully.");
        log("");
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

    public synchronized void patientFinished() {
        completedPatients.release();
    }

    public WaitingRoom getWaitingRoom() {
        return waitingRoom;
    }

    public TreatmentRoom getTreatmentRoom() {
        return treatmentRoom;
    }
}
