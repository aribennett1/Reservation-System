import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Scanner;

import static org.junit.Assert.*;

public class Tests {
    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final InputStream originalIn = System.in;

    private Account testAccount;
    private Manager testManager;

    @Before
    public void setUp() {
        System.setOut(new PrintStream(outputStream));
        System.setIn(new ByteArrayInputStream("".getBytes())); // Empty input stream for testing
        testAccount = new Account("123 Main St", "test@example.com", "1234567890");
        testManager = new Manager();
        Manager.acctList.add(testAccount);
    }

    @After
    public void tearDown() {
        System.setOut(originalOut);
        System.setIn(originalIn);
        testManager.scanner = new Scanner(System.in);
    }

    @Test
    public void testAccountToString() {
        // Verify that the toString method returns the correct XML representation of the account
        String expectedXML = "<Account>\n" +
                "\t<acctNum>" + testAccount.getAcctNum() + "</acctNum>\n" +
                "\t<acctAddress>123 Main St</acctAddress>\n" +
                "\t<email>test@example.com</email>\n" +
                "\t<phoneNum>1234567890</phoneNum>\n" +
                "</Account>";
        assertEquals(expectedXML, testAccount.toString());
    }

    @Test
    public void testAddReservation() {
        // Verify that a reservation is successfully added to the account
        String acctNum = testAccount.getAcctNum();
        String prefix = "C";
        String lodgingAddress = "123 Main St";
        String lodgingEmail = "cabin@example.com";
        String startDate = "2023-07-05";
        int numNight = 3;
        int numBed = 2;
        int numBedroom = 1;
        int numBathroom = 1;
        int lodgingSize = 1000;
        boolean hasFullKitchen = true;
        Boolean hasLoft = true;
        String mailingAddress = "456 Elm St";

        CabinReservation reservation = new CabinReservation(acctNum, prefix, lodgingAddress, lodgingEmail, startDate, numNight, numBed, numBedroom, numBathroom, lodgingSize, hasFullKitchen, hasLoft, mailingAddress);
        testAccount.addReservation(reservation);

        List<Reservation> reservations = testAccount.getResv();
        assertTrue(reservations.contains(reservation));
    }


    @Test
    public void testSetAddress() {
        // Verify that the address is correctly set
        testAccount.setAddress("456 Oak St");
        assertEquals("456 Oak St", testAccount.getAddress());
    }

    @Test
    public void testSetEmail() {
        // Verify that the email address is correctly set
        testAccount.setEmail("new@example.com");
        assertEquals("new@example.com", testAccount.getEmailAddress());
    }

    @Test
    public void testSetPhoneNum() {
        // Verify that the phone number is correctly set
        testAccount.setPhoneNum("9876543210");
        assertEquals("9876543210", testAccount.getPhoneNum());
    }

    @Test
    public void testSaveAccountToFile() {
        // Verify that the account information is successfully saved to a file
        testAccount.saveToFile();
        String filePath = Manager.DATA_DIRECTORY + "/" + testAccount.getAcctNum() + "/acc-" + testAccount.getAcctNum() + ".xml";
        Path file = Path.of(filePath);
        assertTrue(Files.exists(file));
    }

    @Test
    public void testCabinReservationConstructor() {
        // Verify that the CabinReservation constructor sets the attributes correctly
        String acctNum = testAccount.getAcctNum();
        String prefix = "C";
        String lodgingAddress = "123 Main St";
        String lodgingEmail = "cabin@example.com";
        String startDate = "2023-07-05";
        int numNight = 3;
        int numBed = 2;
        int numBedroom = 1;
        int numBathroom = 1;
        int lodgingSize = 1000;
        boolean hasFullKitchen = true;
        boolean hasLoft = true;
        String mailingAddress = "456 Elm St";

        CabinReservation reservation = new CabinReservation(acctNum, prefix, lodgingAddress, lodgingEmail, startDate,
                numNight, numBed, numBedroom, numBathroom, lodgingSize, hasFullKitchen, hasLoft, mailingAddress);

        assertEquals(acctNum, reservation.getAcctNum());
        assertEquals(lodgingAddress, reservation.getLodgingAddress());
        assertEquals(lodgingEmail, reservation.getLodgingEmail());
        assertEquals(startDate, reservation.getStartDate());
        assertEquals(numNight, reservation.getNumNight());
        assertEquals(numBed, reservation.getNumBed());
        assertEquals(numBedroom, reservation.getNumBedroom());
        assertEquals(numBathroom, reservation.getNumBathroom());
        assertEquals(lodgingSize, reservation.getLodgingSize());
        assertTrue(reservation.hasFullKitchen());
        assertTrue(reservation.hasLoft());
        assertEquals("Draft", reservation.getStatus());
        assertEquals(0.0, reservation.getPricePerNight(), 0.001); // Price should be unset until updated
    }

    @Test
    public void testAddReservationToAccount() {
        // Verify that a reservation is added to the account's reservation list
        Account localTestAccount1 = new Account("123 Main St", "test@example.com", "1234567890");
        Manager.acctList.add(localTestAccount1);
        CabinReservation reservation = new CabinReservation(localTestAccount1.getAcctNum(), "C", "123 Main St", "cabin@example.com",
                "2023-07-05", 3, 2, 1, 1, 1000, true, true, "456 Elm St");

        assertTrue(localTestAccount1.getResv().isEmpty());
        localTestAccount1.addReservation(reservation);
        assertEquals(1, localTestAccount1.getResv().size());
        assertTrue(localTestAccount1.getResv().contains(reservation));
    }

    @Test
    public void testReservationPriceUpdate() {
        // Verify that the price per night is updated correctly after status change
        CabinReservation reservation = new CabinReservation(testAccount.getAcctNum(), "C", "123 Main St", "cabin@example.com",
                "2023-07-05", 3, 2, 1, 1, 1000, true, true, "456 Elm St");

        assertEquals(0.0, reservation.getPricePerNight(), 0.001); // Price should be unset until updated

        // Change status and verify price update
        reservation.setStatus("Confirmed");
        // Calculate the expected price per night based on the provided criteria
        double basePrice = 120.0;
        double lodgingSizeFee = 15.0;
        double fullKitchenFee = 20.0;
        double additionalBathroomFee = 5.0;

        double expectedPricePerNight = basePrice + lodgingSizeFee + fullKitchenFee + (reservation.getNumBathroom() - 1) * additionalBathroomFee;

        // Assert that the expected price per night matches the calculated price per night
        assertEquals((float) expectedPricePerNight, reservation.getPricePerNight(), 0.001);
    }

    @Test
    public void testReservationSavingToFile() {
        // Verify that reservation information is successfully saved to a file
        Account localTestAccount2 = new Account("123 Main St", "test@example.com", "1234567890");
        Manager.acctList.add(localTestAccount2);
        localTestAccount2.saveToFile();
        CabinReservation reservation = new CabinReservation(localTestAccount2.getAcctNum(), "C", "123 Main St", "cabin@example.com",
                "2023-07-05", 3, 2, 1, 1, 1000, true, true, "456 Elm St");
        localTestAccount2.addReservation(reservation);

        reservation.saveToFile();

        // Assert that the file was created and contains the reservation information
        String filePath = Manager.DATA_DIRECTORY + "/" + localTestAccount2.getAcctNum() + "/res-" + reservation.getResvNum() + ".xml";
        Path file = Path.of(filePath);
        assertTrue(Files.exists(file));
    }

    @Test
    public void testLoadedAccountsAndReservations() {
        // Verify that all accounts and their reservations have been loaded

        // First, we'll count the total number of accounts and reservations in the data directory
        int totalAccounts = 0;
        int totalReservations = 0;
        File[] accountDirectories = new File(Manager.DATA_DIRECTORY).listFiles(File::isDirectory);
        if (accountDirectories != null) {
            totalAccounts = accountDirectories.length;
            for (File accountDirectory : accountDirectories) {
                File[] reservationFiles = accountDirectory.listFiles((dir, name) -> name.startsWith("res-") && name.endsWith(".xml"));
                if (reservationFiles != null) {
                    totalReservations += reservationFiles.length;
                }
            }
        }

        Manager localTestManager = new Manager();

        // Next, we'll check if the loaded accounts and reservations match the counts we obtained above
        assertEquals(totalAccounts, localTestManager.acctList.size());

        int loadedReservations = 0;
        for (Account account : localTestManager.acctList) {
            loadedReservations += account.getResv().size();
        }
        assertEquals(totalReservations, loadedReservations);
    }

    @Test
    public void testAskString_ValidInput() {
        String input = "Hello, World!";
        InputStream inputStream = new ByteArrayInputStream(input.getBytes());
        testManager.scanner = new Scanner(inputStream); // Set the Manager's scanner to the testScanner
        String result = testManager.askString("Enter a string: ");
        assertEquals(input, result);
    }

    @Test
    public void testAskYesNoQuestion_YesAnswer() {
        String input = "Y";
        InputStream inputStream = new ByteArrayInputStream(input.getBytes());
        testManager.scanner = new Scanner(inputStream); // Set the Manager's scanner to the testScanner
        boolean result = testManager.askYesNoQuestion("Do you want to proceed?");
        assertTrue(result);
    }

    @Test
    public void testAskYesNoQuestion_NoAnswer() {
        String input = "N";
        InputStream inputStream = new ByteArrayInputStream(input.getBytes());
        testManager.scanner = new Scanner(inputStream); // Set the Manager's scanner to the testScanner
        boolean result = testManager.askYesNoQuestion("Do you want to proceed?");
        assertFalse(result);
    }

}
