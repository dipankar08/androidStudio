package in.co.dipankar.fmradio.ui.viewpresenter.toolbar;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import in.co.dipankar.fmradio.R;
import in.co.dipankar.fmradio.ui.base.BasePresenter;
import in.co.dipankar.fmradio.ui.base.BaseView;

public class ToolbarView extends BaseView{
    private TextView mTitle;
    public ToolbarView(Context context) {
        super(context);
        init(null);
    }

    public ToolbarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public ToolbarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(@Nullable AttributeSet set){
        LayoutInflater.from(getContext()).inflate(R.layout.view_toolbar, this);
        final ImageView mback = findViewById(R.id.back);
        mTitle = findViewById(R.id.title);
        mback.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                getNavigation().goBack();
            }
        });
        if(set != null){
            TypedArray ta = getContext().obtainStyledAttributes(set, R.styleable.ToolbarView);
            String text = ta.getString(R.styleable.ToolbarView_title);
            mTitle.setText(text);
        }
    }
    public void setTitle(String title){
        mTitle.setText(title);
    }
}
