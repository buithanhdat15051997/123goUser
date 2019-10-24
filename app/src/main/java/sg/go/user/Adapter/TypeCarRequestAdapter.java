package sg.go.user.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import sg.go.user.Models.TypeCarRequest;
import sg.go.user.R;

public class TypeCarRequestAdapter extends RecyclerView.Adapter<TypeCarRequestAdapter.ViewholderTypeCar> {
    private Context context_typecar_request;
    private ArrayList<TypeCarRequest> typeCar_Request_ArrayList;
    private OnItemClickListener onItemClickListenerType;
    private List<LinearLayout> itemViewList = new ArrayList<LinearLayout>();

    public interface OnItemClickListener {
        public void onItemClick(View view, int position);
    }

    public TypeCarRequestAdapter(Context context_typecar_request, ArrayList<TypeCarRequest> typeCar_Request_ArrayList, OnItemClickListener onItemClickListener) {
        this.context_typecar_request = context_typecar_request;
        this.typeCar_Request_ArrayList = typeCar_Request_ArrayList;
        this.onItemClickListenerType = onItemClickListener;
    }

    @NonNull
    @Override
    public ViewholderTypeCar onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());

        View v = inflater.inflate(R.layout.item_type_car, viewGroup, false);

        final ViewholderTypeCar myViewHolder = new ViewholderTypeCar(v);


//        v.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                for(View tempItemView : itemViewList) {
//                    /** navigate through all the itemViews and change color
//                     of selected view to colorSelected and rest of the views to colorDefault **/
//                    if(itemViewList.get(myViewHolder.getAdapterPosition()) == tempItemView) {
//                        tempItemView.setBackgroundResource(R.color.red);
//                    }
//                    else{
//                        tempItemView.setBackgroundResource(R.color.white);
//                    }
//                }
//            }
//        });


        return new ViewholderTypeCar(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewholderTypeCar viewholderTypeCar, final int i) {

        itemViewList.add(viewholderTypeCar.linear_typeCar);

        Log.d("Dat_Test", itemViewList.size() + "");

        viewholderTypeCar.linear_typeCar.setBackgroundColor(context_typecar_request.getResources().getColor(R.color.white));

        viewholderTypeCar.txt_Item_Type_Car.setText(typeCar_Request_ArrayList.get(i).getName_Type_Car_Request());

        viewholderTypeCar.txt_Item_Type_service_fee.setText(typeCar_Request_ArrayList.get(i).getName_Type_service_fee() + " S$");

        if (typeCar_Request_ArrayList.get(i).getImga_Type_Car_Request() != null) {

            Glide.with(context_typecar_request).load(typeCar_Request_ArrayList.get(i).getImga_Type_Car_Request()).into(viewholderTypeCar.img_Item_Type_Car);

        } else {

            viewholderTypeCar.img_Item_Type_Car.setImageResource(typeCar_Request_ArrayList.get(i).getImga_Type_Car_Request_Int());
        }

        // interface onclick

        viewholderTypeCar.linear_typeCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                onItemClickListenerType.onItemClick(view, i);

                Log.d("Dat_Test", i + "");

                switch (i) {
                    case 0:
                        itemViewList.get(0).setBackgroundColor(context_typecar_request.getResources().getColor(R.color.color_background_main));
                        itemViewList.get(1).setBackgroundColor(context_typecar_request.getResources().getColor(R.color.white));
                        itemViewList.get(2).setBackgroundColor(context_typecar_request.getResources().getColor(R.color.white));

                        if (itemViewList.size() == 4) {

                            itemViewList.get(3).setBackgroundColor(context_typecar_request.getResources().getColor(R.color.white));

                        }
                        break;

                    case 1:
                        itemViewList.get(1).setBackgroundColor(context_typecar_request.getResources().getColor(R.color.color_background_main));
                        itemViewList.get(0).setBackgroundColor(context_typecar_request.getResources().getColor(R.color.white));
                        itemViewList.get(2).setBackgroundColor(context_typecar_request.getResources().getColor(R.color.white));

                        if (itemViewList.size() == 4) {

                            itemViewList.get(3).setBackgroundColor(context_typecar_request.getResources().getColor(R.color.white));
                        }

                        break;

                    case 2:
                        itemViewList.get(2).setBackgroundColor(context_typecar_request.getResources().getColor(R.color.color_background_main));
                        itemViewList.get(0).setBackgroundColor(context_typecar_request.getResources().getColor(R.color.white));
                        itemViewList.get(1).setBackgroundColor(context_typecar_request.getResources().getColor(R.color.white));

                        if (itemViewList.size() == 4) {

                            itemViewList.get(3).setBackgroundColor(context_typecar_request.getResources().getColor(R.color.white));
                        }

                        break;

                    case 3:
                        itemViewList.get(3).setBackgroundColor(context_typecar_request.getResources().getColor(R.color.color_background_main));
                        itemViewList.get(1).setBackgroundColor(context_typecar_request.getResources().getColor(R.color.white));
                        itemViewList.get(2).setBackgroundColor(context_typecar_request.getResources().getColor(R.color.white));
                        itemViewList.get(0).setBackgroundColor(context_typecar_request.getResources().getColor(R.color.white));
                        break;

                }


            }
        });


    }

    @Override
    public int getItemCount() {
        return typeCar_Request_ArrayList.size();
    }

    public class ViewholderTypeCar extends RecyclerView.ViewHolder {

        TextView txt_Item_Type_Car, txt_Item_Type_service_fee;
        ImageView img_Item_Type_Car;
        LinearLayout linear_typeCar;


        public ViewholderTypeCar(@NonNull View itemView) {
            super(itemView);

            txt_Item_Type_Car = itemView.findViewById(R.id.txt_Item_Type_Car);
            img_Item_Type_Car = itemView.findViewById(R.id.img_Item_Type_Car);
            txt_Item_Type_service_fee = itemView.findViewById(R.id.txt_Item_Type_service_fee);
            linear_typeCar = itemView.findViewById(R.id.linear_typeCar);

        }
    }
}
