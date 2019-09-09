package sg.go.user.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import sg.go.user.R;

import java.util.List;

public class SimpleSpinnerAdapter extends ArrayAdapter<String> {

    private Context mContext;
    private List<String> mList;

    public SimpleSpinnerAdapter(Context context, List<String> list){

        super(context, R.layout.simple_spinner_layout, R.id.tv_simple_spinner_text, list);
        this.mContext = context;
        this.mList = list;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    public View getCustomView(int position, View convertView, ViewGroup parent) {

        String content = mList.get(position);

        LayoutInflater mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View mView = mLayoutInflater.inflate(R.layout.simple_spinner_layout, parent, false);

        TextView textView = (TextView) mView.findViewById(R.id.tv_simple_spinner_text);
        textView.setSelected(true);

        if (content != null){

            textView.setText(content);

        }

        return mView;
    }
}
