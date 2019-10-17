package sg.go.user.Models;

import java.io.Serializable;

/**
 * Created by user on 1/17/2017.
 */
@SuppressWarnings("serial")
public class RequestDetail implements Serializable {

    private int requestId;
    private int tripStatus;
    private int driverStatus;
    private String driver_name, driver_picture, driver_car_picture, driver_car_number, driver_car_model, driver_car_color, driver_mobile, driver_rating,driver_id, vehical_img;
    private String s_lat, s_lng, d_lat, d_lng, s_address, d_address;
    private double driver_latitude, driver_longitude;
    private String request_type,no_tolls,distance_unit,cancellationFee;
    private String trip_time,trip_distance,trip_total_price,trip_base_price,payment_mode,currnecy_unit;
    private String time_price, tax_price, lift_landing, weight, oxygen_tank, trips;
    private String adStopLatitude;
    private String adStopLongitude,isAdStop,adStopAddress,isAddressChanged;
    private String later;
    private String distance_price,polyline_string;
    private String a_and_e, imh, ferry_terminals, staircase, tarmac, pickup_type;

    public String getPolyline_string() {
        return polyline_string;
    }

    public void setPolyline_string(String polyline_string) {
        this.polyline_string = polyline_string;
    }

    public RequestDetail() {

    }

    public String getA_and_e() {
        return a_and_e;
    }

    public void setA_and_e(String a_and_e) {
        this.a_and_e = a_and_e;
    }

    public String getImh() {
        return imh;
    }

    public void setImh(String imh) {
        this.imh = imh;
    }

    public String getFerry_terminals() {
        return ferry_terminals;
    }

    public void setFerry_terminals(String ferry_terminals) {
        this.ferry_terminals = ferry_terminals;
    }

    public String getStaircase() {
        return staircase;
    }

    public void setStaircase(String staircase) {
        this.staircase = staircase;
    }

    public String getTarmac() {
        return tarmac;
    }

    public void setTarmac(String tarmac) {
        this.tarmac = tarmac;
    }

    public String getPickup_type() {
        return pickup_type;
    }

    public void setPickup_type(String pickup_type) {
        this.pickup_type = pickup_type;
    }

    public String getDistance_price() {
        return distance_price;
    }

    public void setDistance_price(String distance_price) {
        this.distance_price = distance_price;
    }

    public String getLater() {
        return later;
    }

    public void setLater(String later) {
        this.later = later;
    }


    public int getRequestId() {
        return requestId;
    }

    public void setRequestId(int requestId) {
        this.requestId = requestId;
    }

    public int getTripStatus() {
        return tripStatus;
    }

    public void setTripStatus(int tripStatus) {
        this.tripStatus = tripStatus;
    }

    public String getDriver_name() {
        return driver_name;
    }

    public void setDriver_name(String driver_name) {
        this.driver_name = driver_name;
    }

    public String getDriver_picture() {
        return driver_picture;
    }

    public void setDriver_picture(String driver_picture) {
        this.driver_picture = driver_picture;
    }

    public String getDriver_mobile() {
        return driver_mobile;
    }

    public void setDriver_mobile(String driver_mobile) {
        this.driver_mobile = driver_mobile;
    }

    public String getDriver_rating() {
        return driver_rating;
    }

    public void setDriver_rating(String driver_rating) {
        this.driver_rating = driver_rating;
    }

    public String getS_lat() {
        return s_lat;
    }

    public void setS_lat(String s_lat) {
        this.s_lat = s_lat;
    }

    public String getS_lng() {
        return s_lng;
    }

    public void setS_lng(String s_lng) {
        this.s_lng = s_lng;
    }

    public String getD_lat() {
        return d_lat;
    }

    public void setD_lat(String d_lat) {
        this.d_lat = d_lat;
    }

    public String getD_lng() {
        return d_lng;
    }

    public void setD_lng(String d_lng) {
        this.d_lng = d_lng;
    }

    public String getS_address() {
        return s_address;
    }

    public void setS_address(String s_address) {
        this.s_address = s_address;
    }

    public String getD_address() {
        return d_address;
    }

    public void setD_address(String d_address) {
        this.d_address = d_address;
    }

    public String getDriver_id() {
        return driver_id;
    }

    public void setDriver_id(String driver_id) {
        this.driver_id = driver_id;
    }

    public double getDriver_latitude() {
        return driver_latitude;
    }

    public void setDriver_latitude(double driver_latitude) {
        this.driver_latitude = driver_latitude;
    }

    public double getDriver_longitude() {
        return driver_longitude;
    }

    public void setDriver_longitude(double driver_longitude) {
        this.driver_longitude = driver_longitude;
    }

    public String getTrip_time() {
        return trip_time;
    }

    public void setTrip_time(String trip_time) {
        this.trip_time = trip_time;
    }

    public String getTrip_distance() {
        return trip_distance;
    }

    public void setTrip_distance(String trip_distance) {
        this.trip_distance = trip_distance;
    }

    public String getTrip_total_price() {
        return trip_total_price;
    }

    public void setTrip_total_price(String trip_total_price) {
        this.trip_total_price = trip_total_price;
    }

    public String getTrip_base_price() {
        return trip_base_price;
    }

    public void setTrip_base_price(String trip_base_price) {
        this.trip_base_price = trip_base_price;
    }

    public String getPayment_mode() {
        return payment_mode;
    }

    public void setPayment_mode(String payment_mode) {
        this.payment_mode = payment_mode;
    }

    public String getVehical_img() {
        return vehical_img;
    }

    public void setVehical_img(String vehical_img) {
        this.vehical_img = vehical_img;
    }

    public int getDriverStatus() {
        return driverStatus;
    }

    public void setDriverStatus(int driverStatus) {
        this.driverStatus = driverStatus;
    }

    public String getRequest_type() {
        return request_type;
    }

    public void setRequest_type(String request_type) {
        this.request_type = request_type;
    }

    public String getNo_tolls() {
        return no_tolls;
    }

    public void setNo_tolls(String no_tolls) {
        this.no_tolls = no_tolls;
    }

    public String getCurrnecy_unit() {
        return currnecy_unit;
    }

    public void setCurrnecy_unit(String currnecy_unit) {
        this.currnecy_unit = currnecy_unit;
    }

    public String getDistance_unit() {
        return distance_unit;
    }

    public void setDistance_unit(String distance_unit) {
        this.distance_unit = distance_unit;
    }

    public String getCancellationFee() {
        return cancellationFee;
    }

    public void setCancellationFee(String cancellationFee) {
        this.cancellationFee = cancellationFee;
    }

    public String getDriver_car_picture() {
        return driver_car_picture;
    }

    public void setDriver_car_picture(String driver_car_picture) {
        this.driver_car_picture = driver_car_picture;
    }

    public String getDriver_car_number() {
        return driver_car_number;
    }

    public void setDriver_car_number(String driver_car_number) {
        this.driver_car_number = driver_car_number;
    }

    public String getDriver_car_model() {
        return driver_car_model;
    }

    public void setDriver_car_model(String driver_car_model) {
        this.driver_car_model = driver_car_model;
    }

    public String getDriver_car_color() {
        return driver_car_color;
    }

    public void setDriver_car_color(String driver_car_color) {
        this.driver_car_color = driver_car_color;
    }

    public String getAdStopLatitude() {
        return adStopLatitude;
    }

    public void setAdStopLatitude(String adStopLatitude) {
        this.adStopLatitude = adStopLatitude;
    }

    public String getAdStopLongitude() {
        return adStopLongitude;
    }

    public void setAdStopLongitude(String adStopLongitude) {
        this.adStopLongitude = adStopLongitude;
    }

    public String getIsAdStop() {
        return isAdStop;
    }

    public void setIsAdStop(String isAdStop) {
        this.isAdStop = isAdStop;
    }

    public String getAdStopAddress() {
        return adStopAddress;
    }

    public void setAdStopAddress(String adStopAddress) {
        this.adStopAddress = adStopAddress;
    }

    public String getIsAddressChanged() {
        return isAddressChanged;
    }

    public void setIsAddressChanged(String isAddressChanged) {
        this.isAddressChanged = isAddressChanged;
    }

    public String getTime_price() {
        return time_price;
    }

    public void setTime_price(String time_price) {
        this.time_price = time_price;
    }

    public String getTax_price() {
        return tax_price;
    }

    public void setTax_price(String tax_price) {
        this.tax_price = tax_price;
    }

    public String getLift_landing() {
        return lift_landing;
    }

    public void setLift_landing(String lift_landing) {
        this.lift_landing = lift_landing;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getOxygen_tank() {
        return oxygen_tank;
    }

    public void setOxygen_tank(String oxygen_tank) {
        this.oxygen_tank = oxygen_tank;
    }

    public String getTrips() {
        return trips;
    }

    public void setTrips(String trips) {
        this.trips = trips;
    }
}
