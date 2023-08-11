import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;

public class Reservation {
    protected String acctNum;
    protected String resvNum;
    protected String lodgingAddress;
    protected String mailingAddress;
    protected String lodgingEmail;
    protected String startDate;
    protected String status; // cancelled, confirmed, draft
    protected int numNight;
    protected int numBed = 2;
    protected int numBedroom = 1;
    protected int numBathroom = 1;
    protected int lodgingSize;
    protected float pricePerNight;
    protected final int largeLodgingSize = 900;

    public Reservation() {
        this.acctNum = "-99";  // before reservation is made, acctNum must be "-99"
        this.status = "Draft";
        this.pricePerNight = 0;
    }

    //2nd constructor with more attributes
    public Reservation(String acctNum, String prefix, String lodgingAddress, String lodgingEmail, String startDate,
                       int numNight, int numBed, int numBedroom, int numBathroom, int loggingsize, String mailingAddress) {
        this.acctNum = acctNum;
        this.resvNum = generateReservationNumber(prefix);
        this.lodgingAddress = lodgingAddress;
        this.lodgingEmail = lodgingEmail;
        this.startDate = startDate;
        this.numNight = numNight;
        this.numBed = numBed;
        this.numBedroom = numBedroom;
        this.numBathroom = numBathroom;
        this.lodgingSize = loggingsize;
        this.status = "Draft";

        // Check if mailing address is null, use physical address as mailing address
        checkMailingAddress(mailingAddress);
        updatePrice();
    }

    // third constructor to parse variables from reading file
    public Reservation(String line) {
        // parse all variables based on XML tags
        acctNum = line.substring(line.indexOf("<AccountNumber>") + 15, line.indexOf("</AccountNumber>"));
        resvNum = line.substring(line.indexOf("<ReservationNumber>") + 19, line.indexOf("</ReservationNumber>"));
        lodgingAddress = line.substring(line.indexOf("<LodgingAddress>") + 16, line.indexOf("</LodgingAddress>"));
        lodgingEmail = line.substring(line.indexOf("<LodgingEmail>") + 14, line.indexOf("</LodgingEmail>"));
        startDate = line.substring(line.indexOf("<StartDate>") + 11, line.indexOf("</StartDate>"));
        numNight = Integer.parseInt(line.substring(line.indexOf("<NumberOfNights>") + 16, line.indexOf("</NumberOfNights>")));
        numBed = Integer.parseInt(line.substring(line.indexOf("<NumberOfBeds>") + 14, line.indexOf("</NumberOfBeds>")));
        numBedroom = Integer.parseInt(line.substring(line.indexOf("<NumberOfBedrooms>") + 18, line.indexOf("</NumberOfBedrooms>")));
        numBathroom = Integer.parseInt(line.substring(line.indexOf("<NumberOfBathrooms>") + 19, line.indexOf("</NumberOfBathrooms>")));
        lodgingSize = Integer.parseInt(line.substring(line.indexOf("<LodgingSize>") + 13, line.indexOf("</LodgingSize>")));
        String mailingAddress = line.substring(line.indexOf("<MailingAddress>") + 16, line.indexOf("</MailingAddress>"));
        checkMailingAddress(mailingAddress);
        status = line.substring(line.indexOf("<Status>") + 8, line.indexOf("</Status>"));
        updatePrice();
    }

    private String generateReservationNumber(String prefix) {
        String resvNum = "";
        Account myAcct = Manager.findAccountByNumber(acctNum);
        while (!isUniqueResvNum(resvNum, myAcct)) {
            resvNum = "";
            String timestamp = String.valueOf(System.currentTimeMillis());
            String uniqueId = timestamp.substring(timestamp.length() - 9);
            resvNum = prefix + uniqueId;
        }
        return resvNum;
    }

    private boolean isUniqueResvNum(String resvNum, Account myAcct) {
        if (resvNum.equals("")) {
            return false;
        }

        List<Reservation> resvNums = myAcct.getResv();

        try {
            for (Reservation num : resvNums) {
                if (resvNum.equals(num.getResvNum())) {
                    throw new Manager.DuplicateObjectException("Duplicate reservation number: " + resvNum + " already exists.");
                }
            }
        } catch (Manager.DuplicateObjectException e) {
            return false;
        }
        return true;
    }

    private void checkMailingAddress(String mailingAddress) {
        if (mailingAddress.equals("")) {
            this.mailingAddress = lodgingAddress;
        } else {
            this.mailingAddress = mailingAddress;
        }
    }

    public String getResvNum() {
        return resvNum;
    }

    public String getAcctNum() {
        return acctNum;
    }

    public String getLodgingAddress() {
        return lodgingAddress;
    }

    public void setLodgingAddress(String lodgingAddress) {
        this.lodgingAddress = lodgingAddress;
    }

    public String getMailingAddress() {
        return mailingAddress;
    }

    public void setMailingAddress(String mailingAddress) {
        checkMailingAddress(mailingAddress);
    }

    public void setLodgingEmail(String lodgingEmail) {
        this.lodgingEmail = lodgingEmail;
    }

    public String getLodgingEmail() {
        return this.lodgingEmail;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public int getNumNight() {
        return numNight;
    }

    public void setNumNight(int numNight) {
        this.numNight = numNight;
    }

    public int getNumBed() {
        return numBed;
    }

    public void setNumBed(int numBed) {
        this.numBed = numBed;
    }

    public int getNumBedroom() {
        return numBedroom;
    }

    public void setNumBedroom(int numBedroom) {
        this.numBedroom = numBedroom;
    }

    public int getNumBathroom() {
        return numBathroom;
    }

    public void setNumBathroom(int numBathroom) {
        this.numBathroom = numBathroom;
    }

    public int getLodgingSize() {
        return lodgingSize;
    }

    public void setLodgingSize(int lodgingSize) {
        this.lodgingSize = lodgingSize;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public float getPricePerNight() {
        updatePrice();
        return pricePerNight;
    }

    public float getTotalPrice() {
        return getPricePerNight() * numNight;
    }

    public double getBasePrice() {
        double basePrice = 120.0;

        // Additional fee for lodging size greater than 900 square feet
        if (lodgingSize > largeLodgingSize) {
            basePrice += 15.0;
        }

        return basePrice;
    }

    public void updatePrice() {
        if (status.equals("Draft")) {
            System.out.println("Price of reservation will remain unset until reservation is confirmed");
            return;
        }
        double basePrice = getBasePrice();

        // Additional fees for cabin
        // Full kitchen fee
        if (this instanceof CabinReservation) {
            CabinReservation cabinReservation = (CabinReservation) this;

            if (cabinReservation.hasFullKitchen()) {
                basePrice += 20.0;
            }

            // Additional bathroom fee
            int numOfAdditionalBathrooms = numBathroom - 1;
            if (numOfAdditionalBathrooms > 0) {
                basePrice += numOfAdditionalBathrooms * 5.0;
            }
        }

        // Additional fees for hotel
        if (this instanceof HotelReservation) {
            HotelReservation hotelReservation = (HotelReservation) this;

            // Flat fee for hotel
            basePrice += 50.0;

            // Kitchenette fee
            if (hotelReservation.hasKitchenette()) {
                basePrice += 10.0;
            }
        }

        pricePerNight = (float) (basePrice);
    }

    public void saveToFile() {
        String reservationFilePath = Manager.DATA_DIRECTORY + "/" + getAcctNum() + "/res-" + resvNum + ".xml";
        File reservationFile = new File(reservationFilePath);
        try {
            Files.write(reservationFile.toPath(), Arrays.asList(toString().split("\n")));
            System.out.println("Reservation " + resvNum + " written to XML file successfully.");
        } catch (IOException e) {
            System.out.println("Error writing reservation " + resvNum + " to XML file: " + e.getMessage());
        }
    }

    public String toString() {
        return "<Reservation>\n" +
                "\t<ReservationNumber>" + resvNum + "</ReservationNumber>\n" +
                "\t<AccountNumber>" + acctNum + "</AccountNumber>\n" +
                "\t<LodgingAddress>" + lodgingAddress + "</LodgingAddress>\n" +
                "\t<LodgingEmail>" + lodgingEmail + "</LodgingEmail>\n" +
                "\t<StartDate>" + startDate + "</StartDate>\n" +
                "\t<NumberOfNights>" + numNight + "</NumberOfNights>\n" +
                "\t<NumberOfBeds>" + numBed + "</NumberOfBeds>\n" +
                "\t<NumberOfBedrooms>" + numBedroom + "</NumberOfBedrooms>\n" +
                "\t<NumberOfBathrooms>" + numBathroom + "</NumberOfBathrooms>\n" +
                "\t<LodgingSize>" + lodgingSize + "</LodgingSize>\n" +
                "\t<MailingAddress>" + mailingAddress + "</MailingAddress>\n" +
                "\t<Status>" + status + "</Status>\n" +
                "\t<PricePerNight>" + getPricePerNight() + "</PricePerNight>\n";
    }
}
