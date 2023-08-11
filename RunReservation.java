import java.util.Scanner;

public class RunReservation {
    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        Manager manager = new Manager();
        while (true) {
            displayMenu();
            processMenuChoice(scan, manager);
        }
    }

    public static void displayMenu() {
        System.out.println("\nWelcome to Eric's Rental Reservation System for Hotel, House, and Cabin! ");
        System.out.println("0. Create an Account for New User ...");
        System.out.println("1. Modify an Account for Existing User ...");
        System.out.println("2. Create a Reservation (must already have an Account) ...");
        System.out.println("3. Modify a Reservation (must already have a completed Reservation) ....");
        System.out.println("4. Confirm or Cancel a Reservation (must already have a completed Reservation) ....");
        System.out.println("5. Get the list of loaded accounts...");
        System.out.println("6. Retrieve a specific account...");
        System.out.println("7. Get the price-per-night for a reservation...");
        System.out.println("8. Get the total cost of a reservation...");
        System.out.println("9. Exit .... ");
    }

    public static void processMenuChoice(Scanner scan, Manager manager) {
        int choice = Manager.askNumber("");
        switch (choice) {
            case 0:
                manager.createAcct();
                break;

            case 1:
                manager.modifyAcct();
                break;

            case 2:
                manager.makeResv();
                break;

            case 3:
                manager.updateResv();
                break;

            case 4:
                manager.updateResvStatus();
                break;

            case 5:
                manager.printAllAccts();
                break;

            case 6:
                manager.printAcct();
                break;

            case 7:
                manager.getPricePerNight();
                break;

            case 8:
                manager.getTotalPrice();
                break;

            case 9:
                scan.close();
                System.exit(0);
                break;

            default:
                System.out.println("Invalid choice! Please try again.");
                break;
        }
    }

}
