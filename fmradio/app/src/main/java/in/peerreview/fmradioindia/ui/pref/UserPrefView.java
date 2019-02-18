package in.peerreview.fmradioindia.ui.pref;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import in.co.dipankar.quickandorid.arch.BaseView;
import in.peerreview.fmradioindia.R;
import in.peerreview.fmradioindia.ui.common.OptionView;
import in.peerreview.fmradioindia.ui.mainactivity.MainActivity;
import java.util.Map;

public class UserPrefView extends ConstraintLayout implements BaseView<UserPrefState> {

  OptionView mOptionView;
  ImageButton mClose;
  private Callback mCallabck;

  public interface Callback {
    void onClose();
  }

  public UserPrefView(Context context) {
    super(context);
    init();
  }

  private UserPrefPresenter mPresenter;

  public UserPrefView(Context context, AttributeSet attrs) {
    super(context, attrs);
    init();
  }

  public UserPrefView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init();
  }

  private void init() {
    LayoutInflater inflater =
        (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    inflater.inflate(R.layout.screen_options, this, true);
    mOptionView = findViewById(R.id.options);
    mClose = findViewById(R.id.pref_close);
    mOptionView.addCallback(
        new OptionView.Callback() {
          @Override
          public void onOptionChanged(Map<String, Boolean> opt) {
            mPresenter.onOptionChanged(opt);
          }
        });
    mClose.setOnClickListener(
        new OnClickListener() {
          @Override
          public void onClick(View view) {
            mPresenter.onOptionChanged(mOptionView.getSelectedList());
            if (mCallabck != null) {
              mCallabck.onClose();
            }
          }
        });
    mPresenter = new UserPrefPresenter();
  }

  @Override
  public void render(UserPrefState userPrefState) {
    ((MainActivity) getContext())
        .runOnUiThread(
            new Runnable() {
              @Override
              public void run() {
                if (userPrefState.getConfig() != null) {
                  mOptionView.setList(userPrefState.getConfig());
                }
              }
            });
  }

  public void addCallback(Callback callback) {
    mCallabck = callback;
  }

  @Override
  protected void onAttachedToWindow() {
    super.onAttachedToWindow();
    mPresenter.attachView(this);
  }

  @Override
  protected void onDetachedFromWindow() {
    super.onDetachedFromWindow();
    mPresenter.detachView();
  }
}
