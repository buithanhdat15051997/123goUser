package sg.go.user.Adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import sg.go.user.Models.HistoryPayment;
import sg.go.user.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HistoryPaymentAdapter extends RecyclerView.Adapter<HistoryPaymentAdapter.HistoryPaymentViewHolder> {

    private Activity mActivity;
    private List<HistoryPayment> mHistoryPayments;
    private View mView;

    public HistoryPaymentAdapter(Activity mActivity, List<HistoryPayment> mHistoryPayments) {

        this.mActivity = mActivity;
        this.mHistoryPayments = mHistoryPayments;

    }

    @NonNull
    @Override
    public HistoryPaymentViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        mView = LayoutInflater.from(mActivity).inflate(R.layout.history_payment_item, viewGroup, false);

        return new HistoryPaymentViewHolder(mView);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryPaymentViewHolder historyPaymentViewHolder, int i) {

        HistoryPayment historyPayment = mHistoryPayments.get(i);

        if (historyPayment != null){

            historyPaymentViewHolder.mTv_history_payment_date.setText(historyPayment.getmDateCreate());
            historyPaymentViewHolder.mTv_history_payment_ambulance_operator.setText(historyPayment.getmAmbulanceOperator());
            historyPaymentViewHolder.mTv_history_payment_source.setText(historyPayment.getmPickupAddress());
            historyPaymentViewHolder.mTv_history_payment_destination.setText(historyPayment.getmDestinationAddress());
            historyPaymentViewHolder.mTv_history_payment_total.setText("$ " + historyPayment.getmTotal());
        }

    }

    @Override
    public int getItemCount() {
        return mHistoryPayments.size();
    }

    public class HistoryPaymentViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.tv_history_payment_date) TextView mTv_history_payment_date;

        @BindView(R.id.tv_history_payment_ambulance_operator) TextView mTv_history_payment_ambulance_operator;

        @BindView(R.id.tv_history_payment_source) TextView mTv_history_payment_source;

        @BindView(R.id.tv_history_payment_destination) TextView mTv_history_payment_destination;

        @BindView(R.id.tv_history_payment_total) TextView mTv_history_payment_total;

        public HistoryPaymentViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
