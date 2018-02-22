package in.peerreview.fmradioindia.common.views;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import in.peerreview.fmradioindia.R;
import in.peerreview.fmradioindia.common.models.MusicNode;

public class MusicHozListView extends LinearLayout {
    //interface
    public interface IMusicNode{
        String getTitle();
        String getImageUrl();
    }
    public interface  IMusicHozListViewCallback{
        void onButtonClicked();
        void onItemClicked(int pos);
    }

    private View rootView;
    private TextView title;
    private RecyclerView rv;
    private TextView showall;
    private ContactsAdapter adapter;
    private IMusicHozListViewCallback mIMusicHozListViewCallback;
    private final OnClickListener mOnClickListener = new OnClickListener() {
            @Override
            public void onClick(final View view) {
                int itemPosition = rv.getChildLayoutPosition(view);
                mIMusicHozListViewCallback.onItemClicked(itemPosition);
            }
    };


    public MusicHozListView(Context context) {
        super(context);
        init(context);
    }

    public MusicHozListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }


    public void prepareView(String titleText, String ButtonText, IMusicHozListViewCallback iMusicHozListViewCallback){
        mIMusicHozListViewCallback = iMusicHozListViewCallback;
        title.setText(titleText);
        showall.setText(ButtonText);
    }
    public void updateData(List<MusicNode> viewModels) {
        if(adapter != null){
            adapter.updateData(viewModels);
        }
        if(viewModels.size() > 0){
            rootView.setVisibility(VISIBLE);
        } else{
            rootView.setVisibility(GONE);
        }
    }

    //private
    private void init(Context context) {
        rootView = inflate(context, R.layout.customview_horizontal_list_view, this);
        rootView.setVisibility(GONE);

        title = rootView.findViewById(R.id.title);
        showall = rootView.findViewById(R.id.showall);
        rv = rootView.findViewById(R.id.rv);

        showall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if(mIMusicHozListViewCallback != null){
                   mIMusicHozListViewCallback.onButtonClicked();
               }
            }
        });

        LinearLayoutManager layoutManager
                = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        List<MusicNode> initList = new ArrayList<MusicNode>();
        adapter = new ContactsAdapter(context, initList);
        // Attach the adapter to the recyclerview to populate items
        rv.setAdapter(adapter);
        rv.setLayoutManager(layoutManager);

    }

    //private adapter
    private class ContactsAdapter extends  RecyclerView.Adapter<ContactsAdapter.ViewHolder> {
        private List<MusicNode> mMusicNode;
        private Context mContext;
        public ContactsAdapter(Context context, List<MusicNode> contacts) {
            mMusicNode = contacts;
            mContext = context;
        }

        // Easy access to the context object in the recyclerview
        private Context getContext() {
            return mContext;
        }
        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView titleView;
            public ImageView imageView;
            public ViewHolder(View itemView) {
                super(itemView);
                titleView = (TextView) itemView.findViewById(R.id.item_text);
                imageView = (ImageView) itemView.findViewById(R.id.item_image);
            }
        }

        @Override
        public ContactsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);
            // Inflate the custom layout
            View contactView = inflater.inflate(R.layout.customview_horizontal_list_view_item, parent, false);
            // Return a new holder instance
            ViewHolder viewHolder = new ViewHolder(contactView);
            contactView.setOnClickListener(mOnClickListener);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ContactsAdapter.ViewHolder viewHolder, int position) {
            MusicNode contact = mMusicNode.get(position);
            TextView textView = viewHolder.titleView;
            textView.setText(contact.getTitle());
            ImageView image = viewHolder.imageView;
            Picasso.with(mContext).load(contact.getImageUrl()).fit().centerCrop().into(image);
        }
        @Override
        public int getItemCount() {
            return mMusicNode.size();
        }

        public void updateData(List<MusicNode> viewModels) {
            mMusicNode.clear();
            mMusicNode.addAll(viewModels);
            notifyDataSetChanged();
        }
    }

}