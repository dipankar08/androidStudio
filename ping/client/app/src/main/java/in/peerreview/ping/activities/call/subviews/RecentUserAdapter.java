package in.peerreview.ping.activities.call.subviews;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

//import com.squareup.picasso.Picasso;

import com.bumptech.glide.Glide;

import java.util.List;

import in.peerreview.ping.R;
import in.peerreview.ping.contracts.IRtcUser;
import in.co.dipankar.quickandorid.views.CircleImageView;

public class RecentUserAdapter extends RecyclerView.Adapter<RecentUserAdapter.MyViewHolder> {

    private List<IRtcUser> mRecentUserList;
    Context mContext;

    public void updateUserList(List<IRtcUser> userList) {
        if(userList!= null) {
            mRecentUserList = userList;
            notifyDataSetChanged();
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView name;
        public CircleImageView picture;
        public MyViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.name);
            picture = (CircleImageView) view.findViewById(R.id.picture);
        }

        @Override
        public void onClick(View v) {

        }
    }

    public RecentUserAdapter(Context context, List<IRtcUser> recentUserList) {
        this.mRecentUserList = recentUserList;
        this.mContext = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recent_call, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        IRtcUser user = mRecentUserList.get(position);
        holder.name.setText(user.getUserName());
       // Picasso.get().load(user.getProfilePictureUrl()).into(holder.picture);
        //holder.picture.setImageResource();
        Glide.with(mContext)
                .load(user.getProfilePictureUrl())
                .into(holder.picture);
    }

    @Override
    public int getItemCount() {
        return mRecentUserList.size();
    }
}