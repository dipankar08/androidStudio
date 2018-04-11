package in.peerreview.ping.common.subview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import in.peerreview.ping.R;
import in.peerreview.ping.activities.application.PingApplication;
import in.peerreview.ping.contracts.ICallInfo;
import in.peerreview.ping.contracts.IRtcUser;
import in.co.dipankar.quickandorid.views.CircleImageView;

import java.util.List;

public class RecentCallAdapter extends RecyclerView.Adapter<RecentCallAdapter.MyViewHolder> {

  private List<ICallInfo> mCallInfoList;
  Context mContext;

  public RecentCallAdapter(Context context, List<ICallInfo> mCallInfoList) {
    this.mCallInfoList = mCallInfoList;
    this.mContext = context;
  }

  public class MyViewHolder extends RecyclerView.ViewHolder {
    public CircleImageView picture;
    public TextView name;
    public TextView status;
    public TextView time;

    public MyViewHolder(View view) {
      super(view);
      picture = (CircleImageView) view.findViewById(R.id.picture);
      name = (TextView) view.findViewById(R.id.name);
      status = (TextView) view.findViewById(R.id.status);
      time = (TextView) view.findViewById(R.id.time);
    }
  }

  @Override
  public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View itemView =
        LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recent_call, parent, false);

    return new MyViewHolder(itemView);
  }

  @SuppressLint("ResourceAsColor")
  @Override
  public void onBindViewHolder(MyViewHolder holder, int position) {
    ICallInfo call = mCallInfoList.get(position);
    IRtcUser user = PingApplication.Get().getUserManager().getPeerUserForCall(call);
    if (user != null) {
      holder.name.setText(user.getUserName());
      String StatusText =
          "You have a "
              + call.getType().name()
              + " with duration "
              + call.getDuration()
              + " sec and data uses of "
              + call.getDataUses()
              + " kb";
      holder.status.setText(StatusText);
      holder.time.setText(call.getStartTime());
      Glide.with(mContext).load(user.getProfilePictureUrl()).into(holder.picture);

      if (user.isOnline()) {
        holder.picture.setDotColor(R.color.green);
      } else {
        holder.picture.setDotColor(R.color.red);
      }
    } else {
      holder.status.setText("");
    }
  }

  @Override
  public int getItemCount() {
    return mCallInfoList.size();
  }

  // public API.
  public void updateList(List<ICallInfo> callList) {
    if (mCallInfoList != null) {
      mCallInfoList = callList;
      notifyDataSetChanged();
    }
  }
}
