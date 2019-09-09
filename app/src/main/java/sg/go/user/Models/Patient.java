package sg.go.user.Models;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Patient extends RealmObject {

    @PrimaryKey
    private int id;

    private String mFamilyMemberName;
    private String mFullname;
    private String mPicture;
    private String mMobile;
    private String mHomeNumber;
    private String mEmail;
    private int mIsRegistered;
    private int mOTP;
    private int mIsActivated;
    private String mGender;
    private String mPaypalEmail;
    private String mTimeZone;
    private String mCurrencyCode;
    private String mCountry;
    private String mReferralCode;
    private int mReferralBonus;
    private String mBlockNumber;
    private String mUnitNumber;
    private String mStreetName;
    private String mPostal;
    private double mWeight;
    private int mLiftLanding;
    private int mStairs;
    private int mNoStairs;
    private int mLowStairs;
    private String mPreferredUsername;
    private int mOperatorID;
    private String mPatientCondition; //As Good, Critical
    private int mStretcher;
    private int mWheelChair;
    private int mOxygen;
    private int mEscorts;
    private String mAddInformation;
    private String mPaymentMode; //As Cash, Monthly Bill, Digital Wallet, Credit CreditCards

    public Patient() {

    }

    public Patient(int id,
                   String mFamilyMemberName,
                   String mFullname,
                   String mMobile,
                   String mHomeNumber,
                   String mEmail,
                   int mIsRegistered,
                   int mOTP,
                   int mIsActivated,
                   String mGender,
                   String mPaypalEmail,
                   String mTimeZone,
                   String mCurrencyCode,
                   String mCountry,
                   String mReferralCode,
                   int mReferralBonus,
                   String mBlockNumber,
                   String mUnitNumber,
                   String mStreetName,
                   String mPostal,
                   double mWeight,
                   int mLiftLanding,
                   int mStairs,
                   int mNoStairs,
                   int mLowStairs,
                   String mPreferredUsername,
                   int mOperatorID,
                   String mPatientCondition,
                   int mStretcher,
                   int mWheelChair,
                   int mOxygen,
                   int mEscorts,
                   String mAddInformation,
                   String mPaymentMode,
                   String mPicture) {

        this.id = id;
        this.mFamilyMemberName = mFamilyMemberName;
        this.mFullname = mFullname;
        this.mMobile = mMobile;
        this.mHomeNumber = mHomeNumber;
        this.mEmail = mEmail;
        this.mIsRegistered = mIsRegistered;
        this.mOTP = mOTP;
        this.mIsActivated = mIsActivated;
        this.mGender = mGender;
        this.mPaypalEmail = mPaypalEmail;
        this.mTimeZone = mTimeZone;
        this.mCurrencyCode = mCurrencyCode;
        this.mCountry = mCountry;
        this.mReferralCode = mReferralCode;
        this.mReferralBonus = mReferralBonus;
        this.mBlockNumber = mBlockNumber;
        this.mUnitNumber = mUnitNumber;
        this.mStreetName = mStreetName;
        this.mPostal = mPostal;
        this.mWeight = mWeight;
        this.mLiftLanding = mLiftLanding;
        this.mStairs = mStairs;
        this.mNoStairs = mNoStairs;
        this.mLowStairs = mLowStairs;
        this.mPreferredUsername = mPreferredUsername;
        this.mOperatorID = mOperatorID;
        this.mPatientCondition = mPatientCondition;
        this.mStretcher = mStretcher;
        this.mWheelChair = mWheelChair;
        this.mOxygen = mOxygen;
        this.mEscorts = mEscorts;
        this.mAddInformation = mAddInformation;
        this.mPaymentMode = mPaymentMode;
        this.mPicture = mPicture;

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getmFamilyMemberName() {
        return mFamilyMemberName;
    }

    public void setmFamilyMemberName(String mFamilyMemberName) {
        this.mFamilyMemberName = mFamilyMemberName;
    }

    public String getmFullname() {
        return mFullname;
    }

    public void setmFullname(String mFullname) {
        this.mFullname = mFullname;
    }

    public String getmMobile() {
        return mMobile;
    }

    public void setmMobile(String mMobile) {
        this.mMobile = mMobile;
    }

    public String getmHomeNumber() {
        return mHomeNumber;
    }

    public void setmHomeNumber(String mHomeNumber) {
        this.mHomeNumber = mHomeNumber;
    }

    public String getmEmail() {
        return mEmail;
    }

    public void setmEmail(String mEmail) {
        this.mEmail = mEmail;
    }

    public int getmIsRegistered() {
        return mIsRegistered;
    }

    public void setmIsRegistered(int mIsRegistered) {
        this.mIsRegistered = mIsRegistered;
    }

    public int getmOTP() {
        return mOTP;
    }

    public void setmOTP(int mOTP) {
        this.mOTP = mOTP;
    }

    public int getmIsActivated() {
        return mIsActivated;
    }

    public void setmIsActivated(int mIsActivated) {
        this.mIsActivated = mIsActivated;
    }

    public String getmGender() {
        return mGender;
    }

    public void setmGender(String mGender) {
        this.mGender = mGender;
    }

    public String getmPaypalEmail() {
        return mPaypalEmail;
    }

    public void setmPaypalEmail(String mPaypalEmail) {
        this.mPaypalEmail = mPaypalEmail;
    }

    public String getmTimeZone() {
        return mTimeZone;
    }

    public void setmTimeZone(String mTimeZone) {
        this.mTimeZone = mTimeZone;
    }

    public String getmCurrencyCode() {
        return mCurrencyCode;
    }

    public void setmCurrencyCode(String mCurrencyCode) {
        this.mCurrencyCode = mCurrencyCode;
    }

    public String getmCountry() {
        return mCountry;
    }

    public void setmCountry(String mCountry) {
        this.mCountry = mCountry;
    }

    public String getmReferralCode() {
        return mReferralCode;
    }

    public void setmReferralCode(String mReferralCode) {
        this.mReferralCode = mReferralCode;
    }

    public int getmReferralBonus() {
        return mReferralBonus;
    }

    public void setmReferralBonus(int mReferralBonus) {
        this.mReferralBonus = mReferralBonus;
    }

    public String getmBlockNumber() {
        return mBlockNumber;
    }

    public void setmBlockNumber(String mBlockNumber) {
        this.mBlockNumber = mBlockNumber;
    }

    public String getmUnitNumber() {
        return mUnitNumber;
    }

    public void setmUnitNumber(String mUnitNumber) {
        this.mUnitNumber = mUnitNumber;
    }

    public String getmStreetName() {
        return mStreetName;
    }

    public void setmStreetName(String mStreetName) {
        this.mStreetName = mStreetName;
    }

    public String getmPostal() {
        return mPostal;
    }

    public void setmPostal(String mPostal) {
        this.mPostal = mPostal;
    }

    public double getmWeight() {
        return mWeight;
    }

    public void setmWeight(double mWeight) {
        this.mWeight = mWeight;
    }

    public int getmLiftLanding() {
        return mLiftLanding;
    }

    public void setmLiftLanding(int mLiftLanding) {
        this.mLiftLanding = mLiftLanding;
    }

    public int getmStairs() {
        return mStairs;
    }

    public void setmStairs(int mStairs) {
        this.mStairs = mStairs;
    }

    public int getmNoStairs() {
        return mNoStairs;
    }

    public void setmNoStairs(int mNoStairs) {
        this.mNoStairs = mNoStairs;
    }

    public int getmLowStairs() {
        return mLowStairs;
    }

    public void setmLowStairs(int mLowStairs) {
        this.mLowStairs = mLowStairs;
    }

    public String getmPreferredUsername() {
        return mPreferredUsername;
    }

    public void setmPreferredUsername(String mPreferredUsername) {
        this.mPreferredUsername = mPreferredUsername;
    }

    public int getmOperatorID() {
        return mOperatorID;
    }

    public void setmOperatorID(int mOperatorID) {
        this.mOperatorID = mOperatorID;
    }

    public String getmPatientCondition() {
        return mPatientCondition;
    }

    public void setmPatientCondition(String mPatientCondition) {
        this.mPatientCondition = mPatientCondition;
    }

    public int getmStretcher() {
        return mStretcher;
    }

    public void setmStretcher(int mStretcher) {
        this.mStretcher = mStretcher;
    }

    public int getmWheelChair() {
        return mWheelChair;
    }

    public void setmWheelChair(int mWheelChair) {
        this.mWheelChair = mWheelChair;
    }

    public int getmOxygen() {
        return mOxygen;
    }

    public void setmOxygen(int mOxygen) {
        this.mOxygen = mOxygen;
    }

    public int getmEscorts() {
        return mEscorts;
    }

    public void setmEscorts(int mEscorts) {
        this.mEscorts = mEscorts;
    }

    public String getmAddInformation() {
        return mAddInformation;
    }

    public void setmAddInformation(String mAddInformation) {
        this.mAddInformation = mAddInformation;
    }

    public String getmPaymentMode() {
        return mPaymentMode;
    }

    public void setmPaymentMode(String mPaymentMode) {
        this.mPaymentMode = mPaymentMode;
    }

    public String getmPicture() {
        return mPicture;
    }

    public void setmPicture(String mPicture) {
        this.mPicture = mPicture;
    }
}
