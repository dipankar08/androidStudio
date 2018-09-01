package in.co.dipankar.fmradio.ui.viewpresenter.categoriesview;

import android.content.Context;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import java.util.List;

import in.co.dipankar.fmradio.FmRadioApplication;
import in.co.dipankar.fmradio.R;
import in.co.dipankar.fmradio.data.radio.Radio;
import in.co.dipankar.fmradio.ui.base.BaseView;
import in.co.dipankar.fmradio.ui.viewpresenter.radiolist.RadioListAdapter;
import in.co.dipankar.fmradio.ui.viewpresenter.shared.RecyclerTouchListener;
import in.co.dipankar.fmradio.ui.viewpresenter.toolbar.ToolbarView;

public class CategoriesView extends BaseView {
    private ToolbarView mToolbar;
    private RecyclerView mRecyclerView;
    private RadioListAdapter mAdapter;
    private List<Radio> mCurList;
    private int mCurIndex;
    public CategoriesView(Context context) {
        super(context);
        init();
    }

    public CategoriesView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        String cat = getArgs().getString("CAT");
        mToolbar.setTitle(cat);
        mCurList = FmRadioApplication.Get().getRadioManager().getRadioByCategories(cat);
        mAdapter.setItems(mCurList);
    }

    public CategoriesView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.view_categories,this);
        mToolbar = findViewById(R.id.tool_bar);
        mRecyclerView = (RecyclerView) findViewById(R.id.rv);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), calculateNoOfColumns()));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mAdapter = new RadioListAdapter(getContext(), RadioListAdapter.ItemStyle.CATEGORIES);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(getContext(), mRecyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                mCurIndex = position;
                FmRadioApplication.Get().getMusicController().play(mCurList.get(mCurIndex).getId());
            }
            @Override
            public void onLongClick(View view, int position) {
            }
        }));
    }
    public  int calculateNoOfColumns() {
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int noOfColumns = (int) (dpWidth / 100);
        return noOfColumns;
    }
}
