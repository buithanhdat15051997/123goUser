package sg.go.user.Models;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Nurse extends RealmObject {

    @PrimaryKey
    private int id;

    private String mFullName;
    private String mContact_Name;
    private String mContact_Number;
    private String mPreferred_Username;
    private String mMobile;
    private String mEmail;
    private String mAddress;
    private String mPictureUrl;
    private String mTimezone;
    private String mGender;
    private String mCountry;
    private String mCurrencyCode;
    private int mOperatorID;

    public int getmOperatorID() {
        return mOperatorID;
    }

    public void setmOperatorID(int mOperatorID) {
        this.mOperatorID = mOperatorID;
    }

    public Nurse() {

    }

    public Nurse(int id,
                 String mFullName,
                 String mContact_Name,
                 String mContact_Number,
                 String mPreferred_Username,
                 String mMobile,
                 String mEmail,
                 String mAddress,
                 String mPictureUrl,
                 String mTimezone,
                 String mGender,
                 String mCountry,
                 String mCurrencyCode) {

        this.id = id;
        this.mFullName = mFullName;
        this.mContact_Name = mContact_Name;
        this.mContact_Number = mContact_Number;
        this.mPreferred_Username = mPreferred_Username;
        this.mMobile = mMobile;
        this.mEmail = mEmail;
        this.mAddress = mAddress;
        this.mPictureUrl = mPictureUrl;
        this.mTimezone = mTimezone;
        this.mGender = mGender;
        this.mCountry = mCountry;
        this.mCurrencyCode = mCurrencyCode;

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getmFullName() {
        return mFullName;
    }

    public void setmFullName(String mFullName) {
        this.mFullName = mFullName;
    }

    public String getmContact_Name() {
        return mContact_Name;
    }

    public void setmContact_Name(String mContact_Name) {
        this.mContact_Name = mContact_Name;
    }

    public String getmContact_Number() {
        return mContact_Number;
    }

    public void setmContact_Number(String mContact_Number) {
        this.mContact_Number = mContact_Number;
    }

    public String getmPreferred_Username() {
        return mPreferred_Username;
    }

    public void setmPreferred_Username(String mPreferred_Username) {
        this.mPreferred_Username = mPreferred_Username;
    }

    public String getmMobile() {
        return mMobile;
    }

    public void setmMobile(String mMobile) {
        this.mMobile = mMobile;
    }

    public String getmEmail() {
        return mEmail;
    }

    public void setmEmail(String mEmail) {
        this.mEmail = mEmail;
    }

    public String getmAddress() {
        return mAddress;
    }

    public void setmAddress(String mAddress) {
        this.mAddress = mAddress;
    }

    public String getmPictureUrl() {
        return mPictureUrl;
    }

    public void setmPictureUrl(String mPictureUrl) {
        this.mPictureUrl = mPictureUrl;
    }

    public String getmTimezone() {
        return mTimezone;
    }

    public void setmTimezone(String mTimezone) {
        this.mTimezone = mTimezone;
    }

    public String getmGender() {
        return mGender;
    }

    public void setmGender(String mGender) {
        this.mGender = mGender;
    }

    public String getmCountry() {
        return mCountry;
    }

    public void setmCountry(String mCountry) {
        this.mCountry = mCountry;
    }

    public String getmCurrencyCode() {
        return mCurrencyCode;
    }

    public void setmCurrencyCode(String mCurrencyCode) {
        this.mCurrencyCode = mCurrencyCode;
    }
}
