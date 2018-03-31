package in.co.dipankar.ping.common.subview;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import in.co.dipankar.ping.R;
import in.co.dipankar.ping.contracts.IRtcUser;
import in.co.dipankar.quickandorid.views.CircleImageView;

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
            picture = (CircleImageView) view.findViewById(R.id.picture);
            name = (TextView) view.findViewById(R.id.name);
            status = (TextView) view.findViewById(R.id.status);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_quick_contact, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        IRtcUser user = mUserList.get(position);
        holder.name.setText(user.getUserName());
        holder.status.setText("Online");
        Glide.with(mContext)
                .load(user.getProfilePictureUrl())
                .into(holder.picture);
    }

    @Override
    public int getItemCount() {
        return mUserList.size();
    }

    //public API.
    public void updateList(List<IRtcUser> userList) {
        if(mUserList!= null) {
            mUserList = userList;
            notifyDataSetChanged();
        }
    }
}