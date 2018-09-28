package in.co.dipankar.fmradio.ui.viewpresenter.mainlist;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import in.co.dipankar.fmradio.R;
import in.co.dipankar.fmradio.ui.viewpresenter.sublist.RadioSubListView;
import java.util.ArrayList;
import java.util.List;

public class RadioMainListAdapter extends RecyclerView.Adapter<RadioMainListAdapter.MyViewHolder> {
  private List<String> mCat;
  private int lastPosition = -1;

  public class MyViewHolder extends RecyclerView.ViewHolder {
    public RadioSubListView mRadioSubListView;

    public MyViewHolder(RadioSubListView view) {
      super(view);
      mRadioSubListView = view;
    }
  }

  public RadioMainListAdapter() {
    mCat = new ArrayList<>();
  }

  @Override
  public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    return new MyViewHolder(new RadioSubListView(parent.getContext()));
  }

  @Override
  public void onBindViewHolder(MyViewHolder holder, int position) {
    holder.mRadioSubListView.populateItem(mCat.get(position));
    setAnimation(holder.itemView, position);
  }

  @Override
  public int getItemCount() {
    return mCat.size();
  }

  private void setAnimation(View viewToAnimate, int position) {
    if (position > lastPosition) {
      Animation animation =
          AnimationUtils.loadAnimation(viewToAnimate.getContext(), R.anim.fade_in);
      viewToAnimate.startAnimation(animation);
      lastPosition = position;
    }
  }

  public void updateList(List<String> cat) {
    mCat = cat;
    notifyDataSetChanged();
  }
}
