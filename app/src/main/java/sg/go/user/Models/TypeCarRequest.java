package sg.go.user.Models;

public class TypeCarRequest {
    String Name_Type_Car_Request;
    String Imga_Type_Car_Request;
    int Imga_Type_Car_Request_Int;

    public int getImga_Type_Car_Request_Int() {
        return Imga_Type_Car_Request_Int;
    }

    public void setImga_Type_Car_Request_Int(int imga_Type_Car_Request_Int) {
        Imga_Type_Car_Request_Int = imga_Type_Car_Request_Int;
    }

    public TypeCarRequest(String name_Type_Car_Request, String imga_Type_Car_Request) {
        Name_Type_Car_Request = name_Type_Car_Request;
        Imga_Type_Car_Request = imga_Type_Car_Request;
    }
    public TypeCarRequest(String name_Type_Car_Request, int imga_Type_Car_Request_int) {
        Name_Type_Car_Request = name_Type_Car_Request;
        Imga_Type_Car_Request_Int = imga_Type_Car_Request_int;
    }

    public String getName_Type_Car_Request() {
        return Name_Type_Car_Request;
    }

    public void setName_Type_Car_Request(String name_Type_Car_Request) {
        Name_Type_Car_Request = name_Type_Car_Request;
    }

    public String getImga_Type_Car_Request() {
        return Imga_Type_Car_Request;
    }

    public void setImga_Type_Car_Request(String imga_Type_Car_Request) {
        Imga_Type_Car_Request = imga_Type_Car_Request;
    }
}