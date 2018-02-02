package com.example.dip.sweepingeffect2;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

public class ResizeWidthAnimation extends Animation {
    public enum Dimention{
        WIDTH,
        HEIGHT
    };
    private Dimention mDimention;
    private int mStartWidth;
    private int mWidth;
    private int mOffset;

    private View mView;

    public ResizeWidthAnimation(View view) {
        mView = view;

    }

    public void setOffset(int offset){
        mStartWidth = mView.getMeasuredWidth();
        mWidth = mStartWidth + offset;

        mOffset = offset;
    }

    public void setDimention(Dimention dimention){
        mDimention = dimention;
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        int newWidth = mStartWidth + (int) ((mWidth - mStartWidth) * interpolatedTime);

        //int newWidth =  (int) ((mOffset) * interpolatedTime);
        if(mDimention == Dimention.HEIGHT){
            mView.getLayoutParams().height = newWidth;
        } else{
            mView.getLayoutParams().width = newWidth;
        }
        mView.requestLayout();
    }

    @Override
    public void initialize(int width, int height, int parentWidth, int parentHeight) {
        super.initialize(width, height, parentWidth, parentHeight);
    }

    @Override
    public boolean willChangeBounds() {
        return true;
    }
}

