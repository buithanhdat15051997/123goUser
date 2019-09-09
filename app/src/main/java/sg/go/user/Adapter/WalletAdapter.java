package sg.go.user.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import sg.go.user.Models.Wallets;
import sg.go.user.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mahesh on 8/30/2017.
 */

public class WalletAdapter extends BaseAdapter {
    private ArrayList<Wallets> dataSet;
    private Context mContext;
    private Holder holder;
    private static LayoutInflater inflater = null;
    private List<Wallets> arraylist;

    public WalletAdapter(Context context, ArrayList<Wallets> dataSet) {
        mContext = context;
        this.dataSet = dataSet;
        this.arraylist = new ArrayList<Wallets>();
        inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }


    @Override
    public int getCount() {
        return dataSet.size();
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    public class Holder {

        TextView tv_payment_title;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        holder = new WalletAdapter.Holder();

        if (view == null)

            view = inflater.inflate(R.layout.gird_wallet_item,
                    viewGroup, false);
        holder.tv_payment_title = (TextView) view.findViewById(R.id.tv_payment_title);


        if (dataSet != null) {

            holder.tv_payment_title.setText(dataSet.get(i).getGateway_name());
           // holder.tv_payment_title.setBackgroundColor(mContext.getResources().getColor(R.color.paypal_color));

        }

        return view;
    }

}