package in.co.dipankar.ping.common.utils;

import android.animation.Animator;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.List;

import in.co.dipankar.ping.R;

public class CustomButtonSheetView extends RelativeLayout {

    private Context mContext;
    private View mRootView;

    public interface ISheetItem{
        int getId();
        String getName();
        OnClickListener getOnClickListener();
    };

    public interface  Callback{
        public void onClick(String id);
        public void onShow();
        public void onHide();
    }

    private Callback mCallback;
    private List<ISheetItem> mItemList;
    private LinearLayout mMenuHolder;
    private List<String> mMenuItems;
    public CustomButtonSheetView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context);
    }

    public CustomButtonSheetView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public CustomButtonSheetView(Context context) {
        super(context);
        initView(context);
    }

    private void initView(Context context) {
        mContext = context;
        LayoutInflater mInflater = LayoutInflater.from(context);
        mRootView = mInflater.inflate(R.layout.view_custom_button_sheet, this, true);
        mMenuHolder = findViewById(R.id.menu_holder);
        mRootView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hide();
                return true;
            }
        });
        mRootView.setVisibility(GONE);
    }

    public void show(){
        mMenuHolder.animate().translationY(0).setDuration(150).setInterpolator(new LinearInterpolator()).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                mRootView.setVisibility(VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        if(mCallback != null){
            mCallback.onShow();
        }
    }

    public void hide(){
        mMenuHolder.animate().translationY(mMenuHolder.getHeight()).setDuration(150).setInterpolator(new LinearInterpolator()).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mRootView.setVisibility(GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                mRootView.setVisibility(GONE);
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        if(mCallback != null){
            mCallback.onHide();
        }
    }

    public void addMenu(List<ISheetItem> items){
        mItemList = items;
        for (ISheetItem menu: items) {
            Button btnTag = new Button(mContext);
            btnTag.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
            btnTag.setText(menu.getName());
            btnTag.setBackgroundColor(Color.TRANSPARENT);
            btnTag.setOnClickListener(menu.getOnClickListener());
            mMenuHolder.addView(btnTag);
        }
        mRootView.setTranslationY(mMenuHolder.getHeight());
    }

    public void setCallback(Callback callback){
        mCallback = callback;
    }

}
