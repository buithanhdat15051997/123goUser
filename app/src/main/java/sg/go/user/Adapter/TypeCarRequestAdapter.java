package sg.go.user.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import sg.go.user.Fragment.BillingInfoFragment;
import sg.go.user.Models.TypeCarRequest;
import sg.go.user.R;
import sg.go.user.Utils.Const;

public class TypeCarRequestAdapter extends RecyclerView.Adapter<TypeCarRequestAdapter.ViewholderTypeCar> {
    Context context_typecar_request;
    ArrayList<TypeCarRequest> typeCar_Request_ArrayList;
    OnItemClickListener onItemClickListenerType;
    public interface OnItemClickListener {
        public void onItemClick(View view, int position);
    }

    public TypeCarRequestAdapter(Context context_typecar_request, ArrayList<TypeCarRequest> typeCar_Request_ArrayList,OnItemClickListener onItemClickListener) {
        this.context_typecar_request = context_typecar_request;
        this.typeCar_Request_ArrayList = typeCar_Request_ArrayList;
        this.onItemClickListenerType = onItemClickListener;
    }

    @NonNull
    @Override
    public ViewholderTypeCar onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater =LayoutInflater.from(viewGroup.getContext());
        View v = inflater.inflate(R.layout.item_type_car,viewGroup,false);
        return new ViewholderTypeCar(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewholderTypeCar viewholderTypeCar, final int i) {

        viewholderTypeCar.txt_Item_Type_Car.setText(typeCar_Request_ArrayList.get(i).getName_Type_Car_Request());

        viewholderTypeCar.txt_Item_Type_service_fee.setText(typeCar_Request_ArrayList.get(i).getName_Type_service_fee()+" S$");

        if(typeCar_Request_ArrayList.get(i).getImga_Type_Car_Request() != null){

            Glide.with(context_typecar_request).load(typeCar_Request_ArrayList.get(i).getImga_Type_Car_Request()).into(viewholderTypeCar.img_Item_Type_Car);
        }else {

            viewholderTypeCar.img_Item_Type_Car.setImageResource(typeCar_Request_ArrayList.get(i).getImga_Type_Car_Request_Int());
        }
      // interface onclick
        viewholderTypeCar.img_Item_Type_Car.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                onItemClickListenerType.onItemClick(view,i);

            }
        });


    }

    @Override
    public int getItemCount() {
        return typeCar_Request_ArrayList.size();
    }

    public class ViewholderTypeCar extends RecyclerView.ViewHolder {

        TextView txt_Item_Type_Car,txt_Item_Type_service_fee;
        ImageView img_Item_Type_Car;


        public ViewholderTypeCar(@NonNull View itemView) {
            super(itemView);

            txt_Item_Type_Car = itemView.findViewById(R.id.txt_Item_Type_Car);
            img_Item_Type_Car = itemView.findViewById(R.id.img_Item_Type_Car);
            txt_Item_Type_service_fee = itemView.findViewById(R.id.txt_Item_Type_service_fee);


        }
    }
}
