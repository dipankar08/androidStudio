package in.co.dipankar.fmradio.ui.viewpresenter.sublist;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import java.util.List;

import in.co.dipankar.fmradio.FmRadioApplication;
import in.co.dipankar.fmradio.R;
import in.co.dipankar.fmradio.entity.radio.Radio;
import in.co.dipankar.fmradio.entity.radio.RadioManager;
import in.co.dipankar.fmradio.ui.base.BaseView;
import in.co.dipankar.fmradio.ui.base.Screen;
import in.co.dipankar.fmradio.ui.viewpresenter.shared.RecyclerTouchListener;
import in.co.dipankar.quickandorid.utils.DLog;

public class RadioSubListView extends BaseView implements RadioSubListViewPresenter.ViewContract {
    RadioSubListViewPresenter mPresenter;
    private RecyclerView mRecyclerView;
    private List<Radio> mRadioList;
    private RadioAdapter mRadioAdapter;
    private TextView mTitle;
    private String mCategories;

    private RadioManager.DataChangeCallback dataChangeCallback = new RadioManager.DataChangeCallback() {
        @Override
        public void onDataChanged(boolean done) {
            update();
        }
    };

    public RadioSubListView(Context context) {
        super(context);
        init(context);
    }
    public RadioSubListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public RadioSubListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(getContext()).inflate(R.layout.view_radio_sublist, this);
        mPresenter = new RadioSubListViewPresenter();
        setPresenter(mPresenter);
        mRecyclerView = (RecyclerView) findViewById(R.id.rv);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRadioAdapter = new RadioAdapter(getContext());
        mRecyclerView.setAdapter(mRadioAdapter);
        mRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(getContext(), mRecyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
               Radio radio = mRadioList.get(position);
                DLog.d("Now Playing "+radio.getName());
                startPlay(radio);
            }
            @Override
            public void onLongClick(View view, int position) {
            }
        }));
        mTitle = findViewById(R.id.title);
    }

    private void startPlay(Radio radio) {
        Bundle bundle = new Bundle();
        bundle.putString("ID", radio.getId());
        if(radio.isVideo()){
            getNavigation().navigate(Screen.VIDEO_PLAER_SCREEN,bundle);
        } else{
            getNavigation().navigate(Screen.PLAYER_SCREEN,bundle);
        }
    }

    public void populateItem(String s) {
        mCategories = s;
        mTitle.setText(capitilize(s));
        update();
    }

    private String capitilize(String input) {
        String output = input.substring(0, 1).toUpperCase() + input.substring(1);
        return output;
    }

    @Override
    protected void onDetachedFromWindow() {
        FmRadioApplication.Get().getRadioManager().addDataChangeCallback(dataChangeCallback);
        super.onDetachedFromWindow();
    }

    @Override
    protected void onAttachedToWindow() {
        FmRadioApplication.Get().getRadioManager().removeDataChangeCallback(dataChangeCallback);
        super.onAttachedToWindow();
    }

    private void update() {
        mRadioList = FmRadioApplication.Get().getRadioManager().getRadioByCategories(mCategories);
        mRadioAdapter.setItems(mRadioList);
    }
}

