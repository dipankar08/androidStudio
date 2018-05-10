package in.peerreview.fmradioindia.activities.radio;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import in.peerreview.fmradioindia.R;
import in.peerreview.fmradioindia.common.models.Node;
import java.util.ArrayList;
import java.util.List;

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.RadioItemViewHolder> {
  private static final String TAG = "RVAdapter";
  static List<Node> nodes = new ArrayList<>();
  Context mContext;
  IRadioContract.Presenter mPresenter;

  static final int skip = 10;

  RVAdapter(List<Node> persons, Context c, IRadioContract.Presenter presenter) {
    if (persons != null) {
      this.nodes = persons;
    }
    mContext = c;
    mPresenter = presenter;
  }

  public Node getItem(int idx) {
    if (nodes != null && idx >= 0 && idx < nodes.size()) {
      return nodes.get(idx);
    } else {
      return null;
    }
  }

  public class RadioItemViewHolder extends RecyclerView.ViewHolder {
    CardView cv;
    TextView sl, liveindicator;
    TextView name;
    TextView count;
    ImageView img;
    int type;

    RadioItemViewHolder(View itemView, int type) {
      super(itemView);
      this.type = type;
      cv = (CardView) itemView.findViewById(R.id.cv);
      sl = (TextView) itemView.findViewById(R.id.sl);
      name = (TextView) itemView.findViewById(R.id.name);
      count = (TextView) itemView.findViewById(R.id.count);
      liveindicator = (TextView) itemView.findViewById(R.id.liveindicator);
      img = (ImageView) itemView.findViewById(R.id.img);
      itemView.setOnClickListener(
          new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              final int pos = getAdapterPosition();
              // Toast.makeText(MainActivity.Get(), nodes.get(pos).getName(),
              // Toast.LENGTH_SHORT).show();

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
    View v =
        LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_radio, viewGroup, false);
    RadioItemViewHolder pvh = new RadioItemViewHolder(v, viewType);
    return pvh;
  }

  @Override
  public void onBindViewHolder(RadioItemViewHolder personViewHolder, int i) {
    if (personViewHolder.type == 0) {
      // this is an add view..
    } else {
      personViewHolder.sl.setText((i + 1) + "");
      personViewHolder.name.setText(nodes.get(i).getTitle());
      String msg =
          "<span color='black'>"
              + nodes.get(i).getCount()
              + "</span> plays  ."
              + nodes.get(i).getRankMessage();
      personViewHolder.count.setText(Html.fromHtml(msg), TextView.BufferType.SPANNABLE);
      Glide.with(mContext).load(nodes.get(i).getImageUrl()).into(personViewHolder.img);
    }
  }

  @Override
  public void onAttachedToRecyclerView(RecyclerView recyclerView) {
    super.onAttachedToRecyclerView(recyclerView);
  }

  public void update(List<Node> datas) {
    if (datas == null) return;
    if (nodes != null && nodes.size() >= 0) {
      nodes.clear();
    }
    nodes.addAll(datas);
    notifyDataSetChanged();
  }

  @Override
  public int getItemViewType(int position) {
    return 1;
  }
}
