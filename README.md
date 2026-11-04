# Hospital Emergency Room Simulation

This project demonstrates the Producer-Consumer problem using Java threads,
a bounded buffer, and semaphore which is built from scratch.

## Project structure

```text
src/
├── Main.java
├── Hospital.java
├── Semaphore.java
├── Patient.java
├── Doctor.java
├── WaitingRoom.java
└── TreatmentRoom.java
```

## How it works

- Each `Patient` is a producer. It arrives and calls `WaitingRoom.enter`.
- `WaitingRoom` is a bounded circular buffer. A patient waits with `wait()`
  when all seats are occupied, and a doctor waits when the buffer is empty.
  `notifyAll()` wakes threads after a patient is added or removed.
- Each `Doctor` is a consumer. It removes one patient, then tries to acquire
  one of the treatment rooms.
- `TreatmentRoom` owns a `Semaphore` initialized with exactly two permits.
  Therefore, at most two doctors can treat patients at the same time.
- A doctor uses `tryAcquire(3000)`, so it waits at most three seconds for a
  treatment room. On timeout, it returns the patient to the waiting room.
- The hospital uses another semaphore to count completed patients. After all
  patients finish, stop signals allow the doctor threads to terminate.

The waiting room and treatment rooms are synchronized independently. The
waiting room's monitor protects its array and counters, while the custom
semaphore protects treatment-room permits. This prevents overfilling, lost
patients, duplicate treatment, and simultaneous use of one treatment room by
two doctors. No lock is held while a doctor waits for a treatment room, which
also avoids a deadlock between these resources.


```text
Waiting room capacity: 2
Number of doctors: 3
Number of patients: 5
```

Negative or non-numeric input is rejected with a clear message.

## Example output

Thread scheduling and treatment times are random, so the exact order differs.

```text
Patient 1 arrives at the hospital.
Patient 1 enters the waiting room.
Doctor 1 is assigned to Patient 1.
Doctor 1 acquired a treatment room.
Doctor 1 begins treating Patient 1.
The waiting room is full.
Patient 4 waits outside for an available seat.
Patient 1 finishes treatment.
Patient 1 leaves the hospital.
Doctor 1 releases the treatment room.
All patients have been treated.
Simulation completed.
```
