package sg.go.user.Models;

/**
 * Created by user on 1/21/2017.
 */

public class CreditCards {
    private String cardNumber;
    private String cardTypeUrl;
    private String isDefault;
    private String type;
    private String cardtype;
    private String cardId;
    private String email;

    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getCardTypeUrl() {
        return cardTypeUrl;
    }

    public void setCardTypeUrl(String cardTypeUrl) {
        this.cardTypeUrl = cardTypeUrl;
    }

    public String getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(String isDefault) {
        this.isDefault = isDefault;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCardtype() {
        return cardtype;
    }

    public void setCardtype(String cardtype) {
        this.cardtype = cardtype;
    }
}