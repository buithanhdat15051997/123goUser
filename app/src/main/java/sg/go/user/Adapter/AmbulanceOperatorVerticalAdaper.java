package sg.go.user.Adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
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

import java.text.DecimalFormat;
import java.util.List;

public class AmbulanceOperatorVerticalAdaper extends RecyclerView.Adapter<AmbulanceOperatorVerticalAdaper.VerticalViewHolder> {

    private Activity mContext;
    private AdapterCallback mAdapterCallback;
    private List<AmbulanceOperator> ambulanceOperatorList;
    public int pos;
    DecimalFormat format = new DecimalFormat("0.00");

    public AmbulanceOperatorVerticalAdaper(Activity mContext, List<AmbulanceOperator> ambulanceOperatorList) {
        this.mContext = mContext;
        this.ambulanceOperatorList = ambulanceOperatorList;
    }

    @NonNull
    @Override
    public VerticalViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.ambulance_operator_vertical_item, viewGroup, false);

        return new VerticalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VerticalViewHolder verticalViewHolder, int i) {

        final AmbulanceOperator ambulanceOperator = ambulanceOperatorList.get(i);

        verticalViewHolder.tv_type_name.setText(ambulanceOperator.getAmbulanceOperator());
        verticalViewHolder.tv_type_name.setSelected(true);

        if (null!= ambulanceOperator.getAmbulanceCost() && !ambulanceOperator.getAmbulanceCost().equals("")) {

            verticalViewHolder.tv_type_cost.setText(ambulanceOperator.getCurrencey_unit() + format.format(Double.valueOf(ambulanceOperator.getAmbulanceCost())));

        } else {

            verticalViewHolder.tv_type_cost.setText(ambulanceOperator.getCurrencey_unit() + ambulanceOperator.getAmbulanceCost());

        }

        /*Picasso.get().load(list_types.getAmbulanceImage()).error(R.drawable.frontal_ambulance_cab).into(holder.type_picutre);*/

        Glide.with(mContext).load(ambulanceOperator.getAmbulanceImage())
                .apply(new RequestOptions()
                        .error(R.drawable.ambulance_car))
                .into(verticalViewHolder.type_picutre);

        /*if (new PreferenceHelper(mContext).getLoginType().equals(Const.PatientService.PATIENT)) {

            verticalViewHolder.btn_fare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mAdapterCallback.onMethodCallback(
                            ambulanceOperator.getId(),
                            ambulanceOperator.getAmbulanceOperator(),
                            ambulanceOperator.getAmbulance_price_distance(),
                            ambulanceOperator.getAmbulance_price_min(),
                            ambulanceOperator.getAmbulanceImage(),
                            ambulanceOperator.getAmbulanceSeats(),
                            ambulanceOperator.getBasefare());
                }
            });

            verticalViewHolder.tv_type_cost.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mAdapterCallback.onMethodCallback(
                            ambulanceOperator.getId(),
                            ambulanceOperator.getAmbulanceOperator(),
                            ambulanceOperator.getAmbulance_price_distance(),
                            ambulanceOperator.getAmbulance_price_min(),
                            ambulanceOperator.getAmbulanceImage(),
                            ambulanceOperator.getAmbulanceSeats(),
                            ambulanceOperator.getBasefare());
                }
            });

            if (pos == i) {

                verticalViewHolder.view_select.setVisibility(View.VISIBLE);

            } else {

                verticalViewHolder.view_select.setVisibility(View.INVISIBLE);

            }

        }*/

        /*if (pos == i) {

            verticalViewHolder.view_select.setVisibility(View.VISIBLE);

        } else {

            verticalViewHolder.view_select.setVisibility(View.INVISIBLE);

        }*/

    }

    @Override
    public int getItemCount() {
        return ambulanceOperatorList.size();
    }

    public class VerticalViewHolder extends RecyclerView.ViewHolder{

        private ImageView type_picutre,btn_fare;
        private TextView tv_type_name, tv_type_cost;
        private View view_select;

        public VerticalViewHolder(@NonNull View itemView) {
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
        Log.d("HaoLS", "pos" + pos);
    }
}
