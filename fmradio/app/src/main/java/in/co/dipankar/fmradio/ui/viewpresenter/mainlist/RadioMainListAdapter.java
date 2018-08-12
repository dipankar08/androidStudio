package in.co.dipankar.fmradio.ui.viewpresenter.mainlist;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import in.co.dipankar.fmradio.FmRadioApplication;
import in.co.dipankar.fmradio.entity.radio.Radio;
import in.co.dipankar.fmradio.ui.viewpresenter.sublist.RadioSubListView;

public class RadioMainListAdapter extends RecyclerView.Adapter<RadioMainListAdapter.MyViewHolder> {
    private List<String> mCat;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public RadioSubListView mRadioSubListView;
        public MyViewHolder(RadioSubListView view) {
            super(view);
            mRadioSubListView = view;
        }
    }

    public RadioMainListAdapter() {
        mCat = FmRadioApplication.Get().getRadioManager().getCategories();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(new RadioSubListView(parent.getContext()));
    }
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.mRadioSubListView.populateItem(mCat.get(position) );
    }

    @Override
    public int getItemCount() {
        return mCat.size();
    }
} 
