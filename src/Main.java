public class Main {

    public static void main(String[] args) {
        Hospital hospital = new Hospital();

        try {
            hospital.startSimulation();
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            System.out.println("The simulation was interrupted.");
        }
    }
}
