package com.example.dip.sweepingeffect2;

import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

public class MainActivity extends AppCompatActivity {

    private RelativeLayout parent, body;

    private RelativeLayout mPanelRelativeLayout, mControlHolderRelativeLayout;
    private ImageButton mHideImageButton, mShowImageButton;
    private RemoteButtonView mRemoteButtonView;
    private ZoomButtonView mZoomButtomView;


    private int PANEL_WIDTH  = 950;
    private int BUTTON_MARGIN = 8;

    private int MOVE_BUTTON_HEIGHT = 150;
    private int MOVE_BUTTON_WIDTH = 150;
    private int ZOOM_BUTTON_HEIGHT = 150;
    private int ZOOM_BUTTON_WIDTH = 80;
    private int TWO_BUTTON_GAP = 25;


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
        mRemoteButtonView = new RemoteButtonView(this);
        mZoomButtomView = new ZoomButtonView(this);

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
                newParams = new RelativeLayout.LayoutParams( PANEL_WIDTH,
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
                mHideImageButton.setImageResource(R.drawable.line_horizantal);
                break;
            case LANDSCAPE:
                buttonParams.removeRule(RelativeLayout.ALIGN_PARENT_TOP);
                buttonParams.removeRule(RelativeLayout.CENTER_HORIZONTAL);
                buttonParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 1);
                buttonParams.addRule(RelativeLayout.CENTER_VERTICAL, 1);
                buttonParams.setMargins(BUTTON_MARGIN, 0, BUTTON_MARGIN, 0);
                mHideImageButton.setLayoutParams(buttonParams);
                mHideImageButton.setImageResource(R.drawable.line_vertical);
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
        RelativeLayout.LayoutParams buttonParams = new RelativeLayout.LayoutParams(
                MOVE_BUTTON_WIDTH,MOVE_BUTTON_WIDTH);
        switch(mOrientation){
            case PORTRAIT:
                buttonParams.setMargins(0,0,BUTTON_MARGIN,0);
                buttonParams.addRule(RelativeLayout.LEFT_OF,R.id.zoom_btn);
                break;
            case LANDSCAPE:
                buttonParams.setMargins(0,0,0,BUTTON_MARGIN);
                break;
            default:return;
        }
        mRemoteButtonView.setLayoutParams(buttonParams);
    }

    private void renderCameraControlZoomButtonView(){
        RelativeLayout.LayoutParams buttonParams = new RelativeLayout.LayoutParams(
                ZOOM_BUTTON_WIDTH,ZOOM_BUTTON_HEIGHT);
       // buttonParams.addRule(,R.id.move_btn);
        mZoomButtomView.setLayoutParams(buttonParams);



/*
        switch(mOrientation){
            case PORTRAIT:
                RelativeLayout.LayoutParams buttonParams = new RelativeLayout.LayoutParams(
                        ZOOM_BUTTON_WIDTH,ZOOM_BUTTON_HEIGHT);
                buttonParams.removeRule(RelativeLayout.BELOW);
                buttonParams.addRule(RelativeLayout.BELOW,mRemoteButtonView.getId());
                mZoomButtomView.setLayoutParams(buttonParams);
                mZoomButtomView.invalidate();
                break;
            case LANDSCAPE:
                RelativeLayout.LayoutParams buttonParam = new RelativeLayout.LayoutParams(
                        ZOOM_BUTTON_HEIGHT,ZOOM_BUTTON_WIDTH);
                buttonParam.removeRule(RelativeLayout.RIGHT_OF);
                buttonParam.addRule(RelativeLayout.BELOW,R.id.move_btn);
                mZoomButtomView.setLayoutParams(buttonParam);
                mZoomButtomView.setRotation(90);
                break;
        }
        */
    }


    private void setCameraControlEventListner(){
        /*
        mHideImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideCameraControlPanel();
            }
        });
        mShowImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCameraControlPanel();
            }
        });
        mPanelRelativeLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return handleCameraControlTouchEvent();

            }
        });
        */
    }


    /*

    

    public void hidePanel(){
        if(mOrientation == Configuration.ORIENTATION_PORTRAIT){
            panel.animate().translationY(PANEL_WIDTH).setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(200);

            ResizeWidthAnimation anim = new ResizeWidthAnimation(body);
            anim.setDimention(ResizeWidthAnimation.Dimention.HEIGHT);
            anim.setOffset(PANEL_WIDTH);
            anim.setInterpolator(new AccelerateDecelerateInterpolator());
            anim.setDuration(200);
            body.startAnimation(anim);

        } else{
            panel.animate().translationX(PANEL_WIDTH).setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(200);
            ResizeWidthAnimation anim = new ResizeWidthAnimation(body);
            anim.setDimention(ResizeWidthAnimation.Dimention.WIDTH);
            anim.setOffset(PANEL_WIDTH);
            anim.setInterpolator(new AccelerateDecelerateInterpolator());
            anim.setDuration(200);
            body.startAnimation(anim);
        }
    }

    public void showPanel(){
        if(mOrientation == Configuration.ORIENTATION_PORTRAIT){
            panel.animate().translationY(0).setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(200);
            ResizeWidthAnimation anim = new ResizeWidthAnimation(body);
            anim.setDimention(ResizeWidthAnimation.Dimention.HEIGHT);
            anim.setOffset(0);
            anim.setInterpolator(new AccelerateDecelerateInterpolator());
            anim.setDuration(200);
            body.startAnimation(anim);
        } else{
            panel.animate().translationX(0).setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(200);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void handleMoveEffect() {

        panel.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        dX = view.getX() - event.getRawX();
                        dY = view.getY() - event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if(mOrientation == Configuration.ORIENTATION_LANDSCAPE){

                            view.animate()
                                    .x(event.getRawX() + dX)
                                    .setDuration(0)
                                    .start();
                        } else{
                            Log.d("DIPANKAR =>",""+(event.getRawY() + dY));
                            if(event.getRawX() + dY >= PANEL_WIDTH){
                                break;
                            }
                            view.animate()
                                    .y(event.getRawY() + dY)
                                    .setDuration(0)
                                    .start();
                        }

                        break;
                    default:
                        return false;
                }
                return true;
            }
        });

    }

    void putInButton(){
        //LayoutBodyOnPotrateMode();
        LayoutPanelOnPotrateMode();
        LayoutButtonOnPotrateMode();
    }

    void putInRight(){
        //LayoutBodyOnLandscapeMode();
        LayoutPanelOnLandscapeMode();
        LayoutButtonOnLandscapeMode();
    }

    private void  LayoutBodyOnPotrateMode(){
        body.requestLayout();
        RelativeLayout.LayoutParams newParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                body.getMeasuredHeight() - PANEL_WIDTH);;
        body.setLayoutParams(newParams);
    }
    private void LayoutBodyOnLandscapeMode(){
        body.requestLayout();
        RelativeLayout.LayoutParams newParams = new RelativeLayout.LayoutParams(
                body.getMeasuredWidth() - PANEL_WIDTH,
                RelativeLayout.LayoutParams.MATCH_PARENT
                );;
        body.setLayoutParams(newParams);
    }

    private void LayoutPanelOnPotrateMode() {
        // second Button
        RelativeLayout.LayoutParams newParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                PANEL_WIDTH);;
        newParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, 1);
        panel.setLayoutParams(newParams);
    }

    private void LayoutButtonOnPotrateMode() {
        RelativeLayout.LayoutParams buttonParams = new RelativeLayout.LayoutParams(
                BUTTON_LENGTH,BUTTON_WIDTH
        );;
        buttonParams.removeRule(RelativeLayout.ALIGN_PARENT_LEFT);
        buttonParams.removeRule(RelativeLayout.CENTER_VERTICAL);
        buttonParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 1);
        buttonParams.addRule(RelativeLayout.CENTER_HORIZONTAL, 1);
        buttonParams.setMargins(0, BUTTON_MARGIN, 0, 0);
        btnHide.setLayoutParams(buttonParams);
    }

    private void LayoutButtonOnLandscapeMode() {
        RelativeLayout.LayoutParams buttonParams = new RelativeLayout.LayoutParams(
                BUTTON_WIDTH, BUTTON_LENGTH
        );
        buttonParams.removeRule(RelativeLayout.ALIGN_PARENT_TOP);
        buttonParams.removeRule(RelativeLayout.CENTER_HORIZONTAL);
        buttonParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 1);
        buttonParams.addRule(RelativeLayout.CENTER_VERTICAL, 1);
        buttonParams.setMargins(BUTTON_MARGIN, 0, 0, 0);
        btnHide.setLayoutParams(buttonParams);
    }

    private void LayoutPanelOnLandscapeMode() {
        RelativeLayout.LayoutParams newParams = new RelativeLayout.LayoutParams(
                PANEL_WIDTH,RelativeLayout.LayoutParams.MATCH_PARENT
        );;
        newParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 1);
        panel.setLayoutParams(newParams);
    }

    */


}
