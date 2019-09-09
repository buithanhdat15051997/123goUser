package sg.go.user.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import sg.go.user.Models.AmbulanceOperator;
import sg.go.user.R;

import java.util.ArrayList;

/**
 * Created by user on 2/13/2017.
 */

public class TypesAdapter extends BaseAdapter {

    private Context ctx;
    private ArrayList<AmbulanceOperator> typeslst;


    public TypesAdapter(Context context,ArrayList<AmbulanceOperator>typeslst) {

        this.ctx = context;
        this.typeslst = typeslst;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public int getCount() {
        return typeslst.size();
    }

    @Override
    public Object getItem(int i) {
        return typeslst.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    public View getCustomView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.autocomplete_list_text, parent, false);

        TextView textView = (TextView) row.findViewById(R.id.tvPlace);
        textView.setText(typeslst.get(position).getAmbulanceOperator());


        return row;
    }
}