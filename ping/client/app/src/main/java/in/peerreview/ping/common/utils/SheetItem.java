package in.peerreview.ping.common.utils;



/** Created by dip on 4/1/18. */
public class SheetItem implements CustomButtonSheetView.ISheetItem {
  int mId;
  String mName;
  CustomButtonSheetView.Type mType;
  CustomButtonSheetView.Callback mCallback;
  CharSequence[] mPossibleValue;

  public SheetItem(
      int i,
      String s,
      CustomButtonSheetView.Type type,
      CustomButtonSheetView.Callback callback,
      CharSequence[] mPossibleValue) {
    mId = i;
    mName = s;
    mCallback = callback;
    this.mPossibleValue = mPossibleValue;
    mType = type;
  }

  @Override
  public CustomButtonSheetView.Type getType() {
    return mType;
  }

  @Override
  public int getId() {
    return mId;
  }

  @Override
  public String getName() {
    return mName;
  }

  @Override
  public CharSequence[] getPossibleValue() {
    return mPossibleValue;
  }

  @Override
  public CustomButtonSheetView.Callback getCallback() {
    return mCallback;
  }
}
