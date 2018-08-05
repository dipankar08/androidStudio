package in.co.dipankar.fmradio.ui.base;

import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

public abstract class BaseView extends RelativeLayout {
    private BasePresenter mPresenter;
    public BaseView(Context context) {
        super(context);
    }

    public BaseView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BaseView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public final Navigation getNavigation(){
        return ((BaseNavigationActivity)getContext()).getNavigation();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mPresenter.detachView();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mPresenter.attachView(this);
    }

    // this function to be called by subclass.
    protected void setPresenter(BasePresenter presenter){
        mPresenter = presenter;
    }

}
