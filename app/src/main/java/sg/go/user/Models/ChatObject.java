package sg.go.user.Models;

/**
 * Created by Dell on 3/19/2015.
 */
public class ChatObject {
    String message;
    String type;
    String request_id;
    String driver_id;
    String client_id;
    private String data_type;


    public static String TEXT = "0", IMAGE = "1", VIDEO = "3", ATTACHED_IMAGE = "4", ATTACHED_VIDEO = "5", LOCATION = "6", CONTACT = "7", VOICE_CLIP = "8", AUDIO = "9";

    public String getType() {
        return type;
    }





    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }



    public String getRequest_id() {
        return request_id;
    }



    public void setRequest_id(String request_id) {
        this.request_id = request_id;
    }



    public String getDriver_id() {
        return driver_id;
    }



    public void setDriver_id(String driver_id) {
        this.driver_id = driver_id;
    }



    public String getClient_id() {
        return client_id;
    }



    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }



    public void setType(String type) {
        this.type = type;
    }



    public ChatObject(String message, String type,String data_type, String request_id,
                      String client_id,String driver_id) {
        super();
        this.message = message;
        this.type = type;
        this.data_type = data_type;
        this.request_id = request_id;
        this.driver_id = driver_id;
        this.client_id = client_id;
    }


    public String getData_type() {
        return data_type;
    }

    public void setData_type(String data_type) {
        this.data_type = data_type;
    }
}