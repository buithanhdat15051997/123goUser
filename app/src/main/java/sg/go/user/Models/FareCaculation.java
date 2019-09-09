package sg.go.user.Models;

public class FareCaculation {

    private String EstimatedFare;
    private String TaxPrice;
    private String BasePrice;
    private String MinFare;
    private String BookingFee;
    private String Currency;
    private String DistanceUnit;

    public FareCaculation() {

    }

    public FareCaculation(String estimatedFare,
                          String taxPrice,
                          String basePrice,
                          String minFare,
                          String bookingFee,
                          String currency,
                          String distanceUnit) {

        EstimatedFare = estimatedFare;
        TaxPrice = taxPrice;
        BasePrice = basePrice;
        MinFare = minFare;
        BookingFee = bookingFee;
        Currency = currency;
        DistanceUnit = distanceUnit;

    }

    public String getEstimatedFare() {
        return EstimatedFare;
    }

    public void setEstimatedFare(String estimatedFare) {
        EstimatedFare = estimatedFare;
    }

    public String getTaxPrice() {
        return TaxPrice;
    }

    public void setTaxPrice(String taxPrice) {
        TaxPrice = taxPrice;
    }

    public String getBasePrice() {
        return BasePrice;
    }

    public void setBasePrice(String basePrice) {
        BasePrice = basePrice;
    }

    public String getMinFare() {
        return MinFare;
    }

    public void setMinFare(String minFare) {
        MinFare = minFare;
    }

    public String getBookingFee() {
        return BookingFee;
    }

    public void setBookingFee(String bookingFee) {
        BookingFee = bookingFee;
    }

    public String getCurrency() {
        return Currency;
    }

    public void setCurrency(String currency) {
        Currency = currency;
    }

    public String getDistanceUnit() {
        return DistanceUnit;
    }

    public void setDistanceUnit(String distanceUnit) {
        DistanceUnit = distanceUnit;
    }
}
