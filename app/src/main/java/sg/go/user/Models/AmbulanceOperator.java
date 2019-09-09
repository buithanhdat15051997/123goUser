package sg.go.user.Models;

/**
 * Created by user on 1/9/2017.
 */

public class AmbulanceOperator {
    private String id;
    private String ambulanceOperator;
    private String ambulanceImage;
    private String ambulanceCost;
    private String ambulanceSeats;
    private String ambulance_price_min,currencey_unit;
    private String ambulance_price_distance,basefare;

    public String getAmbulanceOperator() {
        return ambulanceOperator;
    }

    public void setAmbulanceOperator(String ambulanceOperator) {
        this.ambulanceOperator = ambulanceOperator;
    }

    public String getAmbulanceImage() {
        return ambulanceImage;
    }

    public void setAmbulanceImage(String ambulanceImage) {
        this.ambulanceImage = ambulanceImage;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAmbulanceCost() {
        return ambulanceCost;
    }

    public void setAmbulanceCost(String ambulanceCost) {
        this.ambulanceCost = ambulanceCost;
    }

    public String getAmbulanceSeats() {
        return ambulanceSeats;
    }

    public void setAmbulanceSeats(String ambulanceSeats) {
        this.ambulanceSeats = ambulanceSeats;
    }

    public String getAmbulance_price_min() {
        return ambulance_price_min;
    }

    public void setAmbulance_price_min(String ambulance_price_min) {
        this.ambulance_price_min = ambulance_price_min;
    }

    public String getAmbulance_price_distance() {
        return ambulance_price_distance;
    }

    public void setAmbulance_price_distance(String ambulance_price_distance) {
        this.ambulance_price_distance = ambulance_price_distance;
    }

    public String getBasefare() {
        return basefare;
    }

    public void setBasefare(String basefare) {
        this.basefare = basefare;
    }

    public String getCurrencey_unit() {
        return currencey_unit;
    }

    public void setCurrencey_unit(String currencey_unit) {
        this.currencey_unit = currencey_unit;
    }
}
