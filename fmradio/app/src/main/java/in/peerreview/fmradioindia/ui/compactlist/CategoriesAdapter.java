package in.peerreview.fmradioindia.ui.compactlist;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import in.peerreview.fmradioindia.R;
import in.peerreview.fmradioindia.model.Category;
import in.peerreview.fmradioindia.model.Channel;
import in.peerreview.fmradioindia.ui.rowlist.RowListView;
import java.util.ArrayList;
import java.util.List;

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.MyViewHolder> {

  public interface Callback {
    public void onClickAllButton(int i);

    public void onClickItem(String id);
  }

  private List<Category> mCategoryList;
  private Callback mCallback;

  public class MyViewHolder extends RecyclerView.ViewHolder {
    public TextView title;
    public TextView all;
    public RowListView rowListView;

    public MyViewHolder(View view) {
      super(view);
      title = (TextView) view.findViewById(R.id.title);
      all = (TextView) view.findViewById(R.id.all);
      rowListView = (RowListView) view.findViewById(R.id.row);
    }
  }

  public CategoriesAdapter(Context context, List<Channel> list, Callback callback) {
    this.mCategoryList = new ArrayList<>();
    mCallback = callback;
  }

  @Override
  public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View itemView =
        LayoutInflater.from(parent.getContext()).inflate(R.layout.item_categories, parent, false);
    return new MyViewHolder(itemView);
  }

  @Override
  public void onBindViewHolder(MyViewHolder holder, int position) {
    Category c = mCategoryList.get(position);
    holder.title.setText(c.getName());

    holder.rowListView.setData(c.getList());
    holder.rowListView.addCallback(
        new RowListView.Callback() {
          @Override
          public void onClick(String id) {
            mCallback.onClickItem(id);
          }
        });
    holder.all.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            mCallback.onClickAllButton(position);
          }
        });
  }

  @Override
  public int getItemCount() {
    return mCategoryList.size();
  }

  public void setItems(List<Category> list) {
    this.mCategoryList = list;
    notifyDataSetChanged();
  }
}
