public class CabinReservation extends Reservation {
    private boolean hasFullKitchen;
    private boolean hasLoft;

    //2nd constructor with more attributes and values
    public CabinReservation(String acctNum, String prefix, String lodgingAddress, String lodgingEmail, String startDate,
                            int numNight, int numBed, int numBedroom, int numBathroom, int loggingsize, boolean hasFullKitchen, Boolean hasLoft, String mailingAddress) {
        // resvNum must start with "C".
        super(acctNum, prefix, lodgingAddress, lodgingEmail, startDate, numNight, numBed, numBedroom, numBathroom, loggingsize, mailingAddress);
        this.hasFullKitchen = hasFullKitchen;
        this.hasLoft = hasLoft;
    }

    //3rd constructor with reading from file
    public CabinReservation(String line) {
        super(line);
        hasFullKitchen = Boolean.parseBoolean(line.substring(line.indexOf("<HasFullKitchen>") + 16, line.indexOf("</HasFullKitchen>")));
        hasLoft = Boolean.parseBoolean(line.substring(line.indexOf("<HasLoft>") + 9, line.indexOf("</HasLoft>")));
    }

    public boolean hasFullKitchen() {
        return hasFullKitchen;
    }

    public void setHasFullKitchen(boolean hasFullKitchen) {
        this.hasFullKitchen = hasFullKitchen;
    }

    public boolean hasLoft() {
        return hasLoft;
    }

    public void setHasLoft(boolean hasLoft) {
        this.hasLoft = hasLoft;
    }

    public String toString() {
        return  super.toString() +
                "\t<ReservationType>Cabin</ReservationType>\n" +
                "\t<HasFullKitchen>" + hasFullKitchen + "</HasFullKitchen>\n" +
                "\t<HasLoft>" + hasLoft + "</HasLoft>\n" +
                "</Reservation>";
    }

}
