package in.peerreview.ping.common.subview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.bumptech.glide.Glide;

import in.peerreview.ping.contracts.IRtcUser;
import in.co.dipankar.quickandorid.views.CircleImageView;

import java.util.List;

public class QuickContactAdapter extends RecyclerView.Adapter<QuickContactAdapter.MyViewHolder> {

  private List<IRtcUser> mUserList;
  Context mContext;

  public QuickContactAdapter(Context context, List<IRtcUser> mUserList) {
    this.mUserList = mUserList;
    this.mContext = context;
  }

  public class MyViewHolder extends RecyclerView.ViewHolder {
    public CircleImageView picture;
    public TextView name;
    public TextView status;

    public MyViewHolder(View view) {
      super(view);
      picture = (CircleImageView) view.findViewById(in.peerreview.ping.R.id.picture);
      name = (TextView) view.findViewById(in.peerreview.ping.R.id.name);
      status = (TextView) view.findViewById(in.peerreview.ping.R.id.status);
    }
  }

  @Override
  public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View itemView =
        LayoutInflater.from(parent.getContext())
            .inflate(in.peerreview.ping.R.layout.item_quick_contact, parent, false);

    return new MyViewHolder(itemView);
  }

  @SuppressLint("ResourceAsColor")
  @Override
  public void onBindViewHolder(MyViewHolder holder, int position) {
    IRtcUser user = mUserList.get(position);
    holder.name.setText(user.getUserName());
    Glide.with(mContext).load(user.getProfilePictureUrl()).into(holder.picture);
    if (user.isOnline()) {
      holder.picture.setDotColor(in.peerreview.ping.R.color.green);
      holder.status.setText("Online");
    } else {
      holder.picture.setDotColor(in.peerreview.ping.R.color.red);
      holder.status.setText("Offline");
    }
  }

  @Override
  public int getItemCount() {
    return mUserList.size();
  }

  // public API.
  public void updateList(List<IRtcUser> userList) {
    if (mUserList != null) {
      mUserList = userList;
      notifyDataSetChanged();
    }
  }
}
