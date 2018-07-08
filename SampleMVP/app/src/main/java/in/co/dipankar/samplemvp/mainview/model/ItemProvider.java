package in.co.dipankar.samplemvp.mainview.model;

import android.os.Handler;
import java.util.Arrays;
import java.util.List;

/** Created by dip on 1/24/18. */
public class ItemProvider implements IItemProvider {
  @Override
  public void loadItems(final IItemProvider.OnFinishedListener listener) {
    new Handler()
        .postDelayed(
            new Runnable() {
              @Override
              public void run() {
                listener.onFinished(createArrayList());
              }
            },
            2000);
  }

  private List<String> createArrayList() {
    return Arrays.asList(
        "Item 1", "Item 2", "Item 3", "Item 4", "Item 5", "Item 6", "Item 7", "Item 8", "Item 9",
        "Item 10");
  }
}
