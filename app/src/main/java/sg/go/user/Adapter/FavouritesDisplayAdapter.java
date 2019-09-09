package sg.go.user.Adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import sg.go.user.Models.Favourites;
import sg.go.user.R;

import java.util.List;

/**
 * Created by asher on 2/7/2018.
 */

public class FavouritesDisplayAdapter extends RecyclerView.Adapter<FavouritesDisplayAdapter.TypesViewHolder>  {

    private Activity mContext;
    private List<Favourites> itemsFavList;

    public FavouritesDisplayAdapter(Activity context, List<Favourites> itemsFavList) {
        mContext = context;
        this.itemsFavList = itemsFavList;
    }



    public class TypesViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_fav_name, tv_fav_address;

        public TypesViewHolder(View itemView) {
            super(itemView);
            tv_fav_address = (TextView) itemView.findViewById(R.id.tv_fav_address_display);
            tv_fav_name = (TextView) itemView.findViewById(R.id.tv_fav_name_display);
        }
    }


    @Override
    public FavouritesDisplayAdapter.TypesViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.fav_display_item, null);
        FavouritesDisplayAdapter.TypesViewHolder holder = new FavouritesDisplayAdapter.TypesViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(FavouritesDisplayAdapter.TypesViewHolder typesViewHolder, int i) {
        final Favourites fav_itme = itemsFavList.get(i);
        if (fav_itme != null) {
            typesViewHolder.tv_fav_address.setText(fav_itme.getFav_Address());
            typesViewHolder.tv_fav_name.setText(fav_itme.getFav_Name());

        }
    }



    @Override
    public int getItemCount() {
        return itemsFavList.size();
    }
}

