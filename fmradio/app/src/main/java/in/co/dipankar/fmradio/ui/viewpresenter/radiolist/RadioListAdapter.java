package in.co.dipankar.fmradio.ui.viewpresenter.radiolist;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.request.RequestOptions;
import in.co.dipankar.fmradio.R;
import in.co.dipankar.fmradio.data.radio.Radio;
import java.util.ArrayList;
import java.util.List;

public class RadioListAdapter extends RecyclerView.Adapter<RadioListAdapter.MyViewHolder> {
  private List<Radio> radioList;
  private Context mContext;
  private int lastPosition = -1;
  private ItemStyle mItemStyle;

  public Radio getItem(int position) {
    return radioList.get(position);
  }

  public enum ItemStyle {
    ROUND_STYLE,
    SQUARE_STYLE,
    CATEGORIES,
    SUGGESTED,
    MAIN_SUBLIST,
    VIDEO_PLAYER
  }

  public class MyViewHolder extends RecyclerView.ViewHolder {
    public ImageView image;
    public TextView status;
    public TextView text;
    private View mView;

    public MyViewHolder(View view) {
      super(view);
      mView = view;
      image = (ImageView) view.findViewById(R.id.image);
      status = (TextView) view.findViewById(R.id.status);
      text = (TextView) view.findViewById(R.id.text);
    }

    public View getView() {
      return mView;
    }
  }

  public RadioListAdapter(Context context, ItemStyle itemStyle) {
    this.radioList = new ArrayList<>();
    mContext = context;
    mItemStyle = itemStyle;
  }

  @Override
  public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    int layoutId = R.layout.item_radiolistadapter_default;
    switch (mItemStyle) {
      case SQUARE_STYLE:
        layoutId = R.layout.item_radiolistadapter_square;
        break;
      case MAIN_SUBLIST:
        layoutId = R.layout.item_radiolistadapter_main_sublist;
        break;
      case CATEGORIES:
        layoutId = R.layout.item_radiolistadapter_categories;
        break;
      case VIDEO_PLAYER:
        layoutId = R.layout.item_radiolistadapter_video_player;
        break;
      case SUGGESTED:
        layoutId = R.layout.item_radiolistadapter_suggested;
        break;
    }
    View itemView = LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
    return new MyViewHolder(itemView);
  }

  @Override
  public void onBindViewHolder(MyViewHolder holder, int position) {
    Radio radio = radioList.get(position);
    holder.text.setText(radio.getName());

    RequestOptions options =
        new RequestOptions()
            .centerCrop()
            .placeholder(R.drawable.ic_radio_gray_32)
            .error(R.drawable.ic_radio_gray_32)
            .priority(Priority.NORMAL);

    Glide.with(mContext).load(radio.getImageUrl()).apply(options).into(holder.image);

    setAnimation(holder.itemView, position);
    switch (radio.getRank()) {
      case OFFLINE:
        holder.status.setTextAppearance(mContext, R.style.TextTipStyle_Offline);
        break;
      case ONLINE:
        if (radio.getState() == Radio.STATE.LIVE_TV) {
          holder.status.setTextAppearance(mContext, R.style.TextTipStyle_LiveTv);
        } else {
          holder.status.setTextAppearance(mContext, R.style.TextTipStyle_LiveRadio);
        }
        break;
      case MOSTLY_WORKING:
        holder.status.setTextAppearance(mContext, R.style.TextTipStyle_MostlyWorking);
        break;
    }
  }

  @Override
  public int getItemCount() {
    return radioList.size();
  }

  public void setItems(List<Radio> list) {
    if (list == null) {
      list = new ArrayList<>();
    }
    this.radioList = list;
    notifyDataSetChanged();
  }

  private void setAnimation(View viewToAnimate, int position) {
    if (position > lastPosition) {
      Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.fade_in);
      viewToAnimate.startAnimation(animation);
      lastPosition = position;
    }
  }
}
