package sg.go.user.Activity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import butterknife.BindView;
import butterknife.ButterKnife;
import sg.go.user.R;
import sg.go.user.SignInActivity;
import sg.go.user.Utils.Const;
import sg.go.user.Utils.PreferenceHelper;

public class HomeLoginActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.activity_home_login_btn_hospital)
    Button mHospital;

    @BindView(R.id.activity_home_login_btn_house_nursing)
    Button mHouseNursing;


    @BindView(R.id.activity_home_login_btn_patient)
    Button mConsumer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_login);
        ButterKnife.bind(this);

        mHospital.setOnClickListener(this);
        mHouseNursing.setOnClickListener(this);
        mConsumer.setOnClickListener(this);

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.activity_home_login_btn_hospital:{

                new PreferenceHelper(this).putLoginType(Const.HospitalService.HOSPITAL);
                Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
                ActivityOptions activityOptions = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.slide_in_right, R.anim.slide_out_left);
                startActivity(intent, activityOptions.toBundle());

                break;
            }

            case R.id.activity_home_login_btn_house_nursing:{

                /*Toast.makeText(this, getResources().getString(R.string.homelogin_notice), Toast.LENGTH_SHORT).show();*/

                new PreferenceHelper(this).putLoginType(Const.NursingHomeService.NURSING_HOME);
                Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
                ActivityOptions activityOptions = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.slide_in_right, R.anim.slide_out_left);
                startActivity(intent, activityOptions.toBundle());

                break;
            }

            case R.id.activity_home_login_btn_patient:{

                new PreferenceHelper(this).putLoginType(Const.PatientService.PATIENT);
                Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
                ActivityOptions activityOptions = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.slide_in_right, R.anim.slide_out_left);
                startActivity(intent, activityOptions.toBundle());


                break;
            }
        }

    }
}
