package sg.go.user.Models;

public class DistanceMatrix {

    private String mDestinationAddresses;
    private String mOriginAddresses;
    private String mDistanceText;
    private String mDistanceValue;
    private String mDurationText;
    private String mDurationValue;

    public DistanceMatrix() {
    }

    public DistanceMatrix(String mDestinationAddresses,
                          String mOriginAddresses,
                          String mDistanceText,
                          String mDistanceValue,
                          String mDurationText,
                          String mDurationValue) {
        this.mDestinationAddresses = mDestinationAddresses;
        this.mOriginAddresses = mOriginAddresses;
        this.mDistanceText = mDistanceText;
        this.mDistanceValue = mDistanceValue;
        this.mDurationText = mDurationText;
        this.mDurationValue = mDurationValue;
    }

    public String getmDestinationAddresses() {
        return mDestinationAddresses;
    }

    public void setmDestinationAddresses(String mDestinationAddresses) {
        this.mDestinationAddresses = mDestinationAddresses;
    }

    public String getmOriginAddresses() {
        return mOriginAddresses;
    }

    public void setmOriginAddresses(String mOriginAddresses) {
        this.mOriginAddresses = mOriginAddresses;
    }

    public String getmDistanceText() {
        return mDistanceText;
    }

    public void setmDistanceText(String mDistanceText) {
        this.mDistanceText = mDistanceText;
    }

    public String getmDistanceValue() {
        return mDistanceValue;
    }

    public void setmDistanceValue(String mDistanceValue) {
        this.mDistanceValue = mDistanceValue;
    }

    public String getmDurationText() {
        return mDurationText;
    }

    public void setmDurationText(String mDurationText) {
        this.mDurationText = mDurationText;
    }

    public String getmDurationValue() {
        return mDurationValue;
    }

    public void setmDurationValue(String mDurationValue) {
        this.mDurationValue = mDurationValue;
    }
}
