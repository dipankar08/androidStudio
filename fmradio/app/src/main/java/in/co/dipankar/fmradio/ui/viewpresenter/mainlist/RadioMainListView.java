package in.co.dipankar.fmradio.ui.viewpresenter.mainlist;

import android.content.Context;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import in.co.dipankar.fmradio.R;
import in.co.dipankar.fmradio.ui.base.BaseView;

public class RadioMainListView extends BaseView {

    private RecyclerView mRecyclerView;
    private RadioMainListAdapter mRadioMainListAdapter;
    private RadioMainListViewPresenter mRadioMainListViewPresenter;
    public RadioMainListView(Context context) {
        super(context);
        init(context);
    }

    public RadioMainListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public RadioMainListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(getContext()).inflate(R.layout.view_main_list, this);
        mRadioMainListViewPresenter = new RadioMainListViewPresenter();
        setPresenter(mRadioMainListViewPresenter);
        mRecyclerView = findViewById(R.id.rv);
        mRadioMainListAdapter = new RadioMainListAdapter();
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mRadioMainListAdapter);
    }
}
