package in.co.dipankar.fmradio.ui.viewpresenter.ftux;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import in.co.dipankar.fmradio.R;
import in.co.dipankar.fmradio.ui.base.BaseView;

public class FtuxView extends BaseView {

    private TextView dotsTextView[];
    private int dotsCount;
    public FtuxView(Context context) {
        super(context);
        init(context);
    }

    public FtuxView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public FtuxView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }
    private void init(Context context){
        LayoutInflater.from(getContext()).inflate(R.layout.view_ftux, this);
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(new CustomPagerAdapter(getContext(), list));
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);
        viewPager.setCurrentItem(0);
        loadDots();
    }

    protected void loadDots() {
        LinearLayout mDotsLayout = findViewById(R.id.dot_holder);
        dotsCount = list.size();
        dotsTextView = new TextView[dotsCount];
        for (int i = 0; i < dotsCount; i++) {
            dotsTextView[i] = new TextView(getContext());
            dotsTextView[i].setText("•");
            dotsTextView[i].setTextSize(30);
            dotsTextView[i].setPadding(5,0,5,0);
            dotsTextView[i].setTypeface(null, Typeface.BOLD);
            dotsTextView[i].setTextColor(android.graphics.Color.GRAY);
            mDotsLayout.addView(dotsTextView[i]);
        }
        dotsTextView[0].setTextColor(Color.WHITE);
    }

    // Add info here

    private static final List<FtuxInfo> list = new ArrayList<FtuxInfo>(){{
        add(new FtuxInfo(
                "Enjoy the Music Anytime Anywhere",
                "Enjoy live FM broadcasts from your lovely city - Delhi, Mumbai, kolkata, Bengaluru, Hyderabad, Pune and Chenni."
        ));
        add(new FtuxInfo(
                "500+ Live FM from India",
                "All the languages and city are provided under different categories to make it easier for you to choose."
        ));
        add(new FtuxInfo(
                "Save your favorite FM",
                "Mark channels as favorite and find them in favorite section for quick select."
        ));
        add(new FtuxInfo(
                "Play Background while browsing",
                "Play FM Radio India in background and continue using your phone for other purpose and use play/pause/stop on notification."
        ));
        add(new FtuxInfo(
                "Enjoy 30 days ads-free Music",
                "Get 30 Days free subscription on Sign in to the app. Go for One Time \"ADS FREE\" In-App-Purchase just for INR 100."
        ));
    }};
    private final ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

        @Override
        public void onPageSelected(int position) {
            for (int i = 0; i < list.size(); i++) {
                dotsTextView[i].setTextColor(Color.GRAY);
            }
            dotsTextView[position].setTextColor(Color.WHITE);
        }

        @Override
        public void onPageScrollStateChanged(int state) {}
    };

}
