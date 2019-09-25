package sg.go.user.Models;

public class ItemGetstartedPicture {
    int picture_viewpager_started;
    String name_picture_viewpager_started;

    public String getName_picture_viewpager_started() {
        return name_picture_viewpager_started;
    }

    public void setName_picture_viewpager_started(String name_picture_viewpager_started) {
        this.name_picture_viewpager_started = name_picture_viewpager_started;
    }

    public ItemGetstartedPicture(int img_viewpager_one) {

        this.picture_viewpager_started = img_viewpager_one;

    }

    public int getPicture_viewpager_started() {
        return picture_viewpager_started;
    }

    public void setPicture_viewpager_started(int picture_viewpager_started) {
        this.picture_viewpager_started = picture_viewpager_started;
    }
}
