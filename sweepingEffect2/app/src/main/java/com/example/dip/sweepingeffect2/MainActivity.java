package com.example.dip.sweepingeffect2;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.RelativeLayout;

public class MainActivity extends AppCompatActivity {

    private RelativeLayout parent, body, panel;
    private Button btnHide, btnShow;
    private int PANEL_WIDTH  = 350;
    private int BUTTON_MARGIN = 20;
    private int BUTTON_LENGTH = 100;
    private int BUTTON_WIDTH = 20;
    float dX, dY;
    private int mOrientation = Configuration.ORIENTATION_PORTRAIT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }
    void init() {
        parent = findViewById(R.id.parent);
        body = findViewById(R.id.body);
        panel = findViewById(R.id.panel);
        btnHide = findViewById(R.id.hide_btn);
        btnShow= findViewById(R.id.show_btn);

        btnHide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hidePanel();
            }
        });
        btnShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPanel();
            }
        });

        handleMoveEffect();
        putInButton();
    }

    public void hidePanel(){
        if(mOrientation == Configuration.ORIENTATION_PORTRAIT){
            panel.animate().translationY(PANEL_WIDTH).setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(200);
        } else{
            panel.animate().translationX(PANEL_WIDTH).setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(200);
        }
    }

    public void showPanel(){
        if(mOrientation == Configuration.ORIENTATION_PORTRAIT){
            panel.animate().translationY(0).setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(200);
        } else{
            panel.animate().translationX(0).setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(200);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void handleMoveEffect() {
        /*
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
        */
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

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mOrientation = newConfig.orientation;
        Log.d("DIPANKAR","New Orientation:"+mOrientation);
        switch(mOrientation) {
            case Configuration.ORIENTATION_LANDSCAPE:
                putInRight();
                break;
            case Configuration.ORIENTATION_PORTRAIT:
                putInButton();
                break;
        }
    }
}
