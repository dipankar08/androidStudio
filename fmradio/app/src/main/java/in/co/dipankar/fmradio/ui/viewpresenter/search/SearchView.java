package in.co.dipankar.fmradio.ui.viewpresenter.search;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import in.co.dipankar.fmradio.FmRadioApplication;
import in.co.dipankar.fmradio.R;
import in.co.dipankar.fmradio.entity.radio.Radio;
import in.co.dipankar.fmradio.ui.base.BaseView;
import in.co.dipankar.fmradio.ui.base.Screen;
import in.co.dipankar.fmradio.ui.viewpresenter.shared.RecyclerTouchListener;
import in.co.dipankar.quickandorid.utils.DLog;

import static com.facebook.FacebookSdk.getApplicationContext;

public class SearchView extends BaseView{
    private SearchViewPresenter mPresenter;

    private EditText mSearch;
    private RecyclerView mRV;
    private SearchAdapter mAdapter;
    public SearchView(Context context) {
        super(context);
        init();
    }

    public SearchView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SearchView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.view_search, this);
        mPresenter = new SearchViewPresenter();
        setPresenter(mPresenter);
        mAdapter = new SearchAdapter(getContext(), new SearchAdapter.Callback() {
            @Override
            public void onClick(String id) {
                playThis(id);
            }
        });
        mSearch = findViewById(R.id.search_btn);
        mRV = findViewById(R.id.rv);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mRV.setLayoutManager(mLayoutManager);
        mRV.setItemAnimator(new DefaultItemAnimator());
        mRV.setAdapter(mAdapter);

        mSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                performSearch(mSearch.getText().toString());
            }
        });

    }

    private void playThis(String id) {
        Bundle bundle= new Bundle();
        bundle.putString("ID", id);
        getNavigation().navigate(Screen.PLAYER_SCREEN, bundle);
    }

    private void performSearch(String str){
        Map<String, List<Radio>> map = FmRadioApplication.Get().getRadioManager().searchRadio(str);
        List<SearchItem> list = new ArrayList<>();
        for(Map.Entry<String, List<Radio>> item : map.entrySet()) {
            String key = item.getKey();
            list.add(new SearchItem("0",key,"","", SearchItem.Type.TITLE));
            for( Radio r : item.getValue()) {
                list.add(new SearchItem(r.getId(), r.getName(), r.getSubTitle(), r.getImageUrl(), SearchItem.Type.RADIO));
            }
        }
        mAdapter.setItemsList(list);
    }
}
