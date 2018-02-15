package in.peerreview.fmradioindia.activities.radio.view;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import in.peerreview.fmradioindia.R;
import in.peerreview.fmradioindia.activities.radio.IRadioContract;
import in.peerreview.fmradioindia.activities.radio.model.RadioNode;


public class RVAdapter extends RecyclerView.Adapter<RVAdapter.RadioItemViewHolder>{
    private static final String TAG = "RVAdapter" ;
    static List<RadioNode> nodes = new ArrayList<>();
    Context mContext;
    IRadioContract.Presenter mPresenter;

    final static  int skip =10;
    RVAdapter(List<RadioNode> persons, Context c, IRadioContract.Presenter presenter){
        if (persons != null){
            this.nodes = persons;
        }
        mContext = c;
        mPresenter = presenter;
    }

    public class RadioItemViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        TextView sl,liveindicator;
        TextView name;
        TextView count;
        ImageView img;
        int type;

        RadioItemViewHolder(View itemView,int type) {
            super(itemView);
            this.type = type;
            cv = (CardView)itemView.findViewById(R.id.cv);
            sl = (TextView)itemView.findViewById(R.id.sl);
            name = (TextView)itemView.findViewById(R.id.name);
            count = (TextView)itemView.findViewById(R.id.count);
            liveindicator = (TextView)itemView.findViewById(R.id.liveindicator);
            img = (ImageView)itemView.findViewById(R.id.img);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    final int pos = getAdapterPosition();
                    //Toast.makeText(MainActivity.Get(), nodes.get(pos).getName(), Toast.LENGTH_SHORT).show();
                    mPresenter.play(pos);
                }
            });
        }
    }


    @Override
    public int getItemCount() {
        return nodes.size();
    }


    @Override
    public RadioItemViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_radio, viewGroup, false);
        RadioItemViewHolder pvh = new RadioItemViewHolder(v,viewType);
        return pvh;
    }


    @Override
    public void onBindViewHolder(RadioItemViewHolder personViewHolder, int i) {
        if(personViewHolder.type ==0){
            // this is an add view..
        } else{
            personViewHolder.sl.setText((i+1)+"");
            personViewHolder.name.setText(nodes.get(i).getName());
            String msg = nodes.get(i).getCount()+" plays  ";
            int per =0;
            if(nodes.get(i).getCount() == 0){
                msg+= ".  Play first";
            }
            else if((nodes.get(i).getSuccess() == 0 && nodes.get(i).getError() > 0)) {
                msg+= ".  Streaming Issue";
            }
            else if((nodes.get(i).getSuccess()+nodes.get(i).getError()) > 0){
                per = (int)((float)nodes.get(i).getSuccess()/(nodes.get(i).getSuccess()+nodes.get(i).getError())*100);
                msg+= ".  "+ per+"% working";
            } else {
                msg+= ".  100+% working";
            }
            personViewHolder.count.setText(msg);
            Glide.with(mContext)
                    .load(nodes.get(i).getImg())
                    .override(70, 70)
                    .into(personViewHolder.img);
        }
    }


    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }


    public void update(List<RadioNode> datas){
        if(datas == null)
            return;
        if (nodes != null && nodes.size()>=0){
            nodes.clear();
        }
        nodes.addAll(datas);
        notifyDataSetChanged();
    }


    @Override
    public int getItemViewType(int position){
        return 1;
    }
}