package in.co.dipankar.fmradio.ui.viewpresenter.mainlist;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.Toast;

import in.co.dipankar.fmradio.FmRadioApplication;
import in.co.dipankar.fmradio.R;
import in.co.dipankar.fmradio.entity.radio.RadioManager;
import in.co.dipankar.fmradio.ui.base.BaseView;

public class RadioMainListView extends BaseView {

    private RecyclerView mRecyclerView;
    private RadioMainListAdapter mRadioMainListAdapter;
    private RadioMainListViewPresenter mRadioMainListViewPresenter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    public RadioMainListView(Context context) {
        super(context);
        init(context);
    }
    RadioManager.DataChangeCallback dataChangeCallback =new RadioManager.DataChangeCallback() {
        @Override
        public void onDataChanged(boolean done) {
            mRadioMainListAdapter.updateList(FmRadioApplication.Get().getRadioManager().getCategories());
        }
    };

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
        mSwipeRefreshLayout = findViewById(R.id.swiperefresh);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Toast.makeText(getContext(), "Refreshing data", Toast.LENGTH_SHORT).show();
                FmRadioApplication.Get().getRadioManager().refreshData(new RadioManager.RefreshCallback() {
                    @Override
                    public void onDone(boolean done) {
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                });
            }
        });
    }

    @Override
    protected void onDetachedFromWindow() {
        FmRadioApplication.Get().getRadioManager().addDataChangeCallback(dataChangeCallback);
        super.onDetachedFromWindow();
    }

    @Override
    protected void onAttachedToWindow() {
        if(FmRadioApplication.Get().getRadioManager()!= null) {
            FmRadioApplication.Get().getRadioManager().removeDataChangeCallback(dataChangeCallback);
            mRadioMainListAdapter.updateList(FmRadioApplication.Get().getRadioManager().getCategories());
        }
        super.onAttachedToWindow();
    }
}
