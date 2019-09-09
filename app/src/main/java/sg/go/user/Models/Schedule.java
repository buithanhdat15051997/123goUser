package sg.go.user.Models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by user on 2/3/2017.
 */

public class Schedule implements Parcelable {
    private String request_id, request_date, request_type, request_pic, s_lat, s_lng, d_lat, d_lng, s_address,d_address, status_request;
    private String patient_name, purpose, special_request, hospital;
    private int room_number;
    private int floor_number;
    private int bed_number;
    private String ward;
    private String houseUnit;
    private int trips, status, lift_landing, weight, familyMember;
    private int assistive_device, oxygen_tank, case_type, pickup_type;
    private int a_and_e, imh, ferry_terminals, staircase, tarmac;
    private String distance;
    private String duration;
    private String operatorID;

    public Schedule() {
    }

    public Schedule(String request_id,
                    String request_date,
                    String request_type,
                    String request_pic,
                    String s_lat,
                    String s_lng,
                    String d_lat,
                    String d_lng,
                    String s_address,
                    String d_address,
                    String status_request,
                    String patient_name,
                    String purpose,
                    String special_request,
                    String hospital,
                    int room_number,
                    int floor_number,
                    int bed_number,
                    String ward,
                    String houseUnit,
                    int trips,
                    int status,
                    int lift_landing,
                    int weight,
                    int familyMember,
                    int assistive_device,
                    int oxygen_tank,
                    int case_type,
                    int pickup_type,
                    int a_and_e,
                    int imh,
                    int ferry_terminals,
                    int staircase,
                    int tarmac,
                    String distance,
                    String duration,
                    String operatorID) {

        this.request_id = request_id;
        this.request_date = request_date;
        this.request_type = request_type;
        this.request_pic = request_pic;
        this.s_lat = s_lat;
        this.s_lng = s_lng;
        this.d_lat = d_lat;
        this.d_lng = d_lng;
        this.s_address = s_address;
        this.d_address = d_address;
        this.status_request = status_request;
        this.patient_name = patient_name;
        this.purpose = purpose;
        this.special_request = special_request;
        this.hospital = hospital;
        this.room_number = room_number;
        this.floor_number = floor_number;
        this.bed_number = bed_number;
        this.ward = ward;
        this.houseUnit = houseUnit;
        this.trips = trips;
        this.status = status;
        this.lift_landing = lift_landing;
        this.weight = weight;
        this.familyMember = familyMember;
        this.assistive_device = assistive_device;
        this.oxygen_tank = oxygen_tank;
        this.case_type = case_type;
        this.pickup_type = pickup_type;
        this.a_and_e = a_and_e;
        this.imh = imh;
        this.ferry_terminals = ferry_terminals;
        this.staircase = staircase;
        this.tarmac = tarmac;
        this.distance = distance;
        this.duration = duration;
        this.operatorID = operatorID;

    }

    protected Schedule(Parcel in) {
        request_id = in.readString();
        request_date = in.readString();
        request_type = in.readString();
        request_pic = in.readString();
        s_lat = in.readString();
        s_lng = in.readString();
        d_lat = in.readString();
        d_lng = in.readString();
        s_address = in.readString();
        d_address = in.readString();
        status_request = in.readString();
        patient_name = in.readString();
        purpose = in.readString();
        special_request = in.readString();
        hospital = in.readString();
        room_number = in.readInt();
        floor_number = in.readInt();
        bed_number = in.readInt();
        ward = in.readString();
        houseUnit = in.readString();
        trips = in.readInt();
        status = in.readInt();
        lift_landing = in.readInt();
        weight = in.readInt();
        familyMember = in.readInt();
        assistive_device = in.readInt();
        oxygen_tank = in.readInt();
        case_type = in.readInt();
        pickup_type = in.readInt();
        a_and_e = in.readInt();
        imh = in.readInt();
        ferry_terminals = in.readInt();
        staircase = in.readInt();
        tarmac = in.readInt();
        distance = in.readString();
        duration = in.readString();
        operatorID = in.readString();
    }

    public static final Creator<Schedule> CREATOR = new Creator<Schedule>() {
        @Override
        public Schedule createFromParcel(Parcel in) {
            return new Schedule(in);
        }

        @Override
        public Schedule[] newArray(int size) {
            return new Schedule[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(request_id);
        dest.writeString(request_date);
        dest.writeString(request_type);
        dest.writeString(request_pic);
        dest.writeString(s_lat);
        dest.writeString(s_lng);
        dest.writeString(d_lat);
        dest.writeString(d_lng);
        dest.writeString(s_address);
        dest.writeString(d_address);
        dest.writeString(status_request);
        dest.writeString(patient_name);
        dest.writeString(purpose);
        dest.writeString(special_request);
        dest.writeString(hospital);
        dest.writeInt(room_number);
        dest.writeInt(floor_number);
        dest.writeInt(bed_number);
        dest.writeString(ward);
        dest.writeString(houseUnit);
        dest.writeInt(trips);
        dest.writeInt(status);
        dest.writeInt(lift_landing);
        dest.writeInt(weight);
        dest.writeInt(familyMember);
        dest.writeInt(assistive_device);
        dest.writeInt(oxygen_tank);
        dest.writeInt(case_type);
        dest.writeInt(pickup_type);
        dest.writeInt(a_and_e);
        dest.writeInt(imh);
        dest.writeInt(ferry_terminals);
        dest.writeInt(staircase);
        dest.writeInt(tarmac);
        dest.writeString(distance);
        dest.writeString(duration);
        dest.writeString(operatorID);
    }

    public String getOperatorID() {
        return operatorID;
    }

    public void setOperatorID(String operatorID) {
        this.operatorID = operatorID;
    }

    public int getFamilyMember() {
        return familyMember;
    }

    public void setFamilyMember(int familyMember) {
        this.familyMember = familyMember;
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

    public int getPickup_type() {
        return pickup_type;
    }

    public void setPickup_type(int pickup_type) {
        this.pickup_type = pickup_type;
    }
    public int getLift_landing() {
        return lift_landing;
    }

    public void setLift_landing(int lift_landing) {
        this.lift_landing = lift_landing;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public String getHouseUnit() {
        return houseUnit;
    }

    public void setHouseUnit(String houseUnit) {
        this.houseUnit = houseUnit;
    }

    public int getAssistive_device() {
        return assistive_device;
    }

    public void setAssistive_device(int assistive_device) {
        this.assistive_device = assistive_device;
    }

    public int getOxygen_tank() {
        return oxygen_tank;
    }

    public void setOxygen_tank(int oxygen_tank) {
        this.oxygen_tank = oxygen_tank;
    }

    public int getCase_type() {
        return case_type;
    }

    public void setCase_type(int case_type) {
        this.case_type = case_type;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getTrips() {
        return trips;
    }

    public void setTrips(int trips) {
        this.trips = trips;
    }

    public int getRoom_number() {
        return room_number;
    }

    public void setRoom_number(int room_number) {
        this.room_number = room_number;
    }

    public int getFloor_number() {
        return floor_number;
    }

    public void setFloor_number(int floor_number) {
        this.floor_number = floor_number;
    }

    public int getBed_number() {
        return bed_number;
    }

    public void setBed_number(int bed_number) {
        this.bed_number = bed_number;
    }

    public String getWard() {
        return ward;
    }

    public void setWard(String ward) {
        this.ward = ward;
    }

    public String getPatient_name() {
        return patient_name;
    }

    public void setPatient_name(String patient_name) {
        this.patient_name = patient_name;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public String getSpecial_request() {
        return special_request;
    }

    public void setSpecial_request(String special_request) {
        this.special_request = special_request;
    }

    public String getHospital() {
        return hospital;
    }

    public void setHospital(String hospital) {
        this.hospital = hospital;
    }

    public String getRequest_id() {
        return request_id;
    }

    public void setRequest_id(String request_id) {
        this.request_id = request_id;
    }

    public String getRequest_date() {
        return request_date;
    }

    public void setRequest_date(String request_date) {
        this.request_date = request_date;
    }

    public String getRequest_type() {
        return request_type;
    }

    public void setRequest_type(String request_type) {
        this.request_type = request_type;
    }

    public String getRequest_pic() {
        return request_pic;
    }

    public void setRequest_pic(String request_pic) {
        this.request_pic = request_pic;
    }

    public String getS_address() {
        return s_address;
    }

    public void setS_address(String s_address) {
        this.s_address = s_address;
    }

    public String getD_address() {
        return d_address;
    }

    public void setD_address(String d_address) {
        this.d_address = d_address;
    }

    public String getS_lat() {
        return s_lat;
    }

    public void setS_lat(String s_lat) {
        this.s_lat = s_lat;
    }

    public String getS_lng() {
        return s_lng;
    }

    public void setS_lng(String s_lng) {
        this.s_lng = s_lng;
    }

    public String getD_lat() {
        return d_lat;
    }

    public void setD_lat(String d_lat) {
        this.d_lat = d_lat;
    }

    public String getD_lng() {
        return d_lng;
    }

    public void setD_lng(String d_lng) {
        this.d_lng = d_lng;
    }

    public String getStatus_request() {
        return status_request;
    }

    public void setStatus_request(String status_request) {
        this.status_request = status_request;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

}
