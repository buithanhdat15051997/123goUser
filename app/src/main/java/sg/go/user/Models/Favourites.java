package sg.go.user.Models;

/**
 * Created by asher on 2/7/2018.
 */

public class Favourites {
    private String fav_Name;
    private String fav_Address;
    private String fav_Latitude;
    private String fav_Longitude;
    private String fav_Id;

    public String getFav_Name() {
        return fav_Name;
    }

    public void setFav_Name(String fav_Name) {
        this.fav_Name = fav_Name;
    }

    public String getFav_Address() {
        return fav_Address;
    }

    public void setFav_Address(String fav_Address) {
        this.fav_Address = fav_Address;
    }

    public String getFav_Latitude() {
        return fav_Latitude;
    }

    public void setFav_Latitude(String fav_Latitude) {
        this.fav_Latitude = fav_Latitude;
    }

    public String getFav_Longitude() {
        return fav_Longitude;
    }

    public void setFav_Longitude(String fav_Longitude) {
        this.fav_Longitude = fav_Longitude;
    }

    public String getFav_Id() {
        return fav_Id;
    }

    public void setFav_Id(String fav_Id) {
        this.fav_Id = fav_Id;
    }
}
