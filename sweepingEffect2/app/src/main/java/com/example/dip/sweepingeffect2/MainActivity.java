package com.example.dip.sweepingeffect2;

import android.animation.Animator;
import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class MainActivity extends AppCompatActivity {

    private RelativeLayout parent, body;

    private RelativeLayout mPanelRelativeLayout;
    private RelativeLayout  mControlHolderRelativeLayout;
    private ImageButton mHideImageButton, mShowImageButton;
    private RemoteButtonView mRemoteButtonView;
    private ZoomButtonView mZoomButtomView;
    private Button mTest;


    private final int PANEL_WIDTH  = 450;
    private final float PANEL_HIDE_CUTOFF_DISTANCE = PANEL_WIDTH/2;
    private final float PANEL_VALID_TOUCH_MOVE_CUTOFF_DISTANCE = 20;
    private int BUTTON_MARGIN = 8;

    private int MOVE_BUTTON_HEIGHT = 150;
    private int MOVE_BUTTON_WIDTH = 150;
    private int ZOOM_BUTTON_HEIGHT = 300;
    private int ZOOM_BUTTON_WIDTH = 120;
    private int TWO_BUTTON_GAP = 50;


    float dX, dY;
    private Orientation mOrientation = Orientation.PORTRAIT;

    private enum Orientation {
        PORTRAIT,
        LANDSCAPE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        parent = findViewById(R.id.parent);
        body = findViewById(R.id.body);
        mTest = findViewById(R.id.test);
        initPanelView();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mOrientation = (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) ?
            Orientation.LANDSCAPE: Orientation.PORTRAIT;
        renderCameraControlOnDisplayMode();
    }

    void initPanelView() {

        mPanelRelativeLayout = findViewById(R.id.panel);
        mControlHolderRelativeLayout = findViewById(R.id.control_holder);
        mRemoteButtonView = findViewById(R.id.move_btn);
        mZoomButtomView = findViewById(R.id.zoom_btn);

        mHideImageButton = (ImageButton) findViewById(R.id.hide_btn);
        mShowImageButton = (ImageButton) findViewById(R.id.show_btn);
        setCameraControlEventListner();
        renderCameraControlOnDisplayMode();
    }
    

    private void renderCameraControlOnDisplayMode(){

        Log.d("DIPANKAR ","Now Orientaion is:"+mOrientation);
        renderCameraControlPanel();
        renderCameraControlHideButton();
        renderCameraControlHolder();
        renderCameraControlRemoteButtonView();
        renderCameraControlZoomButtonView();
    }

    private void renderCameraControlPanel(){
        RelativeLayout.LayoutParams newParams;
        switch(mOrientation){
            case PORTRAIT:
                newParams = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.MATCH_PARENT,
                        PANEL_WIDTH);;
                newParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, 1);
                mPanelRelativeLayout.setLayoutParams(newParams);
                break;
            case LANDSCAPE:
                newParams = new RelativeLayout.LayoutParams(PANEL_WIDTH,
                        RelativeLayout.LayoutParams.MATCH_PARENT
                        );;
                newParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 1);
                mPanelRelativeLayout.setLayoutParams(newParams);
                break;
        }
    }

    private void renderCameraControlHideButton(){
        RelativeLayout.LayoutParams buttonParams = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        switch(mOrientation){
            case PORTRAIT:
                buttonParams.removeRule(RelativeLayout.ALIGN_PARENT_LEFT);
                buttonParams.removeRule(RelativeLayout.CENTER_VERTICAL);
                buttonParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 1);
                buttonParams.addRule(RelativeLayout.CENTER_HORIZONTAL, 1);
                buttonParams.setMargins(0, BUTTON_MARGIN, 0, BUTTON_MARGIN);
                mHideImageButton.setLayoutParams(buttonParams);
                mHideImageButton.setBackground( getResources().getDrawable(R.drawable.line_horizantal));

                break;
            case LANDSCAPE:
                buttonParams.removeRule(RelativeLayout.ALIGN_PARENT_TOP);
                buttonParams.removeRule(RelativeLayout.CENTER_HORIZONTAL);
                buttonParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 1);
                buttonParams.addRule(RelativeLayout.CENTER_VERTICAL, 1);
                buttonParams.setMargins(BUTTON_MARGIN, 0, BUTTON_MARGIN, 0);
                mHideImageButton.setLayoutParams(buttonParams);
                mHideImageButton.setBackground( getResources().getDrawable(R.drawable.line_vertical));
                break;
        }
    }

    private void renderCameraControlHolder(){
        RelativeLayout.LayoutParams buttonParams = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        buttonParams.addRule(RelativeLayout.CENTER_IN_PARENT, 1);

        switch(mOrientation){
            case PORTRAIT:
                buttonParams.removeRule(RelativeLayout.RIGHT_OF);
                buttonParams.addRule(RelativeLayout.BELOW, R.id.hide_btn);
                break;
            case LANDSCAPE:
                buttonParams.removeRule(RelativeLayout.BELOW);
                buttonParams.addRule(RelativeLayout.RIGHT_OF, R.id.hide_btn);
                break;
            default: return;
        }
        mControlHolderRelativeLayout.setLayoutParams(buttonParams);
    }

    private void renderCameraControlRemoteButtonView(){
        RelativeLayout.LayoutParams buttonParams = (RelativeLayout.LayoutParams) mRemoteButtonView.getLayoutParams();
        switch(mOrientation){
            case PORTRAIT:
                buttonParams.setMargins(0,0,TWO_BUTTON_GAP,0);
                break;
            case LANDSCAPE:
                buttonParams.setMargins(0,0,0,TWO_BUTTON_GAP);
                break;
            default:return;
        }
        mRemoteButtonView.setLayoutParams(buttonParams);
    }

    private void renderCameraControlZoomButtonView(){
        RelativeLayout.LayoutParams buttonParams = (RelativeLayout.LayoutParams) mZoomButtomView.getLayoutParams();
        switch(mOrientation){
            case PORTRAIT:
                //buttonParams = new RelativeLayout.LayoutParams(ZOOM_BUTTON_WIDTH,ZOOM_BUTTON_HEIGHT);
                buttonParams.removeRule(RelativeLayout.BELOW);
                buttonParams.addRule(RelativeLayout.RIGHT_OF,R.id.move_btn);
                buttonParams.addRule(RelativeLayout.CENTER_IN_PARENT);
                mZoomButtomView.setLayout(ZoomButtonView.Layout.VERTICAL);
                mZoomButtomView.setLayoutParams(buttonParams);
                break;
            case LANDSCAPE:
                //buttonParams = new RelativeLayout.LayoutParams(ZOOM_BUTTON_HEIGHT,ZOOM_BUTTON_WIDTH);
                buttonParams.removeRule(RelativeLayout.RIGHT_OF);
                buttonParams.addRule(RelativeLayout.BELOW,R.id.move_btn);
                buttonParams.addRule(RelativeLayout.CENTER_IN_PARENT);
                mZoomButtomView.setLayout(ZoomButtonView.Layout.HORIZONTAL);
                mZoomButtomView.setLayoutParams(buttonParams);
                break;
            default:return;
        }
    }


    // button event handling
    private void setCameraControlEventListner(){

        mHideImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("DIPANKAR","HIDE BUTTON CLICKED");
                hideCameraControlPanelAnimation();
            }
        });
        mShowImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("DIPANKAR","SHOW BUTTON CLICKED");
                showCameraControlPanelAnimation();
            }
        });
        mPanelRelativeLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return handleCameraControlTouchEvent(event);

            }
        });
        mTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("DIPANKAR","Test BUTTON CLICKED");
                /*
                if(mZoomButtomView.getVisibility() != GONE) {
                    mZoomButtomView.setVisibility(GONE);
                } else{
                    mZoomButtomView.setVisibility(VISIBLE);
                }*/
                showMoveControlOnly();
                enableTopDownMoveControlOnly();
            }
        });

    }


    // show hide animation when button clicked.
    void hideCameraControlPanelAnimation(){
        float x = 0;
        float y = 0;
        switch(mOrientation){
            case PORTRAIT:
                x = 0;
                y = PANEL_WIDTH;
                break;
            case LANDSCAPE:
                x =  PANEL_WIDTH;
                y = 0;
                break;
            default:return;
        }
        mPanelRelativeLayout
                .animate()
                .translationX(x)
                .translationY(y)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .setDuration(100)
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mPanelRelativeLayout.setVisibility(GONE);
                    }
                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }
                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }

                });
    }

    void showCameraControlPanelAnimation(){
        mPanelRelativeLayout
            .animate()
            .translationY(0)
            .translationX(0)
            .setInterpolator(new AccelerateDecelerateInterpolator())
            .setDuration(100)
            .setListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    mPanelRelativeLayout.setVisibility(VISIBLE);
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
    }


    //handling touch slide animation.
    private float orgX, orgY,downX,downY,curX, curY;
    boolean handleCameraControlTouchEvent(MotionEvent event){
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                orgX = mPanelRelativeLayout.getX();
                orgY = mPanelRelativeLayout.getY();
                downX = event.getRawX();
                downY = event.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                 curX = orgX + event.getRawX() - downX;
                 curY = orgY + event.getRawY() - downY;
                if(mOrientation == Orientation.PORTRAIT){
                    if(curY > orgY){
                        mPanelRelativeLayout.animate()
                                .y(curY)
                                .setDuration(0)
                                .start();
                    }
                } else{
                    if(curX > orgX){
                        mPanelRelativeLayout.animate()
                                .x(curX)
                                .setDuration(0)
                                .start();
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                 curX = orgX + event.getRawX() - downX;
                 curY = orgY + event.getRawY() - downY;
                Log.d("DIPANKAR11:",dX+"::"+dY);
                if(mOrientation == Orientation.PORTRAIT){
                    if(Math.abs(event.getRawX() - downX) < PANEL_VALID_TOUCH_MOVE_CUTOFF_DISTANCE &&
                            Math.abs(event.getRawY() - downY)< PANEL_VALID_TOUCH_MOVE_CUTOFF_DISTANCE){
                        return false;
                    }
                    if(curY - orgY < PANEL_HIDE_CUTOFF_DISTANCE){
                        showCameraControlPanelAnimation();
                    } else{
                        hideCameraControlPanelAnimation();
                    }
                } else{
                    if(curX - orgX < PANEL_HIDE_CUTOFF_DISTANCE){
                        showCameraControlPanelAnimation();
                    } else{
                        hideCameraControlPanelAnimation();
                    }
                }
                break;
            default:
                return false;
        }
        return true;
    }

    //set of helper functions
    private void showZoomControlOnly() {
        mZoomButtomView.setVisibility(VISIBLE);
        mRemoteButtonView.setVisibility(GONE);
    }

    private void showMoveControlOnly() {
        mZoomButtomView.setVisibility(GONE);
        mRemoteButtonView.setVisibility(VISIBLE);
    }

    private void showAllControl() {
        mZoomButtomView.setVisibility(VISIBLE);
        mRemoteButtonView.setVisibility(VISIBLE);
    }

    private void enableTopDownMoveControlOnly() {
        mRemoteButtonView.setEnableUpDownOnly();
    }

    private void enableLeftRightMoveControlOnly() {
        mRemoteButtonView.setEnableLeftRightOnly();
    }

    private void enableAllMoveControl() {
        mRemoteButtonView.setEnableAllControl();
    }

    private void enableZoomControlHorizontally() {
        mZoomButtomView.setLayout(ZoomButtonView.Layout.HORIZONTAL);
    }

    private void enableZoomControlVertically() {
        mZoomButtomView.setLayout(ZoomButtonView.Layout.VERTICAL);
    }

    private void resetControlState() {
        showAllControl();
        enableZoomControlVertically();
        enableAllMoveControl();
    }

    // Text view resize animations 
}
