package sg.go.user.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import sg.go.user.R;


public class SpinnerLanguageAdapter extends ArrayAdapter<String> {

    private Context ctx;
    private String[] contentArray;
    private Integer[] imageArray;

    public SpinnerLanguageAdapter(Context context, int resource, String[] objects,
                                  Integer[] imageArray) {
        super(context, resource, R.id.tv_flag_name, objects);
        /*super(context, R.layout.spinner_value_layout, R.id.tv_flag_name, objects);*/
        this.ctx = context;
        this.contentArray = objects;
        this.imageArray = imageArray;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    public View getCustomView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.spinner_value_layout, parent, false);

        TextView textView = (TextView) row.findViewById(R.id.tv_flag_name);
        textView.setText(contentArray[position]);

        ImageView imageView = (ImageView) row.findViewById(R.id.flag_icon);

        if(null!=imageArray[position]) {
            imageView.setImageResource(imageArray[position]);
        } else {
            imageView.setVisibility(View.GONE);
        }

        return row;
    }
}