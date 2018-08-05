package in.co.dipankar.fmradio.ui.viewpresenter.toolbar;

import android.content.Context;
import android.util.AttributeSet;

import in.co.dipankar.fmradio.ui.base.BasePresenter;
import in.co.dipankar.fmradio.ui.base.BaseView;

public class ToolbarView extends BaseView implements ToolbarViewPresenter.ViewContract {
    private ToolbarViewPresenter mPresenter;
    public ToolbarView(Context context) {
        super(context);
        init(context);
    }

    public ToolbarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ToolbarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        setPresenter(mPresenter);
    }

    @Override
    public void onBackPressed() {
        getNavigation().goBack();
    }
}
