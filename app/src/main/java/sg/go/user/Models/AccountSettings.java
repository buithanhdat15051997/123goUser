package sg.go.user.Models;

/**
 * Created by getit on 8/11/2016.
 */
public class AccountSettings
{
    private int accountSettingsIcon;
    private String accountSettingsText;

    public AccountSettings(int accountSettingsIcon, String accountSettingsText) {
        this.accountSettingsIcon = accountSettingsIcon;
        this.accountSettingsText = accountSettingsText;
    }

    public String getAccountSettingsText() {
        return accountSettingsText;
    }

    public void setAccountSettingsText(String accountSettingsText) {
        this.accountSettingsText = accountSettingsText;
    }

    public int getAccountSettingsIcon() {
        return accountSettingsIcon;
    }

    public void setAccountSettingsIcon(int accountSettingsIcon) {
        this.accountSettingsIcon = accountSettingsIcon;
    }




}
