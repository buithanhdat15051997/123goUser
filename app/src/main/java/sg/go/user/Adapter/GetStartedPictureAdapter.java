package sg.go.user.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import sg.go.user.Models.ItemGetstartedPicture;

public class GetStartedPictureAdapter extends PagerAdapter {

    Context context;
    ArrayList<ItemGetstartedPicture> listlinkimage;

    public GetStartedPictureAdapter(Context context, ArrayList<ItemGetstartedPicture> listlinkimage) {
        this.context = context;
        this.listlinkimage = listlinkimage;
    }
    @Override
    public int getCount() {
        return listlinkimage.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == o;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        ImageView imageView = new ImageView(context);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);


        if (listlinkimage.get(0)==null) {

        } else {
            imageView.setImageResource(listlinkimage.get(position).getPicture_viewpager_started());
//            Picasso.get().load(listlinkimage.get(position)).into(imageView);
            container.addView(imageView);

        }
        return imageView;
    }
    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
