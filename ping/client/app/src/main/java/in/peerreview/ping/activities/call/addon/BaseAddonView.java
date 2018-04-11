package in.peerreview.ping.activities.call.addon;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;

public class BaseAddonView extends RelativeLayout {

  public interface Callback {
    void onClose();

    void onCommand();
  }

  private Callback mCallback;

  public BaseAddonView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    initView(context);
  }

  public BaseAddonView(Context context, AttributeSet attrs) {
    super(context, attrs);
    initView(context);
  }

  public BaseAddonView(Context context) {
    super(context);
    initView(context);
  }

  private void initView(Context context) {
    LayoutInflater mInflater = LayoutInflater.from(context);
    mInflater.inflate(in.peerreview.ping.R.layout.view_base_addin_view, this, true);
  }
}
