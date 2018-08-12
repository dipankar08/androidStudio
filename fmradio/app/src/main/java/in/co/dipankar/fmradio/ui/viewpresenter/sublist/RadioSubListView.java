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
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import in.co.dipankar.fmradio.FmRadioApplication;
import in.co.dipankar.fmradio.R;
import in.co.dipankar.fmradio.entity.radio.Radio;
import in.co.dipankar.fmradio.ui.base.BaseView;
import in.co.dipankar.fmradio.ui.base.Screen;
import in.co.dipankar.quickandorid.utils.DLog;

public class RadioSubListView extends BaseView implements RadioSubListViewPresenter.ViewContract {
    RadioSubListViewPresenter mPresenter;
    private RecyclerView mRecyclerView;
    private List<Radio> mRadioList;
    private RadioAdapter mRadioAdapter;
    private TextView mTitle;
    private String mCategories;

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

        mRadioList = FmRadioApplication.Get().getRadioManager().getRadioList();
        mRecyclerView = (RecyclerView) findViewById(R.id.rv);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRadioAdapter = new RadioAdapter(getContext(), mRadioList);
        mRecyclerView.setAdapter(mRadioAdapter);
        mRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(getContext(), mRecyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
               Radio radio = mRadioList.get(position);
                DLog.d("Now Playing "+radio.getName());
                startPlay(position);
            }
            @Override
            public void onLongClick(View view, int position) {
            }
        }));

        mTitle = findViewById(R.id.title);
    }

    private void startPlay(int pos) {
        Bundle bundle = new Bundle();
        bundle.putString("cat",mCategories);
        bundle.putSerializable("index", pos);
        getNavigation().navigate(Screen.PLAYER_SCREEN,bundle);
    }

    public void populateItem(String s) {
        mCategories = s;
        mTitle.setText(s);
        mRadioList = FmRadioApplication.Get().getRadioManager().getRadioByCategories(s);
        mRadioAdapter.setItems(mRadioList);
    }
}

