package sg.go.user.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import sg.go.user.Models.AccountSettings;
import sg.go.user.R;
import sg.go.user.Utils.EbizworldUtils;


/**
 * Created by getit on 8/11/2016.
 */
public class AccountSettingsAdapter extends BaseAdapter {

    private Context mContext;
    private List<AccountSettings> accountSettingsList;

    public AccountSettingsAdapter(Context context, List<AccountSettings> accountSettingsList) {
        mContext=context;
        this.accountSettingsList = accountSettingsList;
    }

    @Override
    public int getCount() {
        return accountSettingsList.size();
    }

    @Override
    public Object getItem(int position) {
        return accountSettingsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView==null)
       {
           convertView= LayoutInflater.from(mContext).inflate(R.layout.adpter_user_settings,null);
           holder=new ViewHolder();
           holder.accountSettingsIcon = (ImageView) convertView.findViewById(R.id.iv_user_settings_icon);
           holder.accountSettingsTitle = (TextView) convertView.findViewById(R.id.tv_user_settings_title);
           convertView.setTag(holder);
       }
        holder= (ViewHolder) convertView.getTag();
        EbizworldUtils.appLogDebug("AccountSettingsAdapter","Position" +position);
        holder.accountSettingsTitle.setText(accountSettingsList.get(position).getAccountSettingsText());
        holder.accountSettingsIcon.setImageResource(accountSettingsList.get(position).getAccountSettingsIcon());

        return convertView;
    }

    class ViewHolder
    {
        ImageView accountSettingsIcon;
        TextView accountSettingsTitle;
    }
}
