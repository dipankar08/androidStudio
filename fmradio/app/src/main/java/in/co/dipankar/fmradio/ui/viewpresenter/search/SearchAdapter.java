package in.co.dipankar.fmradio.ui.viewpresenter.search;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import in.co.dipankar.fmradio.R;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.MyViewHolder> {
    private List<SearchItem> itemsList;

    final private Context mContext;
    private static final int TYPE_RADIO = 0;
    private static final int TYPE_TITLE = 1;
    private static final int TYPE_ADDS = 2;

    public interface Callback{
        public void onClick(String id);
    }
    final private Callback mCallback;
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public MyViewHolder(View itemView) {
            super(itemView);
        }
    }

    public class RadioViewHolder extends MyViewHolder{
        public ViewGroup mContainer;
        public ImageView image;
        public TextView title, subtitle;
        public RadioViewHolder(View view) {
            super(view);
            image = view.findViewById(R.id.image);
            title = (TextView) view.findViewById(R.id.title);
            subtitle = (TextView) view.findViewById(R.id.subtitle);
            mContainer =(ViewGroup)view.findViewById(R.id.container);
        }
    }

    public class TitleViewHolder extends MyViewHolder{
        public TextView title;
        public TitleViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.title);
        }
    }

    public class AddsViewHolder extends MyViewHolder{
        public TextView title;
        public Button mMore;
        public AddsViewHolder(View view) {
            super(view);
        }
    }

    public SearchAdapter(Context context, Callback callback) {
        mContext = context;
        mCallback =callback;
        this.itemsList = new ArrayList<>();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyViewHolder viewHolder = null;
        switch (viewType){
            case TYPE_ADDS:
                View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_search_item, parent, false);
                viewHolder = new AddsViewHolder(itemView);
                break;
            case TYPE_RADIO:
                View radio = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_search_item, parent, false);
                viewHolder = new RadioViewHolder(radio);
                break;
            case TYPE_TITLE:
                View titleView = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_item_title, parent, false);
                viewHolder = new TitleViewHolder(titleView);
                break;
        }
        return viewHolder;
    }
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        switch (holder.getItemViewType()){
            case TYPE_ADDS:
                break;
            case TYPE_RADIO:
                SearchItem item = itemsList.get(position);
                RadioViewHolder holder1 = (RadioViewHolder)holder;
                Glide.with(mContext)
                        .load(item.getImage())
                        .into(holder1.image);
                holder1.title.setText( item.getTitle());
                holder1.subtitle.setText(item.getSubtitle());
                holder1.mContainer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mCallback.onClick(item.getId());
                    }
                });
                break;
            case TYPE_TITLE:
                SearchItem titleItem = itemsList.get(position);
                TitleViewHolder titleHolder = (TitleViewHolder)holder;
                titleHolder.title.setText(titleItem.getTitle());
                break;
        }
    }

    @Override
    public int getItemCount() {
        return itemsList.size();
    }

    public void setItemsList(List<SearchItem> list){
        itemsList =list;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position)
    {
        if(itemsList.get(position).getType() == SearchItem.Type.TITLE){
            return TYPE_TITLE;
        }
        else if (itemsList.get(position).getType() == SearchItem.Type.ADD){
            return TYPE_ADDS;
        }
        if(itemsList.get(position).getType() == SearchItem.Type.RADIO){
            return TYPE_RADIO;
        } else{
            return TYPE_ADDS;
        }
    }
}