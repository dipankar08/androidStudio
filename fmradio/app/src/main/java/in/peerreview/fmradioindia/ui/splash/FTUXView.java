package in.peerreview.fmradioindia.ui.splash;

import android.content.Context;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.VideoView;

import in.peerreview.fmradioindia.R;
import in.peerreview.fmradioindia.storage.StaticResource;

public class FTUXView extends ConstraintLayout {
    private ViewPager mViewPager;
    private int index =0;
    private Button mButton;
    private VideoView mVideoView;
    public FTUXView(Context context) {
        super(context);
        init();
    }

    public FTUXView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FTUXView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    private void init() {
        LayoutInflater inflater =
                (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.pagelet_ftux1, this, true);
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mVideoView = findViewById(R.id.videoView);
        mButton = findViewById(R.id.next);
        FixedPageAdapter adapter = new FixedPageAdapter(getContext(), StaticResource.getSplashData(getContext()));
        mViewPager.setAdapter(adapter);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabDots);
        tabLayout.setupWithViewPager(mViewPager, true);
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                index = position;
                if(position < StaticResource.getSplashData(getContext()).size()-1){
                    mButton.setBackgroundResource(R.drawable.rounded_white_empty);
                    mButton.setTextColor(Color.WHITE);
                    mButton.setText("Next");
                } else{
                    mButton.setBackgroundResource(R.drawable.rounded_white_full);
                    mButton.setTextColor(Color.BLACK);
                    mButton.setText("Getting Stated");
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mViewPager.setCurrentItem(++index);
            }
        });
        Uri uri = Uri.parse("android.resource://"+getContext().getPackageName()+"/"+R.raw.backgroud_preview);
        mVideoView.setVideoURI(uri);
        mVideoView.start();
        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
            }
        });
    }
}
