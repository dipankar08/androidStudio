package in.co.dipankar.fmradio.ui.viewpresenter.home;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import in.co.dipankar.fmradio.R;
import in.co.dipankar.fmradio.ui.base.BaseView;

public class HomeView extends BaseView implements HomeViewPresenter.ViewContract{
    HomeViewPresenter mPresenter;
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
    }
}
