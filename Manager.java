import java.io.*;
import java.text.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.regex.*;

public class Manager {
    protected static List<Account> acctList = new ArrayList<>();
    protected static Scanner scanner;
    protected static final String DATA_DIRECTORY = "C:\\Users\\DEN - NEW\\Desktop\\UMGC\\Summer 2023\\SWEN646\\accounts";
    private final NumberFormat usdFormat = NumberFormat.getCurrencyInstance(Locale.US);

    public Manager() {
        acctList = new ArrayList<>();
        scanner = new Scanner(System.in);
        loadAccountsFromXml();
    }

    private void loadAccountsFromXml() {
        File[] accountDirectories = new File(DATA_DIRECTORY).listFiles(File::isDirectory);
        if (accountDirectories != null) {
            for (File accountDirectory : accountDirectories) {
                File[] accountFiles = accountDirectory.listFiles((dir, name) -> name.startsWith("acc-") && name.endsWith(".xml"));
                if (accountFiles != null) {
                    for (File accountFile : accountFiles) {
                        Account account = readAccountFromXml(accountFile);
                        if (account != null) {
                            acctList.add(account);
                        } else {
                            throw new IllegalLoadException("Failed to load account file: " + accountFile.getName() + " for account number: " + accountFile.getName().replaceAll("acc-", "").replaceAll(".xml", ""));
                        }
                    }
                }

                File[] reservationFiles = accountDirectory.listFiles((dir, name) -> name.startsWith("res-") && name.endsWith(".xml"));
                if (reservationFiles != null) {
                    for (File reservationFile : reservationFiles) {
                        Reservation reservation = readReservationFromXml(reservationFile);
                        if (reservation != null) {
                            // Add the reservation to the corresponding account
                            Account account = findAccountByReservationFile(reservationFile);
                            if (account != null) {
                                account.addReservation(reservation);
                            } else {
                                throw new IllegalLoadException("Failed to load reservation file: " + reservationFile.getName() + " for account number: " + reservationFile.getName().replaceAll("res-", "").replaceAll(".xml", ""));
                            }
                        } else {
                            throw new IllegalLoadException("Failed to load reservation file: " + reservationFile.getName() + " for account number: " + reservationFile.getName().replaceAll("res-", "").replaceAll(".xml", ""));
                        }
                    }
                }
            }
        }
    }

    private Account readAccountFromXml(File accountFile) {
        try (Scanner fileScanner = new Scanner(accountFile)) {
            StringBuilder xmlContent = new StringBuilder();
            while (fileScanner.hasNextLine()) {
                xmlContent.append(fileScanner.nextLine());
            }
            return new Account(xmlContent.toString());
        } catch (IOException e) {
            System.out.println("Error reading account from XML file: " + e.getMessage());
        }
        return null;
    }

    private Reservation readReservationFromXml(File reservationFile) {
        try (Scanner fileScanner = new Scanner(reservationFile)) {
            StringBuilder xmlContent = new StringBuilder();
            while (fileScanner.hasNextLine()) {
                xmlContent.append(fileScanner.nextLine());
            }
            String filename = reservationFile.getName();

            if (filename.startsWith("res-C")) {
                return new CabinReservation(xmlContent.toString());
            } else if (filename.startsWith("res-H")) {
                return new HotelReservation(xmlContent.toString());
            } else if (filename.startsWith("res-O")) {
                return new HouseReservation(xmlContent.toString());
            } else {
                System.out.println("Invalid filename prefix: " + filename);
            }
        } catch (IOException e) {
            System.out.println("Error reading reservation from XML file: " + e.getMessage());
        }
        return null;
    }

    private Account findAccountByReservationFile(File reservationFile) {
        String accountNum = reservationFile.getParentFile().getName();
        for (Account account : acctList) {
            if (account.getAcctNum().equals(accountNum)) {
                return account;
            }
        }
        return null;
    }

    // Helper method to ask for a string input
    protected static String askString(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine();
            if (!input.isEmpty()) {
                return input;
            } else {
                try {
                    throw new IllegalArgumentException("Invalid input. Please enter a non-empty string.");
                } catch (IllegalArgumentException e) {
                    System.out.println(e.getMessage());
                }
            }
        }
    }

    // Helper method to ask for an int input
    protected static int askNumber(String prompt) {
        while (true) {
            System.out.print(prompt);

            if (scanner.hasNextInt()) {
                int tempInt = scanner.nextInt();
                scanner.nextLine(); // Consume newline character
                return tempInt;
            } else {
                scanner.nextLine(); // Consume invalid input
                try {
                    throw new IllegalArgumentException("Invalid input. Please enter a valid integer.");
                } catch (IllegalArgumentException e) {
                    System.out.println(e.getMessage());
                }
            }
        }
    }

    // Helper method to ask for a yes/no input
    protected static boolean askYesNoQuestion(String prompt) {
        String answer;
        while (true) {
            answer = askString(prompt + " (Y/N): ");
            if (answer.equalsIgnoreCase("Y")) {
                return true;
            } else if (answer.equalsIgnoreCase("N")) {
                return false;
            }

            try {
                throw new IllegalArgumentException("Invalid input. Please enter either 'Y' or 'N'.");
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    // Helper method to get a valid 10-digit phone number
    String getValidPhoneNum(String prompt) {
        String phoneNum;
        while (true) {
            phoneNum = askString(prompt);

            if (phoneNum.length() == 10 && phoneNum.matches("\\d{10}")) {
                break;
            }

            try {
                throw new IllegalArgumentException("Invalid phone number.");
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }
        return phoneNum;
    }

    // Helper method to get a valid email
    String getValidEmail(String prompt) {
        String email;
        while (true) {
            email = askString(prompt);

            Pattern pattern = Pattern.compile("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}");
            Matcher matcher = pattern.matcher(email);

            if (matcher.matches()) {
                break;
            }

            try {
                throw new IllegalArgumentException("Invalid email address.");
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }
        return email;
    }

    //Helper method
    protected static String getValidDate(String prompt) {
        while (true) {
            String input = askString(prompt);
            try {
                LocalDate.parse(input);
                return input;
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date format. Please enter a date in the format yyyy-mm-dd.");
            }
        }
    }

    //Helper method
    private Account askUserForAcct() {
        String acctNum = askString("Please enter account number (e.g., Axxxxx): ");

        Account myAcct = findAccountByNumber(acctNum);

        while (myAcct == null) {
            acctNum = askString("Account not found. Please try again, or enter \"1\" to create a new account, or enter \"9\" to go back: ");
            if (acctNum.equals("1")) {
                System.out.println("Creating new account!");
                createAcct();
                return null;
            } else if (acctNum.equals("9")) {
                return null;
            } else {
                myAcct = findAccountByNumber(acctNum);
            }
        }
        return myAcct;
    }

    //Helper method
    private Reservation askUserForResv(Account myAcct) {
        if (myAcct == null) {
            return null;
        }
        String resvNum = askString("Please enter the reservation number: ");
        Reservation myResv = findReservationByNumber(myAcct, resvNum);

        while (myResv == null) {
            resvNum = askString("Reservation not found. Please try again, or enter \"1\" to create a new reservation, or enter \"9\" to go back: ");
            if (resvNum.equals("1")) {
                System.out.println("Creating new reservation!");
                makeResv();
                return null;
            } else if (resvNum.equals("9")) {
                return null;
            } else {
                myResv = findReservationByNumber(myAcct, resvNum);
            }
        }
        return myResv;
    }

    // Helper method to find an account by account number
    protected static Account findAccountByNumber(String acctNum) {
        for (Account account : acctList) {
            if (account.getAcctNum().equals(acctNum)) {
                return account;
            }
        }
        return null;
    }

    // Helper method to find a reservation by number
    private Reservation findReservationByNumber(Account acct, String resvNum) {
        for (Reservation resv : acct.getResv()) {
            if (resv.getResvNum().equals(resvNum)) {
                return resv;
            }
        }
        return null;
    }

    // Choice 0: Create an Account
    public void createAcct() {
        String mailingAddress = askString("Please enter your mailing address: ");
        String phoneNum = getValidPhoneNum("Please enter a 10-digit phone number: ");
        String acctEmail = getValidEmail("Please enter account email address: ");
        Account newAcct = new Account(mailingAddress, acctEmail, phoneNum);
        acctList.add(newAcct);
        newAcct.saveToFile();
        System.out.println("Account created successfully. Your account number is " + newAcct.getAcctNum());
    }

    // Choice 1: Modify an Account
    public void modifyAcct() {
        Account myAcct = askUserForAcct();

        if (myAcct == null) {
            return;
        }

        System.out.println("Please choose the detail to update:");
        System.out.println("1. Mailing address");
        System.out.println("2. Phone Number");
        System.out.println("3. Email Address");
        int choice = askNumber("Enter your choice: ");
        switch (choice) {
            case 1:
                String updatedMailingAddress = askString("Please enter the updated mailing address: ");
                myAcct.setAddress(updatedMailingAddress);
                break;
            case 2:
                String updatedPhoneNumber = getValidPhoneNum("Please enter the updated phone number: ");
                myAcct.setPhoneNum(updatedPhoneNumber);
                break;
            case 3:
                String updatedEmailAddress = getValidEmail("Please enter the updated phone number: ");
                myAcct.setEmail(updatedEmailAddress);
                break;
            default:
                System.out.println("Invalid choice. Reservation not updated.");
                return;
        }
        myAcct.saveToFile();
    }

    // Choice 2: Make Reservation
    public void makeResv() {
        Account myAcct = askUserForAcct();

        if (myAcct == null) {
            return;
        }

        int reservationType = askNumber("Please enter the type of reservation (1 - Hotel, 2 - Cabin, 3 - House): ");

        switch (reservationType) {
            case 1: // Hotel Reservation
                boolean hasKitchenette = askYesNoQuestion("Would you like a kitchenette in the hotel room?");
                String hotelAddress = askString("Please enter the lodging address for the hotel reservation: ");
                String hotelEmail = getValidEmail("Please enter the lodging email for the hotel reservation: ");
                String startDateHotel = getValidDate("Please enter the reservation start date (yyyy-mm-dd): ");
                int numNightHotel = askNumber("Please enter the number of nights for the hotel reservation: ");
                int lodgingSizeHotel = askNumber("Please enter the size of the hotel room in square feet: ");
                String hotelMailingAddress = "";
                if (askYesNoQuestion("Is the mailing address different than the lodging address")) {
                    hotelMailingAddress = askString("Please enter the mailing address for the hotel reservation: ");
                }
                HotelReservation hotelResv = new HotelReservation(myAcct.getAcctNum(), "H", hotelAddress, hotelEmail,
                        startDateHotel, numNightHotel, 2, 1, 1, lodgingSizeHotel, hasKitchenette, hotelMailingAddress);
                hotelResv.setStatus("Draft");
                myAcct.addReservation(hotelResv);
                System.out.println("Hotel reservation made successfully. Reservation Number: " + hotelResv.getResvNum());
                hotelResv.saveToFile();
                myAcct.saveToFile();
                break;
            case 2: // Cabin Reservation
                boolean hasFullKitchen = askYesNoQuestion("Would you like a full kitchen in the cabin?");
                boolean hasLoft = askYesNoQuestion("Would you like a loft in the cabin?");
                int numBedroomsCabin = askNumber("Please enter the number of bedrooms in the cabin: ");
                int numBathroomsCabin = askNumber("Please enter the number of bathrooms in the cabin: ");
                int lodgingSizeCabin = askNumber("Please enter the size of the cabin in square feet: ");
                String cabinAddress = askString("Please enter the lodging address for the cabin reservation: ");
                String cabinEmail = getValidEmail("Please enter the lodging email for the cabin reservation: ");
                String startDateCabin = getValidDate("Please enter the reservation start date (yyyy-mm-dd): ");
                int numNightCabin = askNumber("Please enter the number of nights for the cabin reservation: ");
                String cabinMailingAddress = "";
                if (askYesNoQuestion("Is the mailing address different than the lodging address?: ")) {
                    cabinMailingAddress = askString("Please enter the mailing address for the cabin reservation: ");
                }
                CabinReservation cabinResv = new CabinReservation(myAcct.getAcctNum(), "C", cabinAddress, cabinEmail,
                        startDateCabin, numNightCabin, 0, numBedroomsCabin, numBathroomsCabin, lodgingSizeCabin, hasFullKitchen, hasLoft, cabinMailingAddress);
                cabinResv.setStatus("Draft");
                myAcct.addReservation(cabinResv);
                System.out.println("Cabin reservation made successfully. Reservation Number: " + cabinResv.getResvNum());
                cabinResv.saveToFile();
                myAcct.saveToFile();
                break;
            case 3: // House Reservation
                int numFloors = askNumber("Please enter the number of floors: ");
                int numBedroomsHouse = askNumber("Please enter the number of bedrooms in the house: ");
                int numBathroomsHouse = askNumber("Please enter the number of bathrooms in the house: ");
                int lodgingSizeHouse = askNumber("Please enter the size of the house in square feet: ");
                String houseAddress = askString("Please enter the lodging address for the house reservation: ");
                String houseEmail = getValidEmail("Please enter the lodging email for the house reservation: ");
                String startDateHouse = getValidDate("Please enter the reservation start date (yyyy-mm-dd): ");
                int numNightHouse = askNumber("Please enter the number of nights for the house reservation: ");
                String houseMailingAddress = "";
                if (askYesNoQuestion("Is the mailing address different than the lodging address?: ")) {
                    houseMailingAddress = askString("Please enter the mailing address for the house reservation: ");
                }
                HouseReservation houseResv = new HouseReservation(myAcct.getAcctNum(), "O", houseAddress, houseEmail,
                        startDateHouse, numNightHouse, 0, numBedroomsHouse, numBathroomsHouse, lodgingSizeHouse, numFloors, houseMailingAddress);
                houseResv.setStatus("Draft");
                myAcct.addReservation(houseResv);
                System.out.println("House reservation made successfully. Reservation Number: " + houseResv.getResvNum());
                houseResv.saveToFile();
                myAcct.saveToFile();
                break;
            default:
                System.out.println("Invalid reservation type.");
                break;
        }
    }

    // Choice 3: Update Reservation
    public void updateResv() {
        Reservation resv = askUserForResv(askUserForAcct());
        if (resv == null) {
            return;
        }

        // Update reservation details
        System.out.println("Please choose the detail to update: ");
        System.out.println("1. Number of Nights");
        System.out.println("2. Start Date");
        System.out.println("3. Lodging Size");
        System.out.println("4. Lodging Address");
        System.out.println("5. Lodging Email");
        System.out.println("6. Mailing Address");

        if (resv instanceof CabinReservation || resv instanceof HouseReservation) {
            System.out.println("7. Number of Beds");
            System.out.println("8. Number of Bedrooms");
            System.out.println("9. Number of Bathrooms");

            if (resv instanceof CabinReservation) {
                if (((CabinReservation) resv).hasLoft()) {
                    System.out.println("10. Change to Cabin without a Loft");
                } else {
                    System.out.println("10. Change to Cabin with a Loft");
                }
                if (((CabinReservation) resv).hasFullKitchen()) {
                    System.out.println("11. Change to Cabin without a Full Kitchen");
                } else {
                    System.out.println("11. Change to Cabin with a Full Kitchen");
                }
            } else { //resv instanceof HouseReservation
                System.out.println("10. Change Number of Floors");
            }
        }
        else {//resv instanceof HotelReservation)
            if (((HotelReservation) resv).hasKitchenette()) {
                System.out.println("7. Get a Hotel Room without a Kitchenette");
            } else {
                System.out.println("7. Get a Hotel Room with a Kitchenette");
            }
        }

        int choice = askNumber("Enter your choice: ");

        switch (choice) {
            case 1:
                resv.setNumNight(askNumber("Enter the updated number of nights: "));
                break;
            case 2:
                resv.setStartDate(getValidDate("Enter the updated start date (yyyy-mm-dd): "));
                break;
            case 3:
                resv.setLodgingSize(askNumber("Enter the updated lodging size in square feet: "));
                break;
            case 4:
                resv.setLodgingAddress(askString("Enter the updated lodging address: "));
                break;
            case 5:
                resv.setLodgingEmail(getValidEmail("Enter the updated lodging email: "));
                break;
            case 6:
                String updatedMailingAddress = "";
                if (askYesNoQuestion("Is the mailing address different than the lodging address")) {
                    updatedMailingAddress = askString("Enter the updated mailing address: ");
                }
                resv.setMailingAddress(updatedMailingAddress);
                break;
            case 7:
                if (resv instanceof CabinReservation || resv instanceof HouseReservation) {
                    resv.setNumBed(askNumber("Enter the updated number of beds: "));
                } else {//resv instanceof HotelReservation
                    ((HotelReservation) resv).setHasKitchenette(!((HotelReservation) resv).hasKitchenette());
                }
                break;
            case 8:
                if (resv instanceof CabinReservation || resv instanceof HouseReservation) {
                    resv.setNumBedroom(askNumber("Enter the updated number of bedrooms: "));
                }
                break;
            case 9:
                if (resv instanceof CabinReservation || resv instanceof HouseReservation) {
                    resv.setNumBathroom(askNumber("Enter the updated number of bathrooms: "));
                }
                break;
            case 10:
                if (resv instanceof CabinReservation) {
                    ((CabinReservation) resv).setHasLoft(!((CabinReservation) resv).hasLoft());
                } else if (resv instanceof HouseReservation) {
                    ((HouseReservation) resv).setNumOfFloors(askNumber("Enter updated number of floors: "));
                }
                break;
            case 11:
                if (resv instanceof CabinReservation) {
                    ((CabinReservation) resv).setHasFullKitchen(!((CabinReservation) resv).hasFullKitchen());
                }
                break;
            default:
                System.out.println("Invalid choice. Reservation not updated.");
                return;
        }

        // Update the price based on the updated details
        resv.updatePrice();

        // Update the XML file
        resv.saveToFile();
    }

    // Choice 4: Cancel of Confirm Reservation
    public void updateResvStatus() {
        Reservation resv = askUserForResv(askUserForAcct());
        if (resv == null) {
            return;
        }

        int choice;

        while (true) {
            choice = askNumber("Please enter \"1\" to confirm the reservation, \"2\" to cancel the reservation, or \"9\" to go back: ");
            if (choice == 1 || choice == 2 || choice == 9) {
                break;
            }
            System.out.println("You entered an invalid option!");
        }
        if (choice == 9) {
            return;
        }
        String status;
        if (choice == 1) {
            status = "Confirmed";
        } else { //status = "Canceled"
            if (!isDateInFuture(resv.getStartDate())) {
                try {
                    throw new IllegalOperationException("You can't cancel a reservation that's in the past.");
                } catch (IllegalOperationException e) {
                    System.out.println(e.getMessage());
                    return;
                }
            }
            if (resv.getStatus().equals("Confirmed")) {
                try {
                    throw new IllegalOperationException("You can't cancel a confirmed reservation.");
                } catch (IllegalOperationException e) {
                    System.out.println(e.getMessage());
                    return;
                }
            }
            status = "Cancelled";
        }
        resv.setStatus(status);
        resv.saveToFile();
        String output = "Reservation " + status.toLowerCase(Locale.ROOT) + " successfully.";
        if (status.equals("Confirmed")) {output += " Your total is " + usdFormat.format(resv.getTotalPrice());}
        System.out.println(output);
    }

    private boolean isDateInFuture(String date) {
        LocalDate currentDate = LocalDate.now();
        LocalDate inputDate = LocalDate.parse(date);
        return inputDate.isAfter(currentDate);
    }

    // Choice 5: Print All Accounts
    public void printAllAccts() {
        for (Account acct : acctList) {
            System.out.println(acct);
        }
    }

    // Choice 6: Print Specific Account
    public void printAcct() {
        Account myAcct = askUserForAcct();
        if (myAcct == null) {
            return;
        }
        System.out.println(myAcct);
    }

    //Choice 7: Get price-per-night
    public void getPricePerNight() {
        Reservation myResv = askUserForResv(askUserForAcct());
        if (myResv == null) {
            return;
        }
        System.out.println("Price for one night for reservation " + myResv.getResvNum() + ": " + usdFormat.format(myResv.getPricePerNight()));
    }

    //Choice 8: Get Total Price
    public void getTotalPrice() {
        Reservation myResv = askUserForResv(askUserForAcct());
        if (myResv == null) {
            return;
        }
        System.out.println("Total price for reservation " + myResv.getResvNum() + ": " + usdFormat.format(myResv.getTotalPrice()));
    }

    public static class IllegalLoadException extends RuntimeException {
        public IllegalLoadException(String message) {
            super(message);
        }
    }

    public static class DuplicateObjectException extends RuntimeException {
        public DuplicateObjectException(String message) {
            super(message);
        }
    }

    public static class IllegalOperationException extends RuntimeException {
        public IllegalOperationException(String message) {
            super(message);
        }
    }
}