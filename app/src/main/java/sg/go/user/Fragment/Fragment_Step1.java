package sg.go.user.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import sg.go.user.R;

public class Fragment_Step1 extends Fragment {
 //   private HandlerButton handlerButton;
    public Fragment_Step1() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view1 = inflater.inflate(R.layout.frm_step1, container, false);
//        Button btn =  view1.findViewById(R.id.btn_next);
//        btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                handlerButton.change(1);
//            }
//        });
        return view1;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
     //   handlerButton =(HandlerButton)context;
    }
}
