package in.co.dipankar.livetv.ui;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import in.co.dipankar.livetv.R;
import in.co.dipankar.livetv.data.Channel;
import java.util.ArrayList;
import java.util.List;

public class TvListAdapter extends RecyclerView.Adapter<TvListAdapter.MyViewHolder> {

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

  public TvListAdapter(Context context) {
    this.channelList = new ArrayList<>();
    mContext = context;
  }

  @Override
  public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View itemView =
        LayoutInflater.from(parent.getContext()).inflate(R.layout.tv_item1, parent, false);
    return new MyViewHolder(itemView);
  }

  @Override
  public void onBindViewHolder(MyViewHolder holder, int position) {
    Channel c = channelList.get(position);
    holder.title.setText(c.getName());
    if(c.getImg()!= null) {
        Glide.with(mContext).load(c.getImg()).into(holder.image);
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
