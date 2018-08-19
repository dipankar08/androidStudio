package in.co.dipankar.fmradio.ui.viewpresenter.sublist;

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

import java.util.List;

import in.co.dipankar.fmradio.R;
import in.co.dipankar.fmradio.entity.radio.Radio;

public class RadioAdapter extends RecyclerView.Adapter<RadioAdapter.MyViewHolder> {
    private List<Radio> radioList;
    private Context mContext;
    private int lastPosition = -1;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView image;
        public TextView status;
        public TextView text;

        public MyViewHolder(View view) {
            super(view);
            image = (ImageView) view.findViewById(R.id.image);
            status = (TextView) view.findViewById(R.id.status);
            text = (TextView) view.findViewById(R.id.text);
        }
    }

    public RadioAdapter(Context context, List<Radio> radioList) {
        this.radioList = radioList;
        mContext = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_radio_sublist_item, parent, false);
        return new MyViewHolder(itemView);
    }
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Radio radio = radioList.get(position);
        holder.text.setText(radio.getName());

        Glide.with(mContext)
                .load(radio.getImageUrl())
                .into(holder.image);
        setAnimation(holder.itemView, position);

    }
    @Override
    public int getItemCount() {
        return radioList.size();
    }
    public void setItems(List<Radio> list) {
        this.radioList = list;
        notifyDataSetChanged();
    }

    private void setAnimation(View viewToAnimate, int position) {
        if (position > lastPosition)
        {
            Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.fade_in);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }

}