package sg.go.user.Models;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class HistoryPayment extends RealmObject {

    @PrimaryKey
    private int mRequestID;

    private String mAmbulanceOperator;
    private String mPickupAddress;
    private String mDestinationAddress;
    private String mDateCreate;
    private String mBasePrice;
    private String mTimePrice;
    private String mLiftLanding;
    private String mWeight;
    private String mOxygenTank;
    private String mTrips;
    private String mPaymentType;
    private String mTotal;

    public HistoryPayment() {
    }

    public HistoryPayment(int mRequestID,
                          String mAmbulanceOperator,
                          String mPickupAddress,
                          String mDestinationAddress,
                          String mDateCreate,
                          String mBasePrice,
                          String mTimePrice,
                          String mLiftLanding,
                          String mWeight,
                          String mOxygenTank,
                          String mTrips,
                          String mPaymentType,
                          String mTotal) {

        this.mRequestID = mRequestID;
        this.mAmbulanceOperator = mAmbulanceOperator;
        this.mPickupAddress = mPickupAddress;
        this.mDestinationAddress = mDestinationAddress;
        this.mDateCreate = mDateCreate;
        this.mBasePrice = mBasePrice;
        this.mTimePrice = mTimePrice;
        this.mLiftLanding = mLiftLanding;
        this.mWeight = mWeight;
        this.mOxygenTank = mOxygenTank;
        this.mTrips = mTrips;
        this.mPaymentType = mPaymentType;
        this.mTotal = mTotal;

    }

    public int getmRequestID() {
        return mRequestID;
    }

    public void setmRequestID(int mRequestID) {
        this.mRequestID = mRequestID;
    }

    public String getmAmbulanceOperator() {
        return mAmbulanceOperator;
    }

    public void setmAmbulanceOperator(String mAmbulanceOperator) {
        this.mAmbulanceOperator = mAmbulanceOperator;
    }

    public String getmPickupAddress() {
        return mPickupAddress;
    }

    public void setmPickupAddress(String mPickupAddress) {
        this.mPickupAddress = mPickupAddress;
    }

    public String getmDestinationAddress() {
        return mDestinationAddress;
    }

    public void setmDestinationAddress(String mDestinationAddress) {
        this.mDestinationAddress = mDestinationAddress;
    }

    public String getmDateCreate() {
        return mDateCreate;
    }

    public void setmDateCreate(String mDateCreate) {
        this.mDateCreate = mDateCreate;
    }

    public String getmBasePrice() {
        return mBasePrice;
    }

    public void setmBasePrice(String mBasePrice) {
        this.mBasePrice = mBasePrice;
    }

    public String getmTimePrice() {
        return mTimePrice;
    }

    public void setmTimePrice(String mTimePrice) {
        this.mTimePrice = mTimePrice;
    }

    public String getmLiftLanding() {
        return mLiftLanding;
    }

    public void setmLiftLanding(String mLiftLanding) {
        this.mLiftLanding = mLiftLanding;
    }

    public String getmWeight() {
        return mWeight;
    }

    public void setmWeight(String mWeight) {
        this.mWeight = mWeight;
    }

    public String getmOxygenTank() {
        return mOxygenTank;
    }

    public void setmOxygenTank(String mOxygenTank) {
        this.mOxygenTank = mOxygenTank;
    }

    public String getmTrips() {
        return mTrips;
    }

    public void setmTrips(String mTrips) {
        this.mTrips = mTrips;
    }

    public String getmPaymentType() {
        return mPaymentType;
    }

    public void setmPaymentType(String mPaymentType) {
        this.mPaymentType = mPaymentType;
    }

    public String getmTotal() {
        return mTotal;
    }

    public void setmTotal(String mTotal) {
        this.mTotal = mTotal;
    }
}
