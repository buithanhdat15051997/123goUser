package sg.go.user.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import sg.go.user.Models.ChatObject;
import sg.go.user.R;

import java.util.List;

/**
 * Created by Dell on 3/19/2015.
 */
public class ChatAdapter extends ArrayAdapter<ChatObject> {

    List<ChatObject> chat_data;
    Context context;
    int resource;
    ViewHolder holder = null;

    private ImageView[] dots;
    private int dotsCount;
    private int pos;


    public ChatAdapter(Context context, int resource, List<ChatObject> chat_data) {
        super(context, resource, chat_data);

        this.chat_data = chat_data;
        this.context = context;
        this.resource = resource;
    }


    private class ViewHolder {
        TextView txt_in_Message;
        TextView txt_out_Message;
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        context = parent.getContext();
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.list_item_chat_message, null);
            holder = new ViewHolder();

            holder.txt_out_Message = (TextView) convertView.findViewById(R.id.txt_out_Message);
            holder.txt_in_Message = (TextView) convertView.findViewById(R.id.txt_in_Message);



            convertView.setTag(holder);
        } else {

            holder = (ViewHolder) convertView.getTag();

        }

   //     if (chat_data.get(position).getData_type().equals("TEXT")) {
//        Log.d("pavan","type "+chat_data.get(position).getType());
//        Log.d("pavan","message "+chat_data.get(position).getMessage());
        Log.e("asher","show chat "+chat_data.get(position).getType()+" "+chat_data.get(position).getMessage());
            if (chat_data.get(position).getType().equalsIgnoreCase("UP")) {

                holder.txt_out_Message.setText(chat_data.get(position).getMessage());
                holder.txt_in_Message.setVisibility(View.GONE);
                holder.txt_out_Message.setVisibility(View.VISIBLE);
            } else {

                holder.txt_in_Message.setText(chat_data.get(position).getMessage());
                holder.txt_out_Message.setVisibility(View.GONE);
                holder.txt_in_Message.setVisibility(View.VISIBLE);
            }
      //  }

        return convertView;
    }


}
