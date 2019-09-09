package sg.go.user.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import sg.go.user.R;

import java.util.ArrayList;


/**
 * Created by user on 2/6/2017.
 */

public class SearchPlaceAdapter extends BaseAdapter
{
    private  ArrayList<String> stringList;
    private Context mContext;
    public SearchPlaceAdapter(Context context, ArrayList<String> stringList)
    {
        this.stringList=stringList;
        mContext=context;
    }

    @Override
    public int getCount() {
        return stringList.size();
    }

    @Override
    public Object getItem(int i)
    {
       return stringList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
       view= LayoutInflater.from(mContext).inflate(R.layout.autocomplete_list_text,null);

        TextView searchText= (TextView) view.findViewById(R.id.tvPlace);
        if(stringList.size()>0) {
            searchText.setText(stringList.get(i));
        }

        return view;
    }

}
