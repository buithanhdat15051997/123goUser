package sg.go.user.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import sg.go.user.R;

public class Fragment_Step2 extends Fragment {

    public Fragment_Step2() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view2 = inflater.inflate(R.layout.frm_step2, container, false);
//        Button btn =  view2.findViewById(R.id.btn_next);
//        btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                HandlerButton.change(2);
//            }
//        });
        return view2;
    }


}