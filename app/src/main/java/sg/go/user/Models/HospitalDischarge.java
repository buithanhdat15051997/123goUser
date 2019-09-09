package sg.go.user.Models;

import android.os.Parcel;
import android.os.Parcelable;

public class HospitalDischarge implements Parcelable {

    private String patientName;
    private String wardNumber;
    private String hospital;
    private String roomNumber;
    private String bedNumber;
    private String timeOfDischarge;

    public HospitalDischarge() {
    }

    protected HospitalDischarge(Parcel in) {
        patientName = in.readString();
        wardNumber = in.readString();
        hospital = in.readString();
        roomNumber = in.readString();
        bedNumber = in.readString();
        timeOfDischarge = in.readString();
    }

    public static final Creator<HospitalDischarge> CREATOR = new Creator<HospitalDischarge>() {
        @Override
        public HospitalDischarge createFromParcel(Parcel in) {
            return new HospitalDischarge(in);
        }

        @Override
        public HospitalDischarge[] newArray(int size) {
            return new HospitalDischarge[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(patientName);
        dest.writeString(wardNumber);
        dest.writeString(hospital);
        dest.writeString(roomNumber);
        dest.writeString(bedNumber);
        dest.writeString(timeOfDischarge);
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getWardNumber() {
        return wardNumber;
    }

    public void setWardNumber(String wardNumber) {
        this.wardNumber = wardNumber;
    }

    public String getHospital() {
        return hospital;
    }

    public void setHospital(String hospital) {
        this.hospital = hospital;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public String getBedNumber() {
        return bedNumber;
    }

    public void setBedNumber(String bedNumber) {
        this.bedNumber = bedNumber;
    }

    public String getTimeOfDischarge() {
        return timeOfDischarge;
    }

    public void setTimeOfDischarge(String timeOfDischarge) {
        this.timeOfDischarge = timeOfDischarge;
    }
}
