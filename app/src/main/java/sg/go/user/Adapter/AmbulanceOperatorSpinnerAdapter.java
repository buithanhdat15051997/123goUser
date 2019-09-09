package sg.go.user.Adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import sg.go.user.Models.AmbulanceOperator;
import sg.go.user.R;

import java.util.List;

public class AmbulanceOperatorSpinnerAdapter extends ArrayAdapter<AmbulanceOperator> {

    private Context mActivity;
    private List<AmbulanceOperator> mAmbulanceOperators;
    private View mView;
    private ImageView mAmbulanceOperatorImage;
    private TextView mAmbulanceOperatorName;



    public AmbulanceOperatorSpinnerAdapter(Activity mActivity, List<AmbulanceOperator> mAmbulanceOperators) {

        super(mActivity, R.layout.ambulance_operator_spinner_item, R.id.tv_ambulance_operator, mAmbulanceOperators);
        this.mActivity = mActivity;
        this.mAmbulanceOperators = mAmbulanceOperators;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    private View getCustomView(int position, View convertView, ViewGroup parent){

        AmbulanceOperator ambulanceOperator = mAmbulanceOperators.get(position);

        LayoutInflater layoutInflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        mView = layoutInflater.inflate(R.layout.ambulance_operator_spinner_item, parent, false);

        mAmbulanceOperatorImage = (ImageView) mView.findViewById(R.id.img_ambulance_operator);
        mAmbulanceOperatorName = (TextView) mView.findViewById(R.id.tv_ambulance_operator);

        if (ambulanceOperator != null){

            Glide.with(mActivity)
                    .load(ambulanceOperator.getAmbulanceImage())
                    .apply(new RequestOptions().error(R.drawable.ambulance_car))
                    .into(mAmbulanceOperatorImage);

            mAmbulanceOperatorName.setText(ambulanceOperator.getAmbulanceOperator());
            mAmbulanceOperatorName.setSelected(true);

        }

        return mView;
    }
}
