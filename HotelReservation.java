public class HotelReservation extends Reservation {
    private boolean hasKitchenette;

    public HotelReservation(String acctNum, String prefix, String lodgingAddress, String lodgingEmail, String startDate,
                            int numNight, int numBed, int numBedroom, int numBathroom, int loggingsize, boolean hasKitchenette, String mailingAddress) {
        // resvNum must start with "H".
        super(acctNum, prefix, lodgingAddress, lodgingEmail, startDate, numNight, numBed, numBedroom, numBathroom, loggingsize, mailingAddress);
        this.hasKitchenette = hasKitchenette;
    }

    //constructor with reading from file
    public HotelReservation(String line) {
        super(line);
        hasKitchenette = Boolean.parseBoolean(line.substring(line.indexOf("<HasKitchenette>") + 17, line.indexOf("</HasKitchenette>")));
    }

    public boolean hasKitchenette() {
        return hasKitchenette;
    }

    public void setHasKitchenette(boolean hasKitchenette) {
        this.hasKitchenette = hasKitchenette;
    }

    public String toString() {
        return super.toString() +
               "\t<ReservationType>Hotel</ReservationType>\n" +
               "\t<HasKitchenette>" + hasKitchenette + "</HasKitchenette>\n" +
               "</Reservation>";
    }
}
