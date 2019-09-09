package sg.go.user.Adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import sg.go.user.Models.Payments;
import sg.go.user.R;

import java.util.List;

/**
 * Created by user on 1/21/2017.
 */

public class PaymentModeAdapter extends RecyclerView.Adapter<PaymentModeAdapter.typesViewHolder> {

    private Activity mContext;
    private List<Payments> itemspaymentList;


    public PaymentModeAdapter(Activity context, List<Payments> itemspaymentList) {
        mContext = context;


        this.itemspaymentList = itemspaymentList;

    }

    @Override
    public PaymentModeAdapter.typesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.select_payment_item, null);
        PaymentModeAdapter.typesViewHolder holder = new PaymentModeAdapter.typesViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(final PaymentModeAdapter.typesViewHolder holder, int position) {
        Payments payment_itme = itemspaymentList.get(position);

        if (payment_itme != null) {

            // holder.payment_title.setText(payment_itme.getPayment_name());
            if (payment_itme.getPayment_name().equals("cod")) {
                holder.payment_img.setBackgroundColor(mContext.getResources().getColor(R.color.circle_color));
                holder.payment_img.setText(mContext.getResources().getString(R.string.txt_pay_cash));
            } else if (payment_itme.getPayment_name().equals("paypal")) {
                holder.payment_img.setBackgroundColor(mContext.getResources().getColor(R.color.paypal_color));
                holder.payment_img.setText(mContext.getResources().getString(R.string.txt_pay_paypal));
            } else if (payment_itme.getPayment_name().equals("walletbay")) {
                holder.payment_img.setBackgroundColor(mContext.getResources().getColor(R.color.wallet_color));
                holder.payment_img.setText(mContext.getResources().getString(R.string.txt_pay_wallet));
            } else {
                holder.payment_img.setBackgroundColor(mContext.getResources().getColor(R.color.background));
                holder.payment_img.setText(mContext.getResources().getString(R.string.txt_pay_card));
            }

        }

    }

    @Override
    public int getItemCount() {
        return itemspaymentList.size();
    }

    public class typesViewHolder extends RecyclerView.ViewHolder {
        private Button payment_img;

        public typesViewHolder(View itemView) {
            super(itemView);
            payment_img = (Button) itemView.findViewById(R.id.payment_btn);

        }
    }


}


