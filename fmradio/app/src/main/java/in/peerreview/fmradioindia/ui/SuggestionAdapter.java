package in.peerreview.fmradioindia.ui;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import in.peerreview.fmradioindia.R;
import in.peerreview.fmradioindia.model.Channel;

public class SuggestionAdapter extends RecyclerView.Adapter<SuggestionAdapter.MyViewHolder> {

  private List<Channel> channelList;
  private Context mContext;

  public class MyViewHolder extends RecyclerView.ViewHolder {
    public ImageView image;
    public TextView title;

    public MyViewHolder(View view) {
      super(view);
      image = (ImageView) view.findViewById(R.id.image);
      title = (TextView) view.findViewById(R.id.title);
    }
  }

  public SuggestionAdapter(Context context) {
    this.channelList = new ArrayList<>();
    mContext = context;
  }

  @Override
  public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View itemView =
        LayoutInflater.from(parent.getContext()).inflate(R.layout.item_suggestion, parent, false);
    return new MyViewHolder(itemView);
  }

  @Override
  public void onBindViewHolder(MyViewHolder holder, int position) {
    Channel c = channelList.get(position);
    holder.title.setText(c.getName());
    if (c.getImg() != null && c.getImg().length() != 0) {
      Glide.with(mContext).load(c.getImg()).into(holder.image);
    } else{
     holder.image.setImageResource(R.drawable.ic_search);
    }
  }

  @Override
  public int getItemCount() {
    return channelList.size();
  }

  public void setItems(List<Channel> list) {
    this.channelList = list;
    notifyDataSetChanged();
  }
}
