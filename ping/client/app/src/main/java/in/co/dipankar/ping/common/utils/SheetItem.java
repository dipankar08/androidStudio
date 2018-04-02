package in.co.dipankar.ping.common.utils;

import android.view.View;

import in.co.dipankar.ping.common.utils.CustomButtonSheetView;

/**
 * Created by dip on 4/1/18.
 */

public class SheetItem implements CustomButtonSheetView.ISheetItem{
    int mId;
    String mName;
    View.OnClickListener mOnClickListener;

    public SheetItem(int i, String s, View.OnClickListener onClickListener) {
        mId = i;
        mName = s;
        mOnClickListener = onClickListener;
    }

    @Override
    public int getId() {
        return mId;
    }

    @Override
    public String getName() {
        return mName;
    }

    @Override
    public View.OnClickListener getOnClickListener() {
        return mOnClickListener;
    }
}
