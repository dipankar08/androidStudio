package in.co.dipankar.fmradio.ui.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

import java.util.List;

import in.co.dipankar.fmradio.entity.radio.Radio;

public abstract class BaseView extends RelativeLayout {

    @Nullable private Bundle args;
    @Nullable private BasePresenter mPresenter;
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
        if(mPresenter!= null) {
            mPresenter.detachView();
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if(mPresenter!=null) {
            mPresenter.attachView(this);
        }
    }

    // this function to be called by subclass.
    protected void setPresenter(BasePresenter presenter){
        mPresenter = presenter;
    }

    public Bundle getArgs() {
        return args;
    }

    public void setArgs(Bundle args) {
        this.args = args;
    }

    // provides an eny
    public void onBack(View v){
        getNavigation().goBack();
    }

}
