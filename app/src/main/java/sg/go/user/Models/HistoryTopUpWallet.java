package sg.go.user.Models;

public class HistoryTopUpWallet {
   private float money_TopUp,moneyOldTopup ;

   private String payment_Type_TopUp;
   private String dateTime_TopUp;

    public float getMoneyOldTopup() {
        return moneyOldTopup;
    }

    public void setMoneyOldTopup(float moneyOldTopup) {
        this.moneyOldTopup = moneyOldTopup;
    }

    public float getMoney_TopUp() {
        return money_TopUp;
    }

    public void setMoney_TopUp(float money_TopUp) {
        this.money_TopUp = money_TopUp;
    }

    public String getPayment_Type_TopUp() {
        return payment_Type_TopUp;
    }

    public void setPayment_Type_TopUp(String payment_Type_TopUp) {
        this.payment_Type_TopUp = payment_Type_TopUp;
    }

    public String getDateTime_TopUp() {
        return dateTime_TopUp;
    }

    public void setDateTime_TopUp(String dateTime_TopUp) {
        this.dateTime_TopUp = dateTime_TopUp;
    }

    public HistoryTopUpWallet(float money_TopUp, float moneyOldTopup ,String payment_Type_TopUp, String dateTime_TopUp) {
        this.money_TopUp = money_TopUp;
        this.moneyOldTopup = moneyOldTopup;
        this.payment_Type_TopUp = payment_Type_TopUp;
        this.dateTime_TopUp = dateTime_TopUp;
    }
}
