package in.peerreview.fmradioindia.common.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;

import in.peerreview.fmradioindia.R;

/**
 * Created by dip on 2/27/18.
 */

public class DTextView extends android.support.v7.widget.AppCompatTextView {
    private String mFont;
    private Context mContext;

    public DTextView(Context context) {
        super(context);
        mContext = context;
    }

    public DTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.DTextView, 0, 0);

        try {
            //TODO verify if the font is set (chashing if not set)
            mFont = a.getString(R.styleable.DTextView_font);
            if (mFont != null) {
                Typeface typeface = Typeface.createFromAsset(context.getAssets(), "fonts/" + mFont);
                this.setTypeface(typeface);
            }
        } finally {
            a.recycle();
        }
    }

    public String getFont() {
        return mFont;
    }

    public void setFont(String mFont) {
        this.mFont = mFont;
        Typeface typeface = Typeface.createFromAsset(mContext.getAssets(), "fonts/" + mFont);
        this.setTypeface(typeface);
        invalidate();
        requestLayout();
    }

    @Override
    public boolean isInEditMode() {
        return true;
    }
}