package in.peerreview.fmradioindia.ui.collist;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import in.peerreview.fmradioindia.R;
import in.peerreview.fmradioindia.model.Channel;
import java.util.ArrayList;
import java.util.List;

public class ColListAdapter extends RecyclerView.Adapter<ColListAdapter.MyViewHolder> {

    private List<Channel> channelList;
    private Context mContext;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView image;
        public TextView count;
        public TextView title;
        public TextView subtitle;

        public MyViewHolder(View view) {
            super(view);
            count = (TextView) view.findViewById(R.id.count);
            image = (ImageView) view.findViewById(R.id.image);
            title = (TextView) view.findViewById(R.id.title);
            subtitle = (TextView) view.findViewById(R.id.sub_title);
        }
    }

    public ColListAdapter(Context context) {
        this.channelList = new ArrayList<>();
        mContext = context;
    }

    @Override
    public ColListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView =
                LayoutInflater.from(parent.getContext()).inflate(R.layout.item_col, parent, false);
        return new ColListAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ColListAdapter.MyViewHolder holder, int position) {
        Channel c = channelList.get(position);
        holder.count.setText(position + 1 + "");
        holder.title.setText(c.getName());
        holder.subtitle.setText(Html.fromHtml(c.getSubTitle()));
        if (c.getImg() != null && c.getImg().length() != 0) {
            Glide.with(mContext).load(c.getImg()).into(holder.image);
        } else {
            holder.image.setImageResource(R.drawable.ic_music);
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
