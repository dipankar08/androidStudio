package in.peerreview.fmradioindia.common.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.ImageView;

import in.peerreview.fmradioindia.R;

public class DImageView extends android.support.v7.widget.AppCompatImageView {

    private Drawable enableBack, disableBack;
    boolean mViewEnable = true;
    public DImageView(Context context) {
        super(context);
        init(context,null);
    }

    public DImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs);
    }

    public DImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context,attrs);
    }

    private void init(Context context, AttributeSet attrs){
        mViewEnable  = true;
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.DImageView, 0, 0);
        try {
            enableBack = a.getDrawable(R.styleable.DImageView_enableBackground);
            disableBack = a.getDrawable(R.styleable.DImageView_disableBackground);
            mViewEnable = a.getBoolean(R.styleable.DImageView_enabled, true);
        } finally {
            a.recycle();
        }
        setViewEnabled(mViewEnable);
    }

    public void setViewEnabled(boolean enabled) {
        if(enabled){
            if(enableBack != null) {
                this.setImageDrawable(enableBack);
            }
        } else{
            if( disableBack != null){
                this.setImageDrawable(disableBack);
            }
        }
        mViewEnable = enabled;
    }
    public boolean isViewEnabled() {
        return mViewEnable;
    }
}
