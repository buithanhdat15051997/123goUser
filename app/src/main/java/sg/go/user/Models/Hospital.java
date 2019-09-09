package sg.go.user.Models;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Hospital extends RealmObject {

    @PrimaryKey
    private int id;

    private String mHospitalName;
    private String mContactName;
    private String mContactNumber;
    private String mMobileNumber;
    private String mWard;
    private int mFloorNumber;
    private String mEmailAddress;
    private String mMainAddress;
    private String mPostal;
    private String mPreferredUsername;
    private int mAmbulanceOperator;
    private String mCompanyPicture;

    public Hospital() {
    }

    public Hospital(int id,
                    String mHospitalName,
                    String mContactName,
                    String mContactNumber,
                    String mMobileNumber,
                    String mWard,
                    int mFloorNumber,
                    String mEmailAddress,
                    String mMainAddress,
                    String mPostal,
                    String mPreferredUsername,
                    int mAmbulanceOperator,
                    String mCompanyPicture) {

        this.id = id;
        this.mHospitalName = mHospitalName;
        this.mContactName = mContactName;
        this.mContactNumber = mContactNumber;
        this.mMobileNumber = mMobileNumber;
        this.mWard = mWard;
        this.mFloorNumber = mFloorNumber;
        this.mEmailAddress = mEmailAddress;
        this.mMainAddress = mMainAddress;
        this.mPostal = mPostal;
        this.mPreferredUsername = mPreferredUsername;
        this.mAmbulanceOperator = mAmbulanceOperator;
        this.mCompanyPicture = mCompanyPicture;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getmHospitalName() {
        return mHospitalName;
    }

    public void setmHospitalName(String mHospitalName) {
        this.mHospitalName = mHospitalName;
    }

    public String getmContactName() {
        return mContactName;
    }

    public void setmContactName(String mContactName) {
        this.mContactName = mContactName;
    }

    public String getmContactNumber() {
        return mContactNumber;
    }

    public void setmContactNumber(String mContactNumber) {
        this.mContactNumber = mContactNumber;
    }

    public String getmMobileNumber() {
        return mMobileNumber;
    }

    public void setmMobileNumber(String mMobileNumber) {
        this.mMobileNumber = mMobileNumber;
    }

    public String getmWard() {
        return mWard;
    }

    public void setmWard(String mWard) {
        this.mWard = mWard;
    }

    public int getmFloorNumber() {
        return mFloorNumber;
    }

    public void setmFloorNumber(int mFloorNumber) {
        this.mFloorNumber = mFloorNumber;
    }

    public String getmEmailAddress() {
        return mEmailAddress;
    }

    public void setmEmailAddress(String mEmailAddress) {
        this.mEmailAddress = mEmailAddress;
    }

    public String getmMainAddress() {
        return mMainAddress;
    }

    public void setmMainAddress(String mMainAddress) {
        this.mMainAddress = mMainAddress;
    }

    public String getmPostal() {
        return mPostal;
    }

    public void setmPostal(String mPostal) {
        this.mPostal = mPostal;
    }

    public String getmPreferredUsername() {
        return mPreferredUsername;
    }

    public void setmPreferredUsername(String mPreferredUsername) {
        this.mPreferredUsername = mPreferredUsername;
    }

    public int getmAmbulanceOperator() {
        return mAmbulanceOperator;
    }

    public void setmAmbulanceOperator(int mAmbulanceOperator) {
        this.mAmbulanceOperator = mAmbulanceOperator;
    }

    public String getmCompanyPicture() {
        return mCompanyPicture;
    }

    public void setmCompanyPicture(String mCompanyPicture) {
        this.mCompanyPicture = mCompanyPicture;
    }
}
