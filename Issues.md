# Issues Found in the Current Implementation

## 1. Waiting Room Capacity Violation
- The waiting room capacity is set to 2, but 3 patients can enter at the same time.
- This means the waiting room capacity is not being enforced correctly.
- **Fix:** Use a shared `Semaphore` with the waiting room capacity to control available seats. Release a permit when a patient leaves the waiting room.

## 2. Treatment Room Capacity Violation
- There are only 2 treatment rooms, but all 3 doctors successfully acquire a treatment room.
- This means the treatment room limit is not being enforced correctly.
- **Fix:** Use one shared `Semaphore(2)` for all doctors. Do not create a separate Semaphore for each doctor.

## 3. Missing 3-Second Timeout Handling
- Doctors must wait a maximum of 3 seconds to acquire a treatment room.
- If a doctor cannot acquire a room within 3 seconds, the patient must return to the waiting room.
- **Fix:** Use `tryAcquire(3, TimeUnit.SECONDS)` and handle the timeout correctly.

## 4. Output Order Differences
- The patients and doctors do not always appear in the same order as the sample output.
- This is normal in concurrent programming because Threads execute independently.
- **Fix:** No change is required unless the assignment explicitly requires a specific execution order.

---

## Expected Behavior After Fixes

1. The waiting room must never contain more patients than its capacity.
2. No more than 2 doctors can treat patients simultaneously.
3. A doctor must wait no longer than 3 seconds to acquire a treatment room.
4. If no treatment room becomes available within 3 seconds, the patient must return to the waiting room.
5. All patients must eventually be treated, according to the assignment requirements.