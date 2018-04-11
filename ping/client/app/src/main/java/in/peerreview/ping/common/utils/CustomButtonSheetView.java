package in.peerreview.ping.common.utils;

import android.animation.Animator;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomButtonSheetView extends RelativeLayout {

  private Context mContext;
  private View mRootView;
  private Map<Integer, DialogInterface> mDialogMap;

  public enum Type {
    BUTTON,
    OPTIONS,
  }

  public interface ISheetItem {
    Type getType();

    int getId();

    String getName();

    CharSequence[] getPossibleValue();

    Callback getCallback();
  };

  public interface Callback {
    public void onClick(int id);
  }

  private Callback mCallback;
  private List<ISheetItem> mItemList;
  private LinearLayout mMenuHolder;
  private List<String> mMenuItems;

  public CustomButtonSheetView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    initView(context);
  }

  public CustomButtonSheetView(Context context, AttributeSet attrs) {
    super(context, attrs);
    initView(context);
  }

  public CustomButtonSheetView(Context context) {
    super(context);
    initView(context);
  }

  private void initView(Context context) {
    mContext = context;
    LayoutInflater mInflater = LayoutInflater.from(context);
    mRootView = mInflater.inflate(in.peerreview.ping.R.layout.view_custom_button_sheet, this, true);
    mMenuHolder = findViewById(in.peerreview.ping.R.id.menu_holder);
    mRootView.setOnTouchListener(
        new OnTouchListener() {
          @Override
          public boolean onTouch(View v, MotionEvent event) {
            hide();
            return true;
          }
        });
    mRootView.setVisibility(GONE);
    mDialogMap = new HashMap<>();
  }

  public void show() {
    mMenuHolder
        .animate()
        .translationY(0)
        .setDuration(150)
        .setInterpolator(new LinearInterpolator())
        .setListener(
            new Animator.AnimatorListener() {
              @Override
              public void onAnimationStart(Animator animation) {
                mRootView.setVisibility(VISIBLE);
              }

              @Override
              public void onAnimationEnd(Animator animation) {}

              @Override
              public void onAnimationCancel(Animator animation) {}

              @Override
              public void onAnimationRepeat(Animator animation) {}
            });
  }

  public void hide() {
    mMenuHolder
        .animate()
        .translationY(mMenuHolder.getHeight())
        .setDuration(150)
        .setInterpolator(new LinearInterpolator())
        .setListener(
            new Animator.AnimatorListener() {
              @Override
              public void onAnimationStart(Animator animation) {}

              @Override
              public void onAnimationEnd(Animator animation) {
                mRootView.setVisibility(GONE);
              }

              @Override
              public void onAnimationCancel(Animator animation) {
                mRootView.setVisibility(GONE);
              }

              @Override
              public void onAnimationRepeat(Animator animation) {}
            });
  }

  public void addMenu(List<ISheetItem> items) {
    mItemList = items;
    for (ISheetItem menu : items) {
      switch (menu.getType()) {
        case BUTTON:
          Button btnTag = new Button(mContext);
          btnTag.setLayoutParams(
              new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
          btnTag.setText(menu.getName());
          btnTag.setBackgroundColor(Color.TRANSPARENT);
          btnTag.setOnClickListener(
              new OnClickListener() {
                @Override
                public void onClick(View v) {
                  Callback callback = menu.getCallback();
                  if (callback != null) {
                    callback.onClick(0);
                  }
                  hide();
                }
              });
          mMenuHolder.addView(btnTag);
          break;
        case OPTIONS:
          btnTag = new Button(mContext);
          btnTag.setLayoutParams(
              new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
          btnTag.setText(menu.getName());
          btnTag.setBackgroundColor(Color.TRANSPARENT);
          Callback callback = menu.getCallback();
          btnTag.setOnClickListener(
              new OnClickListener() {
                @Override
                public void onClick(View v) {
                  AlertDialog alertDialog1 = null;
                  AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                  builder.setTitle("Select the choice");
                  final AlertDialog finalAlertDialog = alertDialog1;
                  builder.setSingleChoiceItems(
                      menu.getPossibleValue(),
                      -1,
                      new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int item) {
                          if (callback != null) {
                            callback.onClick(item);
                          }
                          getDialog(menu.getId()).dismiss();
                          hide();
                        }
                      });
                  alertDialog1 = builder.create();
                  alertDialog1.show();
                  setDialog(menu.getId(), alertDialog1);
                }
              });
          mMenuHolder.addView(btnTag);
          break;
      }
    }
    mRootView.setTranslationY(mMenuHolder.getHeight());
  }

  private void setDialog(int id, AlertDialog alertDialog1) {
    mDialogMap.put(id, alertDialog1);
  }

  private DialogInterface getDialog(int id) {
    return mDialogMap.get(id);
  }

  public void setCallback(Callback callback) {
    mCallback = callback;
  }
}
