package sg.go.user.Models;

import android.os.Parcel;
import android.os.Parcelable;

public class RequestOptional implements Parcelable {

    private String id;
    private String time;
    private String token;
    private double pic_lat;
    private double pic_lng;
    private String pic_address;
    private double drop_lat;
    private double drop_lng;
    private String drop_address;
    private int isAddStop;
    private double addStop_lat;
    private double addStop_lng;
    private String addStop_address;
    private int operator_id;
    private int request_status_type;
    private String promoCode = "";
    private String remark = "";
    private String houseUnit;
    private int a_and_e;
    private int imh;
    private int ferry_terminals;
    private int staircase;
    private int tarmac;
    private int family_member;
    private int weight;
    private int oxygen;
    private int caseType;

    private String total_money_price;

    public String getTotal_money_price() {
        return total_money_price;
    }

    public void setTotal_money_price(String total_money_price) {
        this.total_money_price = total_money_price;
    }

    private String Time_send_billinginfo;
    private String km_send_billinginfo;
    private  String imgType_send_billinginfo;
    private  String nameType_send_billinginfo;

    public String getImgType_send_billinginfo() {
        return imgType_send_billinginfo;
    }

    public void setImgType_send_billinginfo(String imgType_send_billinginfo) {
        this.imgType_send_billinginfo = imgType_send_billinginfo;
    }

    public String getNameType_send_billinginfo() {
        return nameType_send_billinginfo;
    }

    public void setNameType_send_billinginfo(String nameType_send_billinginfo) {
        this.nameType_send_billinginfo = nameType_send_billinginfo;
    }



    public String getKm_send_billinginfo() {
        return km_send_billinginfo;
    }

    public void setKm_send_billinginfo(String km_send_billinginfo) {
        this.km_send_billinginfo = km_send_billinginfo;
    }

    public String getTime_send_billinginfo() {
        return Time_send_billinginfo;
    }

    public void setTime_send_billinginfo(String Time_send_billinginfo) {
        this.Time_send_billinginfo = Time_send_billinginfo;
    }

    public static Creator<RequestOptional> getCREATOR() {
        return CREATOR;
    }

    private  String OverView_Polyline;

    public String getOverView_Polyline() {
        return OverView_Polyline;
    }

    public void setOverView_Polyline(String overView_Polyline) {
        OverView_Polyline = overView_Polyline;
    }

    public RequestOptional() {

    }

    public RequestOptional(String id,
                           String time,
                           String token,
                           double pic_lat,
                           double pic_lng,
                           String pic_address,
                           double drop_lat,
                           double drop_lng,
                           String drop_address,
                           int isAddStop,
                           double addStop_lat,
                           double addStop_lng,
                           String addStop_address,
                           int operator_id,
                           int request_status_type,
                           String promoCode,
                           String remark,
                           String houseUnit,
                           int a_and_e,
                           int imh,
                           int ferry_terminals,
                           int staircase,
                           int tarmac,
                           int family_member,
                           int weight,
                           int oxygen,
                           int caseType) {
        this.id = id;
        this.time = time;
        this.token = token;
        this.pic_lat = pic_lat;
        this.pic_lng = pic_lng;
        this.pic_address = pic_address;
        this.drop_lat = drop_lat;
        this.drop_lng = drop_lng;
        this.drop_address = drop_address;
        this.isAddStop = isAddStop;
        this.addStop_lat = addStop_lat;
        this.addStop_lng = addStop_lng;
        this.addStop_address = addStop_address;
        this.operator_id = operator_id;
        this.request_status_type = request_status_type;
        this.promoCode = promoCode;
        this.remark = remark;
        this.houseUnit = houseUnit;
        this.a_and_e = a_and_e;
        this.imh = imh;
        this.ferry_terminals = ferry_terminals;
        this.staircase = staircase;
        this.tarmac = tarmac;
        this.family_member = family_member;
        this.weight = weight;
        this.oxygen = oxygen;
        this.caseType = caseType;
    }


    protected RequestOptional(Parcel in) {
        id = in.readString();
        time = in.readString();
        token = in.readString();
        pic_lat = in.readDouble();
        pic_lng = in.readDouble();
        pic_address = in.readString();
        drop_lat = in.readDouble();
        drop_lng = in.readDouble();
        drop_address = in.readString();
        isAddStop = in.readInt();
        addStop_lat = in.readDouble();
        addStop_lng = in.readDouble();
        addStop_address = in.readString();
        operator_id = in.readInt();
        request_status_type = in.readInt();
        promoCode = in.readString();
        remark = in.readString();
        houseUnit = in.readString();
        a_and_e = in.readInt();
        imh = in.readInt();
        ferry_terminals = in.readInt();
        staircase = in.readInt();
        tarmac = in.readInt();
        family_member = in.readInt();
        weight = in.readInt();
        oxygen = in.readInt();
        caseType = in.readInt();
    }

    public static final Creator<RequestOptional> CREATOR = new Creator<RequestOptional>() {
        @Override
        public RequestOptional createFromParcel(Parcel in) {
            return new RequestOptional(in);
        }

        @Override
        public RequestOptional[] newArray(int size) {
            return new RequestOptional[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(time);
        dest.writeString(token);
        dest.writeDouble(pic_lat);
        dest.writeDouble(pic_lng);
        dest.writeString(pic_address);
        dest.writeDouble(drop_lat);
        dest.writeDouble(drop_lng);
        dest.writeString(drop_address);
        dest.writeInt(isAddStop);
        dest.writeDouble(addStop_lat);
        dest.writeDouble(addStop_lng);
        dest.writeString(addStop_address);
        dest.writeInt(operator_id);
        dest.writeInt(request_status_type);
        dest.writeString(promoCode);
        dest.writeString(remark);
        dest.writeString(houseUnit);
        dest.writeInt(a_and_e);
        dest.writeInt(imh);
        dest.writeInt(ferry_terminals);
        dest.writeInt(staircase);
        dest.writeInt(tarmac);
        dest.writeInt(family_member);
        dest.writeInt(weight);
        dest.writeInt(oxygen);
        dest.writeInt(caseType);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public double getPic_lat() {
        return pic_lat;
    }

    public void setPic_lat(double pic_lat) {
        this.pic_lat = pic_lat;
    }

    public double getPic_lng() {
        return pic_lng;
    }

    public void setPic_lng(double pic_lng) {
        this.pic_lng = pic_lng;
    }

    public String getPic_address() {
        return pic_address;
    }

    public void setPic_address(String pic_address) {
        this.pic_address = pic_address;
    }

    public double getDrop_lat() {
        return drop_lat;
    }

    public void setDrop_lat(double drop_lat) {
        this.drop_lat = drop_lat;
    }

    public double getDrop_lng() {
        return drop_lng;
    }

    public void setDrop_lng(double drop_lng) {
        this.drop_lng = drop_lng;
    }

    public String getDrop_address() {
        return drop_address;
    }

    public void setDrop_address(String drop_address) {
        this.drop_address = drop_address;
    }

    public int getIsAddStop() {
        return isAddStop;
    }

    public void setIsAddStop(int isAddStop) {
        this.isAddStop = isAddStop;
    }

    public double getAddStop_lat() {
        return addStop_lat;
    }

    public void setAddStop_lat(double addStop_lat) {
        this.addStop_lat = addStop_lat;
    }

    public double getAddStop_lng() {
        return addStop_lng;
    }

    public void setAddStop_lng(double addStop_lng) {
        this.addStop_lng = addStop_lng;
    }

    public String getAddStop_address() {
        return addStop_address;
    }

    public void setAddStop_address(String addStop_address) {
        this.addStop_address = addStop_address;
    }

    public int getOperator_id() {
        return operator_id;
    }

    public void setOperator_id(int operator_id) {
        this.operator_id = operator_id;
    }

    public int getRequest_status_type() {
        return request_status_type;
    }

    public void setRequest_status_type(int request_status_type) {
        this.request_status_type = request_status_type;
    }

    public String getPromoCode() {
        return promoCode;
    }

    public void setPromoCode(String promoCode) {
        this.promoCode = promoCode;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getHouseUnit() {
        return houseUnit;
    }

    public void setHouseUnit(String houseUnit) {
        this.houseUnit = houseUnit;
    }

    public int getA_and_e() {
        return a_and_e;
    }

    public void setA_and_e(int a_and_e) {
        this.a_and_e = a_and_e;
    }

    public int getImh() {
        return imh;
    }

    public void setImh(int imh) {
        this.imh = imh;
    }

    public int getFerry_terminals() {
        return ferry_terminals;
    }

    public void setFerry_terminals(int ferry_terminals) {
        this.ferry_terminals = ferry_terminals;
    }

    public int getStaircase() {
        return staircase;
    }

    public void setStaircase(int staircase) {
        this.staircase = staircase;
    }

    public int getTarmac() {
        return tarmac;
    }

    public void setTarmac(int tarmac) {
        this.tarmac = tarmac;
    }

    public int getFamily_member() {
        return family_member;
    }

    public void setFamily_member(int family_member) {
        this.family_member = family_member;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public int getOxygen() {
        return oxygen;
    }

    public void setOxygen(int oxygen) {
        this.oxygen = oxygen;
    }

    public int getCaseType() {
        return caseType;
    }

    public void setCaseType(int caseType) {
        this.caseType = caseType;
    }


}
