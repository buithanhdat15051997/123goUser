package sg.go.user;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;

import sg.go.user.Adapter.Adapter_ViewPagerFragment;

public class WellcomeSkipActivity extends AppCompatActivity {
    private ViewPager viewpager_getstarted;
    private DotsIndicator dots_indicator_getstarted;
    private Button welcome_btn_skip;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wellcome_skip);

        dots_indicator_getstarted = findViewById(R.id.dots_indicator_getstarted);
        viewpager_getstarted = findViewById(R.id.viewpager_getstarted);
        welcome_btn_skip = findViewById(R.id.welcome_btn_skip);
        showViewPager();
        welcome_btn_skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(getApplicationContext(),WelcomeActivity.class));

            }
        });


    }

    private void showViewPager() {

        Adapter_ViewPagerFragment adapter_viewPagerFragment =new Adapter_ViewPagerFragment(getSupportFragmentManager(),3);
        dots_indicator_getstarted.setViewPager(viewpager_getstarted);

        viewpager_getstarted.setAdapter(adapter_viewPagerFragment);

        viewpager_getstarted.setOffscreenPageLimit(3);//số lượng page lưu vào bộ nhớ cache để tránh tình trạng load lại
    }

}
