package com.example.dip.sweepingeffect2;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

public class ViewResizeAnimation extends Animation {
    public enum Dimention{
        WIDTH,
        HEIGHT
    };

    private View mView;
    private Dimention mDimention;
    private int mStart;
    private int mEnd;

    public ViewResizeAnimation(View view, Dimention dimention, int start, int end) {
        mView = view;
        mDimention = dimention;
        mStart = start;
        mEnd = end;
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        int newDim = mStart + (int) ((mEnd - mStart) * interpolatedTime);
        if(mDimention == Dimention.HEIGHT){
            mView.getLayoutParams().height = newDim;
        } else{
            mView.getLayoutParams().width = newDim;
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

