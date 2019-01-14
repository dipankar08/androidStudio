package in.peerreview.fmradioindia.ui;

import android.content.Context;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import in.peerreview.fmradioindia.R;
import in.peerreview.fmradioindia.model.Category;
import in.peerreview.fmradioindia.model.Channel;
import java.util.ArrayList;
import java.util.List;

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.MyViewHolder> {

  public interface Callback {
    public void onClickAllButton(int i);

    public void onClickItem(String id);
  }

  private List<Category> mCategoryList;
  private Context mContext;
  private Callback mCallback;

  public class MyViewHolder extends RecyclerView.ViewHolder {
    public TextView title;
    public TextView all;
    public RecyclerView rv;

    public MyViewHolder(View view) {
      super(view);
      title = (TextView) view.findViewById(R.id.title);
      all = (TextView) view.findViewById(R.id.all);
      rv = (RecyclerView) view.findViewById(R.id.rv);
    }
  }

  public CategoriesAdapter(Context context, List<Channel> list, Callback callback) {
    this.mCategoryList = new ArrayList<>();
    mContext = context;
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

    holder.rv.setLayoutManager(new GridLayoutManager(mContext, 4));
    holder.rv.setItemAnimator(new DefaultItemAnimator());
    holder.rv.setAdapter(new CategoriesItemAdapter(mContext, c.getList()));
    holder.rv.addOnItemTouchListener(
        new RecyclerTouchListener(
            mContext,
            holder.rv,
            new RecyclerTouchListener.ClickListener() {
              @Override
              public void onClick(View view, int pos) {
                mCallback.onClickItem(mCategoryList.get(position).getList().get(pos).getId());
              }

              @Override
              public void onLongClick(View view, int position) {}
            }));
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
