package in.co.dipankar.fmradio.ui.viewpresenter.home;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import in.co.dipankar.fmradio.R;
import in.co.dipankar.fmradio.ui.base.BaseView;
import in.co.dipankar.fmradio.ui.base.Screen;

public class HomeView extends BaseView implements HomeViewPresenter.ViewContract{
    HomeViewPresenter mPresenter;
    ImageView mSetting;
    ImageView mSerach;
    public HomeView(Context context) {
        super(context);
        init(context);
    }

    public HomeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public HomeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(getContext()).inflate(R.layout.view_home,this);
        mPresenter = new HomeViewPresenter();
        setPresenter(mPresenter);
        mSerach = findViewById(R.id.search);
        mSetting = findViewById(R.id.setting);
        mSerach.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                showSearch();
            }
        });
    }

    private void showSearch() {
        getNavigation().navigate(Screen.SEARCH_SCREEN, null);
    }
}
