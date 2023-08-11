public class HouseReservation extends Reservation {
    private int numOfFloors;

    public HouseReservation(String acctNum, String prefix, String lodgingAddress, String lodgingEmail, String startDate,
                            int numNight, int numBed, int numBedroom, int numBathroom, int loggingsize, int numOfFloors, String mailingAddress) {
        // resvNum must start with "O".
        super(acctNum, prefix, lodgingAddress, lodgingEmail, startDate, numNight, numBed, numBedroom, numBathroom, loggingsize, mailingAddress);
        this.numOfFloors = numOfFloors;
    }
    // constructor with reading from file
    public HouseReservation(String line) {
        super(line);
        numOfFloors = Integer.parseInt(line.substring(line.indexOf("<NumberOfFloors>") + 16, line.indexOf("</NumberOfFloors>")));
    }

    public int getNumOfFloors() {
        return numOfFloors;
    }

    public void setNumOfFloors(int numOfFloors) {
        this.numOfFloors = numOfFloors;
    }

    public String toString() {
        return super.toString() +
                "\t<ReservationType>House</ReservationType>\n" +
                "\t<NumberOfFloors>" + numOfFloors + "</NumberOfFloors>\n" +
                "</Reservation>";
    }

}
