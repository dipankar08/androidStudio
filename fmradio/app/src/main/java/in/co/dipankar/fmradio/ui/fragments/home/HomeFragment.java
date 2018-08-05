package in.co.dipankar.fmradio.ui.fragments.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import in.co.dipankar.fmradio.R;
import in.co.dipankar.fmradio.ui.base.BaseFragment;
import in.co.dipankar.fmradio.ui.base.BasePresenter;
import in.co.dipankar.fmradio.ui.base.BaseView;
import in.co.dipankar.fmradio.ui.base.Screen;

public class HomeFragment extends BaseFragment {
    public static HomeFragment getNewFragment(Bundle args) {
        HomeFragment myFragment = new HomeFragment();
        myFragment.setArguments(args);
        return myFragment;
    }

    // The onCreateView method is called when Fragment should create its View object hierarchy,
    // either dynamically or via XML layout inflation.
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, parent, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        TextView t1 = view.findViewById(R.id.setting);
        t1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getNavigation().navigate(Screen.SETTING_SCREEN, null);
            }
        });

        TextView t2 = view.findViewById(R.id.login);
        t2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getNavigation().navigate(Screen.LOGIN_SCREEN, null);
            }
        });
    }
}