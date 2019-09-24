package sg.go.user.Models;

/**
 * Created by Mahesh on 8/30/2017.
 */

public class Wallets {

    private String Total_Recharge_Wallet, Recharge_Wallet;

    public String getTotal_Recharge_Wallet() {
        return Total_Recharge_Wallet;
    }

    public void setTotal_Recharge_Wallet(String total_Recharge_Wallet) {
        Total_Recharge_Wallet = total_Recharge_Wallet;
    }

    public String getRecharge_Wallet() {
        return Recharge_Wallet;
    }

    public void setRecharge_Wallet(String recharge_Wallet) {
        Recharge_Wallet = recharge_Wallet;
    }



    private String gateway_id,gateway_name,gateway_key,gateway_mode;

    public String getGateway_id() {
        return gateway_id;
    }

    public void setGateway_id(String gateway_id) {
        this.gateway_id = gateway_id;
    }

    public String getGateway_name() {
        return gateway_name;
    }

    public void setGateway_name(String gateway_name) {
        this.gateway_name = gateway_name;
    }

    public String getGateway_key() {
        return gateway_key;
    }

    public void setGateway_key(String gateway_key) {
        this.gateway_key = gateway_key;
    }

    public String getGateway_mode() {
        return gateway_mode;
    }

    public void setGateway_mode(String gateway_mode) {
        this.gateway_mode = gateway_mode;
    }
}
