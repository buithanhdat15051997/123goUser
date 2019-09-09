package sg.go.user.Adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import sg.go.user.Models.CancelReason;
import sg.go.user.R;

import java.util.List;

/**
 * Created by Mahesh on 11/23/2017.
 */

public class CancelReasonAdapter extends RecyclerView.Adapter<CancelReasonAdapter.typesViewHolder> {

    private Activity mContext;
    private List<CancelReason> itemshistroyList;


    public CancelReasonAdapter(Activity context, List<CancelReason> itemshistroyList) {
        this.mContext = context;
        this.itemshistroyList = itemshistroyList;

    }

    @Override
    public CancelReasonAdapter.typesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.reason_item, null);
        CancelReasonAdapter.typesViewHolder holder = new CancelReasonAdapter.typesViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(final CancelReasonAdapter.typesViewHolder holder, int position) {
        CancelReason reason_itme = itemshistroyList.get(position);


        holder.tv_cancel_reason.setText(reason_itme.getReasontext());


    }

    @Override
    public int getItemCount() {
        return itemshistroyList.size();
    }

    public class typesViewHolder extends RecyclerView.ViewHolder {

        private TextView tv_cancel_reason;

        public typesViewHolder(View itemView) {
            super(itemView);

            tv_cancel_reason = (TextView) itemView.findViewById(R.id.tv_cancel_reason);

        }
    }


}


