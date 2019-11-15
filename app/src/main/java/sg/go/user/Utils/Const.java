package sg.go.user.Utils;

/**
 * Created by getit on 8/5/2016.
 */
public class Const {


    public static String PREF_NAME = "123go";
    public static final int GET = 0;
    public static final int POST = 1;
    public static final int TIMEOUT = 30000;
    public static final int MAX_RETRY = 4;
    public static final float DEFAULT_BACKOFF_MULT = 1f;

    public static final int CHOOSE_PHOTO = 100;
    public static final int TAKE_PHOTO = 101;

    public static final String REQUEST_ACCEPT = "REQUEST_ACCEPT";
    public static final String REQUEST_CANCEL = "REQUEST_CANCEL";
    public static final int NO_REQUEST = -1;
    public static final String DRIVER_STATUS = "driverstatus";

    public static final long DELAY = 0;
    public static final long TIME_SCHEDULE = 5 * 1000;
    public static final long DELAY_OFFLINE = 15 * 60 * 1000;
    public static final long TIME_SCHEDULE_OFFLINE = 15 * 60 * 1000;

    /*public static final String PLACES_AUTOCOMPLETE_API_KEY = "AIzaSyC5fNTsNIv5Ji8AuOx-rJwouEAreUVC3s0"; //Need to replace
    public static final String GOOGLE_API_KEY = "AIzaSyDoujGbr86VY2F6vhh-bzZjsebCFoRn0ik"; //Need to replace*/

    /*public static final String PLACES_AUTOCOMPLETE_API_KEY = "AIzaSyDsXzZLQ-9sAR7EgBTQxay0ekjrxW-63_c"; //Need to replace
    public static final String GOOGLE_API_KEY = "AIzaSyDsXzZLQ-9sAR7EgBTQxay0ekjrxW-63_c"; //Need to replace*/

    public static final String GOOGLE_API_KEY = "AIzaSyDGMXyrnV4NT8YJZuoVEuY128rd5HfmEfA";
    public static final String GOOGLE_API_SERVER_KEY = "AIzaSyBIJRYgGt0vga6GSOCvLj-d7D2twyyaKQY";
    public static final String PLACES_AUTOCOMPLETE_API_KEY = "AIzaSyDGMXyrnV4NT8YJZuoVEuY128rd5HfmEfA";

    //Fragments
    public static final String WALLET_FRAGMENT = "wallet_fragment";
    public static final String HOME_MAP_FRAGMENT = "home_map_fragment";
    public static final String TRAVEL_MAP_FRAGMENT = "fragment_travel_map";
    public static final String RATING_FRAGMENT = "rating_fragment";
    public static final String REGISTER_FRAGMENT = "register_fragment";
    public static final String FORGOT_PASSWORD_FRAGMENT = "forgot_fragment";
    public static final String SEARCH_FRAGMENT = "search_fragment";
    public static final String REQUEST_FRAGMENT = "request_fragment";
    public static final String HISTORY_FRAGMENT = "history_fragment";
    public static final String PAYMENT_FRAGMENT = "payment_fragment";
    public static final String HOURLY_BOOKING_FRAGMENT = "hourly_booking_fragment";
    public static final String BOLT_FRAGMENT = "bolt_fragment";
    public static final String ACCOUNT_FRAGMENT = "acount_fragment";
    public static final String NURSE_REGISTER_SCHEDULE_FRAGMENT = "nurse_register_schedule_fragment";
    public static final String SCHEDULE_LIST_FRAGMENT = "list_schedule_fragment";

    public static final String CHOOSE_PAYMENT_FRAGMENT = "choose_payment_fragment";

    public static final String HISTORY_TOPUP_WALLET_FRAMENT = "history_topup_fragment";

    public static final String BILLING_INFO_FRAGMENT = "billing_info_fragment";

    public static final String HISTORY_PAYMENT_FRAGMENT = "history_payment_fragment";

    public static final String CHANGEPASSWORD = "changePassword_fragment";


    public static final String CANCELLATION_POLICY_DIALOGFRAGMENT = "cancellation_policy_dialogfragment";
    public static final String HOSPITAL_DISCHARGE_OPTION_DIALOGFRAGMENT = "hospital_discharge_option_dialogfragment";
    public static final String HOSPITAL_DISCHARGE_WARNING_DIALOGFRAGMENT = "hospital_discharge_warning_dialogfragment";

    //  Trip request status
    public static final int IS_CREATED = 0;
    public static final int IS_ACCEPTED = 1;


    public static final int IS_DRIVER_DEPARTED = 2;
    public static final int IS_DRIVER_ARRIVED = 3;
    public static final int IS_DRIVER_TRIP_STARTED = 4;
    public static final int IS_DRIVER_TRIP_ENDED = 5;
    public static final int IS_DRIVER_RATED = 6;
    public static final int IS_BILLING = 8;

    public static final String DEVICE_TYPE = "android";
    public static final String DEVICE_TYPE_ANDROID = "android";
    public static final String SOCIAL_FACEBOOK = "facebook";
    public static final String SOCIAL_GOOGLE = "google";
    public static final String MANUAL = "manual";
    public static final String SOCIAL = "social";
    public static final String REQUEST_DETAIL = "requestDetails";
    public static final String SCHEDULE = "schedule";
    public static final String HOSPITAL_DISCHARGE_OPTION = "hospital_discharge";

    public static final String paypal = "PAYPAL";
    public static final String paygate = "PAYGATE";
    public static final String stripe = "STRIPE";

    public static final String GOOGLE_MATRIX_URL = "https://maps.googleapis.com/maps/api/distancematrix/json?";

    public class GoogleAPIParams {

        public static final String DESTINATION_ADDRESSES = "destination_addresses";
        public static final String ORIGIN_ADDRESSES = "origin_addresses";
        public static final String ROWS = "rows";
        public static final String ELEMENTS = "elements";
        public static final String DISTANCE = "distance";
        public static final String TEXT = "text";
        public static final String VALUE = "value";
        public static final String DURATION = "duration";
        public static final String STATUS = "status";
    }

    public class Params {
        public static final String ID = "id";
        public static final String TOKEN = "token";
        public static final String SOCIAL_ID = "social_unique_id";

        public static final String AMOUNT = "amount";
        public static final String URL = "url";
        public static final String PICTURE = "picture";
        public static final String EMAIL = "email";
        public static final String PASSWORD = "password";
        public static final String REPASSWORD = "confirm_password";
        public static final String FULLNAME = "name";
        public static final String FIRSTNAME = "first_name";
        public static final String LAST_NAME = "last_name";
        public static final String MOBILE = "mobile";
        public static final String COUNTRY_CODE = "country_code";
        public static final String OTP = "otp";
        public static final String SSN = "ssn";
        public static final String DEVICE_TOKEN = "device_token";
        public static final String ICON = "icon";
        public static final String DEVICE_TYPE = "device_type";
        public static final String LOGIN_BY = "login_by";
        public static final String CURRENCY = "currency_code";
        public static final String LANGUAGE = "language";
        public static final String REQUEST_ID = "request_id";
        public static final String GENDER = "gender";
        public static final String COUNTRY = "country";
        public static final String TIMEZONE = "timezone";
        public static final String LATITUDE = "latitude";
        public static final String LONGITUDE = "longitude";
        public static final String AMBULANCE_TYPE = "service_id";
        public static final String SERVICE_TYPE = "service_type";
        public static final String ORIGINS = "origins";
        public static final String DESTINATION = "destinations";
        public static final String SENSOR = "sensor";
        public static final String MODE = "mode";
        public static final String DISTANCE = "distance";
        public static final String TIME = "time";
        public static final String S_LATITUDE = "s_latitude";
        public static final String S_LONGITUDE = "s_longitude";
        public static final String D_LATITUDE = "d_latitude";
        public static final String D_LONGITUDE = "d_longitude";
        public static final String S_ADDRESS = "s_address";
        public static final String D_ADDRESS = "d_address";
        public static final String PAYMENT_MODE = "payment_mode";
        public static final String IS_PAID = "is_paid";
        public static final String COMMENT = "comment";
        public static final String RATING = "rating";
        /*public static final String PAYMENT_METHOD_NONCE = "payment_method_nonce";*/
        public static final String PAYMENT_METHOD_NONCE = "paymentMethodNonce";
        public static final String LAST_FOUR = "last_four";
        public static final String CARD_ID = "card_id";
        public static final String NO_HOUR = "number_hours";
        public static final String REQ_STATUS_TYPE = "request_status_type";
        public static final String HOURLY_PACKAGE_ID = "hourly_package_id";
        public static final String AIRPORT_PACKAGE_ID = "airport_price_id";
        public static final String PROMOCODE = "promo_code";
        public static final String REMARK = "comment";
        public static final String REFERRAL_CODE = "referral_code";
        public static final String REFERRAL_BONUS = "referee_bonus";
        public static final String FAV_ID = "fav_id";
        public static final String ADDRESS = "address";
        public static final String FAVOURITE_NAME = "favourite_name";
        public static final String IS_ADSTOP = "is_adstop";
        public static final String ADSTOP_LONGITUDE = "adstop_longitude";
        public static final String ADSTOP_LATITUDE = "adstop_latitude";
        public static final String ADSTOP_ADDRESS = "adstop_address";
        public static final String CHANGE_TYPE = "change_type";
        public static final String OPERATOR_ID = "operator_id";
        public static final String SCHEDULE_REQUEST_TIME = "requested_time";
        public static final String SCHEDULE_PATIENT_NAME = "patient_name";
        public static final String SCHEDULE_PURPOSE = "purpose";
        public static final String SCHEDULE_TRIPS = "trips";
        public static final String SCHEDULE_SPECIAL_REQUEST = "special_request";
        public static final String SCHEDULE_ROOM_NUMBER = "room_no";
        public static final String SCHEDULE_FLOOR_NUMBER = "floor_no";
        public static final String SCHEDULE_BED_NUMBER = "bed_no";
        public static final String SCHEDULE_WARD_NUMBER = "ward_no";
        public static final String SCHEDULE_HOSPITAL = "hospital";
        public static final String SCHEDULE_ASSISTIVE_DEVICE = "assistive_device";
        public static final String SCHEDULE_OXYGEN_TANK = "oxygen_tank";
        public static final String CASE_TYPE = "case_type";
        public static final String ARR_REQUEST = "arr_request";
        public static final String TYPE = "type";
        public static final String CONTACT_NAME = "contact_name";
        public static final String CONTACT_NO = "contact_no";
        public static final String PREFERRED_USERNAME = "preferred_username";
        public static final String FAMILY_MEMBER_NAME = "family_member_name";
        public static final String HOME_NUMBER = "home_number";
        public static final String BLOCK_NUMBER = "block_no";
        public static final String UNIT_NUMBER = "unit_no";
        public static final String STREET_NAME = "street_name";
        public static final String POSTAL = "postal";
        public static final String WEIGHT = "weight";
        public static final String LIFT_LANDING = "lift_landing";
        public static final String STAIRS = "stairs";
        public static final String NO_STAIRS = "no_stairs";
        public static final String LOW_STAIRS = "low_stairs";
        public static final String PATIENT_CONDITION = "patient_condition";
        public static final String STRETCHER = "stretcher";
        public static final String WHEEL_CHAIR = "wheel_chair";
        public static final String OXYGEN = "oxygen_tank";
        public static final String ESCORTS = "escorts";
        public static final String ADD_INFORMATION = "add_information";
        public static final String DATA = "data";
        public static final String FLOOR_NUMBER = "floor";
        public static final String WARD = "ward";
        public static final String PICKUP_TYPE = "type_pickup";
        public static final String FERRY_TERMINALS = "ferry_terminals";
        public static final String IMH = "imh";
        public static final String STAIRCASE = "staircase";
        public static final String A_AND_E = "a_e";
        public static final String TARMAC = "tarmac";
        public static final String FAMILY_MEMBER = "family_member";
        public static final String HOUSE_UNIT = "house_unit";
        public static final String INVOICE = "invoice";
        public static final String BILLING_INFO = "billinfo";
        public static final String TOTAL = "total";
        public static final String REQUEST_OPTIONAL = "request_optional";
        public static final String LATER = "later";
        public static final String PLATE_NO = "plate_no";
        public static final String TIME_OF_DISCHARGE = "time_discharge";

        public static final String SERVICE_TYPE_CAR = "service_type";
        public static final String SERVICE_KILOMET = "km";

        public static final String CHOOSE_PAYMENT_SELECT_TYPE = "pre_select_type";

        public static final String USER_TYPE_WALLET ="usertype";
    }

    public class ServiceType {
        public static final String SOCKET_URL = "https://123-go.co:4000?";// dev socket server

        //  public static final String SOCKET_URL = "http://ambulance2u.qooservices.cf:3000?";// dev socket server
//        public static final String HOST_URL = "http://ambulance2u.qooservices.cf/";// PL developing server

//        public static final String SOCKET_URL = "https://halp.sg:3000?";// dev socket server
//        public static final String HOST_URL = "https://halp.sg/";// PL developing server


        public static final String HOST_URL = "https://123-go.co/";// PL developing server


        public static final String BASE_URL = HOST_URL + "userApi/";

        public static final String EWALLET = BASE_URL + "ewallet";

        public static final String LOGIN = BASE_URL + "login";
        public static final String REGISTER = BASE_URL + "register";
        public static final String UPDATE_PROFILE = BASE_URL + "updateProfile";
        public static final String GET_USER_PROFILE = BASE_URL + "userDetails?";
        public static final String FORGOT_PASSWORD = BASE_URL + "forgotpassword";
        public static final String OPERATORS_URL = BASE_URL + "serviceList";
        public static final String GET_PROVIDERS = BASE_URL + "guestProviderList";
        public static final String FARE_CALCULATION = BASE_URL + "fare_calculator";
        public static final String REQUEST_AMBULANCE = BASE_URL + "sendRequest";
        public static final String CANCEL_CREATE_REQUEST = BASE_URL + "waitingRequestCancel";
        public static final String CHECKREQUEST_STATUS = BASE_URL + "requestStatusCheck";
        public static final String PAYNOW = BASE_URL + "payment";
        public static final String RATE_PROVIDER = BASE_URL + "rateProvider";
        public static final String CANCEL_RIDE = BASE_URL + "cancelRequest";
        public static final String GET_HISTORY_MONTH = BASE_URL + "historybymonth";
        public static final String GET_HISTORY = BASE_URL + "history";

        public static final String GET_PAYMENT_MODES = BASE_URL + "getPaymentModes?";
        public static final String PAYMENT_MODE_UPDATE = BASE_URL + "PaymentModeUpdate";
        public static final String GET_BRAIN_TREE_TOKEN_URL = BASE_URL + "getbraintreetoken";
        public static final String CREATE_ADD_CARD_URL = BASE_URL + "addcard";
        public static final String GET_ADDED_CARDS_URL = BASE_URL + "getcards?";
        public static final String REMOVE_CARD = BASE_URL + "deletecard";
        public static final String CREATE_SELECT_CARD_URL = BASE_URL + "selectcard";
        public static final String REQUEST_LATER = BASE_URL + "laterRequest";
        public static final String GET_SCHEDULE = BASE_URL + "upcomingRequest";
        public static final String CANCEL_LATER_RIDE = BASE_URL + "cancel_later_request?";
        public static final String HOURLY_PACKAGE_FARE = BASE_URL + "hourly_package_fare";
        public static final String USER_MESSAGE_NOTIFY = BASE_URL + "message_notification?";
        public static final String AIRPORT_LST = BASE_URL + "airport_details?";
        public static final String LOCATION_LST = BASE_URL + "location_details?";
        public static final String AIRPORT_PACKAGE_FARE = BASE_URL + "airport_package_fare";
        public static final String GET_OTP = BASE_URL + "sendOtp?";
        public static final String CANCEL_REASON = BASE_URL + "cancellationReasons";
        public static final String VALIDATE_PROMO = BASE_URL + "validate_promo";
        public static final String ADVERTISEMENTS = BASE_URL + "adsManagement";
        public static final String POST_PAYPAL_NONCE_URL = BASE_URL + "paymentcheckout";
        public static final String PAYMENT_CASH = BASE_URL + "paymentcash";

        public static final String TOP_UP_WALLET = BASE_URL + "paymenttopupmoney";

        public static final String PAYMENT_WALLLET = BASE_URL + "paymentwallet";

        public static final String HISTORY_PAYMENT = BASE_URL + "historypayment";
        public static final String BILLING_INFO = BASE_URL + "billinfo";

        public static final String HISTORY_TOPUP_WALLET = BASE_URL + "historytopup";


        public static final String CHOOSE_PAYMENT_TYPE = BASE_URL + "selectpaymenttype";

        public static final String GET_TYPE_CHOOSE_PAYMENT = BASE_URL + "checkselectedType";

        public static final String BILLING_EXTRA_COST= BASE_URL + "getExtraCost";

        public static final String CHANGE_PASSWORD = BASE_URL+"changePassword";


        // wallet config
        public static final String WALLET_HOST_URL = "http://walletbay.net/apps";
        public static final String WALLET_BASE_URL = WALLET_HOST_URL + "/api/business/";
        public static final String WALLET_TYPES = WALLET_BASE_URL + "payment-gateways";
        public static final String WALLET_BALANCE = WALLET_HOST_URL + "/api/businesses/users/";

        public static final String GET_SAVED_PLACES = BASE_URL + "userFavourites";
        public static final String CANCEL_FAV = BASE_URL + "deleteuserFavourite";
        public static final String ADD_FAV = BASE_URL + "adduserFavourite";
        public static final String LOGOUT_URL = BASE_URL + "logout";
        public static final String GET_VERSION = HOST_URL + "get_version";
        public static final String MESSAGE_GET = BASE_URL + "message/get";
        public static final String UPDATE_ADDRESS = BASE_URL + "updateAddress";
        public static final String APPLY_REFERRAL = BASE_URL + "applyReferral";
    }

    public class NursingHomeService {

        public static final String NURSING_HOME = "nurse";
        public static final String NURSING_HOME_BASE_URL = ServiceType.HOST_URL + "NurApi/";
        public static final String LOGIN_URL = NURSING_HOME_BASE_URL + "login";
        public static final String OPERATORS_URL = NURSING_HOME_BASE_URL + "OperatorList";
        public static final String REGISTER_SCHEDULE_URL = NURSING_HOME_BASE_URL + "laterRequest";
        public static final String SEND_REQUEST_URL = NURSING_HOME_BASE_URL + "sendRequest";
        public static final String REQUEST_STATUS_CHECK_URL = NURSING_HOME_BASE_URL + "requestStatusCheck";
        public static final String LIST_SCHEDULE_REQUEST_URL = NURSING_HOME_BASE_URL + "listRequests";
        public static final String UPDATE_PROFILE_URL = NURSING_HOME_BASE_URL + "updateProfile";
        public static final String CANCEL_SCHEDULE_URL = NURSING_HOME_BASE_URL + "deleteSchedule";
        public static final String CANCEL_TRIP_URL = NURSING_HOME_BASE_URL + "cancelRequest";
        public static final String CANCEL_REASONS_URL = NURSING_HOME_BASE_URL + "cancellationReasons";
        public static final String GUEST_PROVIDER_URL = NURSING_HOME_BASE_URL + "guestProviderList";
        public static final String FARE_CACULATION_URL = NURSING_HOME_BASE_URL + "fare_calculator";
        public static final String RATE_PROVIDER_URL = NURSING_HOME_BASE_URL + "rateProvider";
        public static final String WAITING_CANCEL_REQUEST_URL = NURSING_HOME_BASE_URL + "waitingRequestCancel";
        public static final String HISTORY_RIDE_URL = NURSING_HOME_BASE_URL + "history";
        public static final String CHAT_NOTIFICATION_URL = NURSING_HOME_BASE_URL + "message_notification?";
        public static final String GET_CHAT_URL = NURSING_HOME_BASE_URL + "message/get";
        public static final String LOGOUT_URL = NURSING_HOME_BASE_URL + "logout";
        public static final String GET_ACCOUNT_INFORMATION_URL = NURSING_HOME_BASE_URL + "getInformationProfile?";
        public static final String GET_BRAINTREE_TOKEN_URL = NURSING_HOME_BASE_URL + "getbraintreetoken";
        public static final String POST_PAYPAL_NONCE_URL = NURSING_HOME_BASE_URL + "paymentcheckout";
        public static final String PAYMENT_BY_CASH_URL = NURSING_HOME_BASE_URL + "paymentcash";
        public static final String HISTORY_PAYMENT = NURSING_HOME_BASE_URL + "historypayment";
        public static final String BILLING_INFO = NURSING_HOME_BASE_URL + "billinfo";

    }

    public class HospitalService {

        public static final String HOSPITAL = "hospital";
        public static final String HOSPITAL_BASE_URL = ServiceType.HOST_URL + "HospitalApi/";
        public static final String LOGIN_URL = HOSPITAL_BASE_URL + "login";
        public static final String LOGOUT_URL = HOSPITAL_BASE_URL + "logout";
        public static final String REGISTER_SCHEDULE_URL = HOSPITAL_BASE_URL + "laterRequest";
        public static final String CANCEL_SCHEDULE_URL = HOSPITAL_BASE_URL + "deleteSchedule";
        public static final String LIST_SCHEDULE_REQUEST_URL = HOSPITAL_BASE_URL + "listRequests";
        public static final String OPERATORS_URL = HOSPITAL_BASE_URL + "OperatorList";
        public static final String HISTORY_RIDE_URL = HOSPITAL_BASE_URL + "history";
        public static final String RATE_PROVIDER_URL = HOSPITAL_BASE_URL + "rateProvider";
        public static final String CANCEL_REASONS_URL = HOSPITAL_BASE_URL + "cancellationReasons";
        public static final String GET_ACCOUNT_INFORMATION_URL = HOSPITAL_BASE_URL + "getInformationProfile?";
        public static final String SEND_REQUEST_URL = HOSPITAL_BASE_URL + "sendRequest";
        public static final String CANCEL_REQUEST_URL = HOSPITAL_BASE_URL + "cancelRequest";
        public static final String WAITING_CANCEL_REQUEST_URL = HOSPITAL_BASE_URL + "waitingRequestCancel";
        public static final String CHECK_REQUEST_STATUS_URL = HOSPITAL_BASE_URL + "requestStatusCheck";
        public static final String GUEST_PROVIDER_LIST_URL = HOSPITAL_BASE_URL + "guestProviderList";
        public static final String FARE_CACULATION_URL = HOSPITAL_BASE_URL + "fare_calculator";
        public static final String CHAT_NOTIFICATION_URL = HOSPITAL_BASE_URL + "message_notification?";

        public static final String UPDATE_PROFILE_URL = HOSPITAL_BASE_URL + "updateProfile";
        public static final String GET_CHAT_URL = HOSPITAL_BASE_URL + "message/get";

        public static final String GET_BRAINTREE_TOKEN_URL = HOSPITAL_BASE_URL + "getbraintreetoken";
        public static final String POST_PAYPAL_NONCE_URL = HOSPITAL_BASE_URL + "paymentcheckout";
        public static final String PAYMENT_BY_CASH_URL = HOSPITAL_BASE_URL + "paymentcash";
        public static final String HISTORY_PAYMENT = HOSPITAL_BASE_URL + "historypayment";
        public static final String BILLING_INFO = HOSPITAL_BASE_URL + "billinfo";
    }

    public class PatientService {

        public static final String PATIENT = "user";

    }

    public class ScheduleStatus {

        public static final String PENDING = "pending";
        public static final String SENT = "sent";
        public static final String APPROVED = "approved";
        public static final String FINISHED = "finished";
        public static final String STARTING = "starting";
        public static final String WAITING_START = "waiting_start";

    }

    public class NotificationType {

        public static final String SCHEDULE = "schedule";
        public static final String TYPE_SCHEDULE_STARTED = "start_schedule";
        public static final String CHAT_MESSAGE = "chat_message";
        public static final String ACTION = "action";
        public static final String TYPE = "type";
        public static final String BODY = "body";
        public static final String TITLE = "title";
        public static final String ACTION_SCHEDULE = "request_later";
        public static final String ACTION_REQUEST_NORMAL = "request";
        public static final String TYPE_DRIVER_START = "driver_start";
        public static final String TYPE_DRIVER_ARRIVED = "arrived";
        public static final String TYPE_SERVICE_START = "service_start";
        public static final String TYPE_TRIP_COMPLETED = "completed";
        public static final String TYPE_SERVICE_ACCEPT = "accept";
        public static final String TYPE_SERVICE_REJECT = "reject";
        public static final String TYPE_SCHEDULE_APPROVED = "approve_scheduled";
        public static final String TYPE_TRIP_RATE = "rate";
        public static final String TYPE_TRIP_CANCLE = "cancel";
        public static final String TYPE_ACCOUNT_LOGOUT = "logout";
    }

    // service codes
    public class ServiceCode {
        public static final int REGISTER = 1;
        public static final int LOGIN = 2;
        public static final int UPDATE_PROFILE = 3;
        public static final int FORGOT_PASSWORD = 4;
        public static final int GOOGLE_DIRECTION_API = 5;
        public static final int AMBULANCE_OPERATOR = 6;
        public static final int GET_PROVIDERS = 7;
        public static final int GOOGLE_MATRIX = 8;
        public static final int FARE_CALCULATION = 9;
        public static final int REQUEST_AMBULANCE = 10;
        public static final int CANCEL_CREATE_REQUEST = 11;
        public static final int CHECKREQUEST_STATUS = 12;
        public static final int PAYNOW = 13;
        public static final int RATE_PROVIDER = 14;
        public static final int GOOGLE_MATRIX_ETA = 15;
        public static final int HOMEAMBULANCE_TYPE = 16;
        public static final int CANCEL_RIDE = 17;
        public static final int GET_HISTORY = 18;
        public static final int GET_PAYMENT_MODES = 19;
        public static final int PAYMENT_MODE_UPDATE = 20;
        public static final int GET_BRAIN_TREE_TOKEN_URL = 21;
        public static final int CREATE_ADD_CARD_URL = 22;
        public static final int GET_ADDED_CARDS_URL = 23;
        public static final int CREATE_SELECT_CARD_URL = 24;
        public static final int REMOVE_CARD = 25;
        public static final int REQUEST_LATER = 26;
        public static final int GET_SCHEDULE = 27;
        public static final int CANCEL_SCHEDULE_RIDE = 28;
        public static final int HOURLY_PACKAGE_FARE = 29;
        public static final int USER_MESSAGE_NOTIFY = 30;
        public static final int AIRPORT_LST = 31;
        public static final int LOCATION_LST = 32;
        public static final int AIRPORT_PACKAGE_FARE = 33;
        public static final int GOOGLE_DIRECTION_forcar_API = 34;
        public static final int LOCATION_API_BASE_SOURCE = 35;
        public static final int LOCATION_API_BASE_DESTINATION = 36;
        public static final int ADDRESS_API_BASE = 37;
        public static final int GET_OTP = 38;
        public static final int GOOGLE_ADDRESS_API = 39;
        public static final int WALLET_TYPES = 40;
        public static final int WALLET_BALANCE = 41;
        public static final int WALLET_CREDIT = 42;
        public static final int WALLET_PAYGATE = 43;
        public static final int CANCEL_REASON = 44;
        public static final int VALIDATE_PROMO = 45;
        public static final int GET_SAVED_PLACES = 46;
        public static final int CANCEL_FAV = 47;
        public static final int ADD_FAV = 48;
        public static final int ADVERTISEMENTS = 49;
        public static final int LOGOUT = 50;
        public static final int GET_VERSION = 51;
        public static final int MESSAGE_GET = 52;
        public static final int GEO_DEST = 53;
        public static final int UPDATE_ADDRESS = 54;
        public static final int APPLY_REFERRAL = 55;
        public static final int NURSE_LOGIN = 56;
        public static final int NURSE_HOSPITAL_REGISTER_SCHEDULE = 57;
        public static final int GET_ACCOUNT_INFORMATION = 58;
        public static final int HOSPITAL_LOGIN = 59;
        public static final int BILLING_INFO = 60;
        public static final int REQUEST_PAYPAL = 133;
        public static final int POST_PAYPAL_NONCE = 132;
        public static final int PAYMENT_CASH = 131;
        public static final int HISTORY_PAYMENT = 130;
        public static final int PAYMENT_WALLET = 135;
        public static final int GET_EXTRA_COST = 134;
        public static final int POLYLINE_DRIVER_TO_USER = 211;



        public static final int GET_BRAIN_TREE_TOKEN_URL_WALLET = 1997;
        public static final int REQUEST_PAYPAL_WALLET = 7991;
        public static final int SHOW_RECHARGE_WALLET = 1998;

        public static final int GET_TYPE_CHOOSE_PAYMENT = 61;

        public static final int CHOOSE_A_PAYMENT = 62;

        public static final int CHANGE_PASSWORD = 63;

    }

    public class PermissionRequestCode {

        public static final int ACCESS_LOCATION = 1;
        public static final int INTERNET = 2;
        public static final int ACCESS_NETWORK_STATE = 3;
        public static final int WRITE_EXTERNAL_STORAGE = 4;
        public static final int ACCESS_WIFI_STATE = 5;
        public static final int CALL_PHONE = 6;
        public static final int VIBRATE = 7;
        public static final int GET_ACCOUNTS = 8;
        public static final int CAMERA = 9;
        public static final int RECEIVE_BOOT_COMPLETED = 9;
        public static final int USE_CREDENTIALS = 10;
        public static final int WAKE_LOCK = 11;
        public static final int READ_PHONE_STATE = 12;
        public static final int READ_CONTACTS = 13;

    }

    public class GoogleMapAPI {

        public static final String NEARBY_PLACE_URL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?";
        public static final String LOCATION = "location=";
        public static final String RADIUS = "&radius=";
        public static final int PROXIMITY_RADIUS = 5000;
        public static final String TYPE = "&type=";
        public static final String SENSOR = "&sensor=";

        public static final String SERVER_KEY = "&key=";

        public static final String DIRECTION_URL = "https://maps.googleapis.com/maps/api/directions/json?";
        public static final String ORIGIN = "origin=";
        public static final String DESTINATION = "&destination=";


    }


    // Placesurls
    public static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    public static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    public static final String TYPE_NEAR_BY = "/nearbysearch";
    public static final String OUT_JSON = "/json";

    // Location API
    public static final String LOCATION_API_BASE = "https://maps.googleapis.com/maps/api/geocode/json?address=";

    // Address API
    public static final String ADDRESS_API_BASE = "https://maps.googleapis.com/maps/api/geocode/json?latlng=";
    public static final String GEO_DEST = "https://maps.googleapis.com/maps/api/geocode/json?latlng=";
    // direction API
    public static final String DIRECTION_API_BASE = "https://maps.googleapis.com/maps/api/directions/json?";
    public static final String ORIGIN = "origin";
    public static final String DESTINATION = "destination";
    public static final String WAYPOINTS = "waypoints";
    public static final String EXTANCTION = "sensor=false&units=metric&mode=driving&alternatives=true&key=" + Const.GOOGLE_API_KEY;
}
