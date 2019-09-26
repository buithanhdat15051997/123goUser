package sg.go.user.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import sg.go.user.Models.AdsList;
import sg.go.user.R;

import java.util.List;

/**
 * Created by ${Asher} on 11/14/2017.
 */

public class AdsAdapter extends RecyclerView.Adapter<AdsAdapter.MyHolder> {
    Context context;

    private List<AdsList> adsLists;

    public AdsAdapter(List<AdsList> adsLists, Context context) {
        this.adsLists = adsLists;
        this.context = context;
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.ads_list_row, parent, false);
        return new MyHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyHolder holder, int position) {
        AdsList list = adsLists.get(position);
        holder.description.setText(list.getAdDescription());

        /*Picasso.get().load(list.getAdImage()).into(holder.imageView);*/

        Glide.with(context)
                .load(list.getAdImage())
                .apply(new RequestOptions().error(R.drawable.background_user_login))
                .into(holder.imageView);

        Log.e("asher", "description  " + list.getAdImage());
    }

    @Override
    public int getItemCount() {
        return adsLists.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {

        TextView description;
        ImageView imageView;

        public MyHolder(View view) {
            super(view);
            description = (TextView) view.findViewById(R.id.adDescription);
            imageView = (ImageView) view.findViewById(R.id.adImage);

        }
    }
}