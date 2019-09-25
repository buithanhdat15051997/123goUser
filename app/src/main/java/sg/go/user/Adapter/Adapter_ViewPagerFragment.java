package sg.go.user.Adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import sg.go.user.Fragment.Fragment_Step1;
import sg.go.user.Fragment.Fragment_Step2;
import sg.go.user.Fragment.Fragment_Step3;

public class Adapter_ViewPagerFragment extends FragmentStatePagerAdapter {
    private int page_num;
    public Adapter_ViewPagerFragment(FragmentManager fm,int page_num) {
        super(fm);
        this.page_num = page_num;
    }

    @Override
    public int getCount() {
        return page_num;
    }
    @Override
    public Fragment getItem(int i) {
        switch (i) {
            case 0:
                Fragment_Step1 frm_step1 = new Fragment_Step1();
                return frm_step1;
            case 1:
                return new Fragment_Step2();
            case 2:
                return new Fragment_Step3();
            default:
                return null;
        }
    }
}
