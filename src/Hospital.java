
import java.util.Scanner;
import java.Doctor;
import java.util.Random;

public class Hospital {

    Buffer waitingRoom;

    // Exactly two treatment rooms
    Semaphore treatmentRooms = new Semaphore(2);

    // Counts completed patients
    Semaphore completed = new Semaphore(0);

    int numberOfPatients;
    int numberOfDoctors;

    public Hospital(int capacity, int doctors, int patients) {

        waitingRoom = new Buffer(capacity);

        numberOfDoctors = doctors;
        numberOfPatients = patients;
    }

    // Synchronized printing to keep output organized
    public static synchronized void log(String message) {

        System.out.println(message);
    }

    public void startSimulation() throws InterruptedException {

        Doctor[] doctors = new Doctor[numberOfDoctors];

        Patient[] patients = new Patient[numberOfPatients];

        // Create doctors
        for (int i = 0; i < numberOfDoctors; i++) {

            doctors[i] = new Doctor(i + 1, this);

            doctors[i].start();
        }

        // Create patients
        for (int i = 0; i < numberOfPatients; i++) {

            patients[i] = new Patient(i + 1, this);

            patients[i].start();
        }

        // Wait for all patient arrival threads
        for (int i = 0; i < numberOfPatients; i++) {

            patients[i].join();
        }

        // Wait until all patients finish treatment
        for (int i = 0; i < numberOfPatients; i++) {

            completed.P();
        }

        // Send stop signals to all doctors
        for (int i = 0; i < numberOfDoctors; i++) {

            waitingRoom.put(Doctor.STOP);
        }

        // Wait for all doctors to finish
        for (int i = 0; i < numberOfDoctors; i++) {

            doctors[i].join();
        }

        log("All patients have been treated.");

        log("Simulation completed.");
    }

    public static void main(String[] args) {

        Scanner input = new Scanner(System.in);

        System.out.print("Waiting room capacity: ");
        int capacity = input.nextInt();

        System.out.print("Number of doctors: ");
        int doctors = input.nextInt();

        System.out.print("Number of patients: ");
        int patients = input.nextInt();

        if (capacity <= 0 || doctors <= 0 || patients <= 0) {

            System.out.println("All inputs must be positive.");

            input.close();

            return;
        }

        Hospital hospital =
                new Hospital(capacity, doctors, patients);

        try {

            hospital.startSimulation();

        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();

            System.out.println("Simulation interrupted.");

        }

        input.close();
    }
}
