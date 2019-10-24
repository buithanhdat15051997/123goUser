package sg.go.user.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import sg.go.user.Models.HistoryTopUpWallet;
import sg.go.user.R;

public class HistoryTopUpWalletAdapter extends RecyclerView.Adapter<HistoryTopUpWalletAdapter.viewHolder> {


    private Context mcontext;
    private ArrayList<HistoryTopUpWallet> historyTopUpWallets;
    private SimpleDateFormat simpleDateFormat;
    private SimpleDateFormat inputformat;

    public HistoryTopUpWalletAdapter(Context mcontext, ArrayList<HistoryTopUpWallet> historyTopUpWallets) {

        simpleDateFormat = new SimpleDateFormat("E, MMM, dd, yyyy hh:mm");
        inputformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        this.mcontext = mcontext;
        this.historyTopUpWallets = historyTopUpWallets;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(mcontext).inflate(R.layout.item_history_topup_wallet, null);

        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder viewHolder, int i) {

        viewHolder.txt_paymentType_TopUp.setText(mcontext.getResources().getString(R.string.txt_type_top_up_wallet)+": "+historyTopUpWallets.get(i).getPayment_Type_TopUp());

        float money = historyTopUpWallets.get(i).getMoney_TopUp();

        float moneyOld = historyTopUpWallets.get(i).getMoneyOldTopup();

        viewHolder.txt_Money_TopUp.setText("+ "+mcontext.getResources().getString(R.string.dolo_singapor)+" "+money);

        viewHolder.txt_MoneyOld_TopUp.setText("  "+mcontext.getResources().getString(R.string.dolo_singapor)+" "+moneyOld);

        String hitory_Date ="";
        try {
            hitory_Date = historyTopUpWallets.get(i).getDateTime_TopUp();
            Date date = inputformat.parse(hitory_Date);
            hitory_Date = simpleDateFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        viewHolder.txt_DateTime_TopUp.setText(hitory_Date);

    }

    @Override
    public int getItemCount() {
        return historyTopUpWallets.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {
       TextView txt_paymentType_TopUp, txt_Money_TopUp,txt_MoneyOld_TopUp, txt_DateTime_TopUp;

        public viewHolder(@NonNull View itemView) {
            super(itemView);

            txt_paymentType_TopUp = itemView.findViewById(R.id.txt_paymentType_TopUp);
            txt_MoneyOld_TopUp = itemView.findViewById(R.id.txt_MoneyOld_TopUp);

            txt_Money_TopUp = itemView.findViewById(R.id.txt_Money_TopUp);

            txt_DateTime_TopUp = itemView.findViewById(R.id.txt_DateTime_TopUp);

        }
    }
}
