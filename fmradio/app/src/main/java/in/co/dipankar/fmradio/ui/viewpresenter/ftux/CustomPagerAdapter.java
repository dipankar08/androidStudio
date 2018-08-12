package in.co.dipankar.fmradio.ui.viewpresenter.ftux;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import in.co.dipankar.fmradio.R;

public class CustomPagerAdapter extends PagerAdapter {

    private Context mContext;
    private List<FtuxInfo> mFtuxInfo;

    public CustomPagerAdapter(Context context, List<FtuxInfo> ftxInfo) {
        mContext = context;
        mFtuxInfo = ftxInfo;
    }

    @Override
    public Object instantiateItem(ViewGroup collection, int position) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.view_ftux_item, collection, false);
        ((TextView)view.findViewById(R.id.title)).setText(mFtuxInfo.get(position).getTitle());
        ((TextView)view.findViewById(R.id.sub_title)).setText(mFtuxInfo.get(position).getSubtitle());
        collection.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup collection, int position, Object view) {
        collection.removeView((View) view);
    }

    @Override
    public int getCount() {
        return mFtuxInfo.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mFtuxInfo.get(position).getTitle();
    }
}