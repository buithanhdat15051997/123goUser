package sg.go.user.Models;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by user on 1/11/2017.
 */

public class NearByDrivers {
    private String id;
    private LatLng latlan;
    private String driver_name;
    private String driver_distance;
    private int driver_rate;

    public LatLng getLatlan() {
        return latlan;
    }

    public void setLatlan(LatLng latlan) {
        this.latlan = latlan;
    }

    public String getDriver_name() {
        return driver_name;
    }

    public void setDriver_name(String driver_name) {
        this.driver_name = driver_name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getDriver_rate() {
        return driver_rate;
    }

    public void setDriver_rate(int driver_rate) {
        this.driver_rate = driver_rate;
    }

    public String getDriver_distance() {
        return driver_distance;
    }

    public void setDriver_distance(String driver_distance) {
        this.driver_distance = driver_distance;
    }
}
