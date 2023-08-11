import java.io.*;
import java.nio.file.*;
import java.util.*;

public class Account {
    private final String acctNum;
    private String address;
    private final List<String> resvNumList = new ArrayList<>();
    private final List<Reservation> resv = new ArrayList<>();
    private String emailAddress;
    private String phoneNum;

    public Account(String address, String emailAddress, String phoneNum) {
        this.acctNum = generateAccountNumber();
        this.address = address;
        this.emailAddress = emailAddress;
        this.phoneNum = phoneNum;
    }

    public Account(String line) {
        this.acctNum = line.substring(line.indexOf("<acctNum>") + 9, line.indexOf("</acctNum>"));
        this.address = line.substring(line.indexOf("<acctAddress>") + 13, line.indexOf("</acctAddress>"));
        this.phoneNum = line.substring(line.indexOf("<phoneNum>") + 10, line.indexOf("</phoneNum>"));
        this.emailAddress = line.substring(line.indexOf("<email>") + 7, line.indexOf("</email>"));
        parseResv(line);
    }

    private void parseResv(String line) {
        //as per the instructions, "The account’s file should have all the reservation numbers associated with the account but no other reservation data."
        while (line.contains("<reservationNumber>")) {
            resvNumList.add(line.substring(line.indexOf("<reservationNumber>") + 19, line.indexOf("</reservationNumber>")));
            line = line.substring(line.indexOf("</reservationNumber>") + 20); //this deletes the just-added reservation number from the read-in line (from file), and the while-loop will continue until all reservation numbers in the account file have been added.
        }
    }

    //Save account information to file
    public void saveToFile() {
        String accountNumber = getAcctNum();
        String accountDirectoryPath = Manager.DATA_DIRECTORY + "/" + accountNumber;
        String accountFilePath = accountDirectoryPath + "/acc-" + accountNumber + ".xml";
        File accountFile = new File(accountFilePath);

        try {
            Path accountDirectory = Paths.get(accountDirectoryPath);
            if (!Files.exists(accountDirectory)) {
                Files.createDirectories(accountDirectory);
                System.out.println("Account directory created successfully.");
            }
            Files.write(accountFile.toPath(), Arrays.asList(toString().split("\n")));
            System.out.println("Account information written to XML file successfully.");
        } catch (IOException e) {
            System.out.println("Error writing account information to XML file: " + e.getMessage());
        }
    }

    private String generateAccountNumber() {
        String accountNumber = "A";
        while (!isUniqueAcctNum(accountNumber)) {
            accountNumber = "A";
            String timestamp = String.valueOf(System.currentTimeMillis());
            String uniqueId = timestamp.substring(timestamp.length() - 9);
            accountNumber += uniqueId;
        }
        return accountNumber;
    }

    private boolean isUniqueAcctNum(String id) {
        if (id.equals("A")) {
            return false;
        }

        try {
            for (int i = 0; i < Manager.acctList.size(); i++) {
                if (id.equals(Manager.acctList.get(i).getAcctNum())) {
                    throw new Manager.DuplicateObjectException("Duplicate account number: " + id + " already exists.");
                }
            }
        } catch (Manager.DuplicateObjectException e) {
            return false;
        }
        return true;
    }


    public String getAcctNum() {
        return acctNum;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void addReservation(Reservation reservation) {
        resv.add(reservation);
    }

    public List<Reservation> getResv() {
        return resv;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmail(String email) {
        this.emailAddress = email;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public String toString() {
        String resvNums = "";
        for (Reservation reservation : resv) {
            resvNums += "\t<reservationNumber>" + reservation.getResvNum() + "</reservationNumber>\n";
        }
        return  "<Account>\n" +
                "\t<acctNum>" + acctNum + "</acctNum>\n" +
                "\t<acctAddress>" + address + "</acctAddress>\n" +
                "\t<email>" + emailAddress + "</email>\n" +
                "\t<phoneNum>" + phoneNum + "</phoneNum>\n" +
                resvNums +
                "</Account>";
    }
}
