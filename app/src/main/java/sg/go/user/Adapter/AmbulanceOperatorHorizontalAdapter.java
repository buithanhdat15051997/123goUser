package sg.go.user.Adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import sg.go.user.Interface.AdapterCallback;
import sg.go.user.Models.AmbulanceOperator;
import sg.go.user.R;
import sg.go.user.Utils.Const;
import sg.go.user.Utils.PreferenceHelper;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by user on 10/5/2016.
 */
public class AmbulanceOperatorHorizontalAdapter extends RecyclerView.Adapter<AmbulanceOperatorHorizontalAdapter.typesViewHolder>{

    private Activity mContext;
    private AdapterCallback mAdapterCallback;
    private List<AmbulanceOperator> ambulanceOperatorList;
    public int pos;
    DecimalFormat format = new DecimalFormat("0.00");

    public AmbulanceOperatorHorizontalAdapter(Activity context, List<AmbulanceOperator> ambulanceOperatorList, AdapterCallback mAdapterCallback) {
        mContext = context;
        this.ambulanceOperatorList = ambulanceOperatorList;
        this.mAdapterCallback = mAdapterCallback;

    }

    @Override
    public AmbulanceOperatorHorizontalAdapter.typesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ambulance_operator_horizontal_item,parent,false);
        typesViewHolder holder = new typesViewHolder(view);

        return holder;
    }



    @Override
    public void onBindViewHolder(final AmbulanceOperatorHorizontalAdapter.typesViewHolder holder, int position) {
        final AmbulanceOperator list_type = ambulanceOperatorList.get(position);

        holder.tv_type_name.setText(list_type.getAmbulanceOperator());
        holder.tv_type_name.setSelected(true);

        if (null!=list_type.getAmbulanceCost()&&!list_type.getAmbulanceCost().equals("")) {

            holder.tv_type_cost.setText(list_type.getCurrencey_unit() + format.format(Double.valueOf(list_type.getAmbulanceCost())));

        } else {

            holder.tv_type_cost.setText(list_type.getCurrencey_unit() + list_type.getAmbulanceCost());

        }

        /*Picasso.get().load(list_types.getAmbulanceImage()).error(R.drawable.frontal_ambulance_cab).into(holder.type_picutre);*/

        Glide.with(mContext).load(list_type.getAmbulanceImage())
                .apply(new RequestOptions()
                        .error(R.drawable.ambulance_car))
                .into(holder.type_picutre);

        if (new PreferenceHelper(mContext).getLoginType().equals(Const.PatientService.PATIENT)){

            holder.btn_fare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mAdapterCallback.onMethodCallback(
                            list_type.getId(),
                            list_type.getAmbulanceOperator(),
                            list_type.getAmbulance_price_distance(),
                            list_type.getAmbulance_price_min(),
                            list_type.getAmbulanceImage(),
                            list_type.getAmbulanceSeats(),
                            list_type.getBasefare());
                }
            });

            holder.tv_type_cost.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mAdapterCallback.onMethodCallback(
                            list_type.getId(),
                            list_type.getAmbulanceOperator(),
                            list_type.getAmbulance_price_distance(),
                            list_type.getAmbulance_price_min(),
                            list_type.getAmbulanceImage(),
                            list_type.getAmbulanceSeats(),
                            list_type.getBasefare());
                }
            });

        }/*else if (new PreferenceHelper(mContext).getLoginType().equals(Const.NursingHomeService.NURSING_HOME)){

        }*/

     /*
        holder.type_picutre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.type_picutre.setBorderColor(mContext.getResources().getColor(R.color.ripple_blue));
            }
        });*/

        if (pos == position) {

            holder.view_select.setVisibility(View.VISIBLE);

        } else {

            holder.view_select.setVisibility(View.INVISIBLE);

        }

    }

    @Override
    public int getItemCount() {
        return ambulanceOperatorList.size();
    }

    public class typesViewHolder extends RecyclerView.ViewHolder {
        private ImageView type_picutre,btn_fare;
        private TextView tv_type_name, tv_type_cost;
        private View view_select;

        public typesViewHolder(View itemView) {
            super(itemView);
            type_picutre = (ImageView) itemView.findViewById(R.id.type_picutre);
            btn_fare = (ImageView)itemView.findViewById(R.id.btn_fare);
            tv_type_name = (TextView) itemView.findViewById(R.id.tv_type_name);
            tv_type_cost = (TextView) itemView.findViewById(R.id.tv_type_cost);
            view_select = (View)itemView.findViewById(R.id.view_select);

        }
    }

    public void OnItemClicked(int position) {
        pos = position;
        Log.d("HaoLS", "AmbulanceOperatorHorizontalAdapter onItemClicked " + pos);
    }

}
