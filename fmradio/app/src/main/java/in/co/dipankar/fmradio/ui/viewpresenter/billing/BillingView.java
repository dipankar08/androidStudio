package in.co.dipankar.fmradio.ui.viewpresenter.billing;

import static com.facebook.FacebookSdk.getApplicationContext;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.TextView;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.ConsumeResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchaseHistoryResponseListener;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;
import in.co.dipankar.fmradio.R;
import in.co.dipankar.fmradio.ui.base.BaseView;
import in.co.dipankar.fmradio.utils.Constants;
import in.co.dipankar.quickandorid.utils.DLog;
import java.util.ArrayList;
import java.util.List;

public class BillingView extends BaseView {
  private static final String TAG = "InAppBilling";

  private BillingClient mBillingClient;
  private boolean mBillConnected;

  TextView mPrice;
  TextView mDesc;
  private PurchasesUpdatedListener mPurchasesUpdatedListener =
      new PurchasesUpdatedListener() {
        @Override
        public void onPurchasesUpdated(int responseCode, @Nullable List<Purchase> purchases) {
          String msg =
              "onPurchasesUpdated() called with: responseCode = ["
                  + responseCode
                  + "], purchases = ["
                  + purchases
                  + "]";
          Log.d(TAG, msg);
          if (responseCode == BillingClient.BillingResponse.OK && purchases != null) {
            for (Purchase purchase : purchases) {
              handlePurchase(purchase);
            }
          } else if (responseCode == BillingClient.BillingResponse.USER_CANCELED) {
            Log.i(TAG, "onPurchasesUpdated() - user cancelled the purchase flow - skipping");
          } else {
            Log.w(TAG, "onPurchasesUpdated() got unknown resultCode: " + responseCode);
          }
        }

        private void handlePurchase(Purchase purchase) {
          Log.d(TAG, "handlePurchase() called with: purchase = [" + purchase + "]");
          if (purchase == null) {
            return;
          }
          Log.d(TAG, "handlePurchase: " + purchase.toString());
        }
      };

  private ConsumeResponseListener mConsumeResponseListener =
      new ConsumeResponseListener() {
        @Override
        public void onConsumeResponse(
            @BillingClient.BillingResponse int responseCode, String outToken) {
          Log.d(
              TAG,
              "onConsumeResponse() called with: responseCode = ["
                  + responseCode
                  + "], outToken = ["
                  + outToken
                  + "]");
          if (responseCode == BillingClient.BillingResponse.OK) {
            // Handle the success of the consume operation.
            // For example, increase the number of coins inside the user's basket.
          }
        }
      };

  public BillingView(Context context) {
    super(context);
    init();
  }

  public BillingView(Context context, AttributeSet attrs) {
    super(context, attrs);
    init();
  }

  public BillingView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init();
  }

  private void init() {
    LayoutInflater.from(getContext()).inflate(R.layout.view_billing, this);
    mPrice = findViewById(R.id.price);
    mDesc = findViewById(R.id.desc);
  }

  private void initBilling() {
    mBillingClient =
        BillingClient.newBuilder(getApplicationContext())
            .setListener(mPurchasesUpdatedListener)
            .build();
    mBillingClient.startConnection(
        new BillingClientStateListener() {
          @Override
          public void onBillingSetupFinished(
              @BillingClient.BillingResponse int billingResponseCode) {
            Log.d(
                TAG,
                "onBillingSetupFinished() called with: billingResponseCode = ["
                    + billingResponseCode
                    + "]");
            if (billingResponseCode == BillingClient.BillingResponse.OK) {
              // The billing client is ready. You can query purchases here.
              mBillConnected = true;
              mBillingClient.queryPurchases(BillingClient.SkuType.INAPP);
              mBillingClient.queryPurchases(BillingClient.SkuType.SUBS);
            }
          }

          @Override
          public void onBillingServiceDisconnected() {
            Log.d(TAG, "onBillingServiceDisconnected() called");
            mBillConnected = false;
          }
        });
  }

  @Override
  protected void onDetachedFromWindow() {
    super.onDetachedFromWindow();
    if (mBillConnected && mBillingClient != null) {
      mBillingClient.endConnection();
    }
  }

  @Override
  protected void onAttachedToWindow() {
    super.onAttachedToWindow();
    initBilling();
    query();
  }

  private void purchased(String skuId) {
    BillingFlowParams flowParams =
        BillingFlowParams.newBuilder().setSku(skuId).setType(BillingClient.SkuType.INAPP).build();
    int responseCode = mBillingClient.launchBillingFlow((Activity) getContext(), flowParams);
    if (responseCode == BillingClient.BillingResponse.OK) {
      Log.d(TAG, "purchased: launchBillingFlow：ok");
    } else {
      Log.e(TAG, "purchased: launchBillingFlow：failed. code:" + responseCode);
    }
  }

  private void query() {
    List skuList = new ArrayList<>();
    skuList.add(Constants.SKU_ONETIME);
    SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
    params.setSkusList(skuList).setType(BillingClient.SkuType.INAPP);
    mBillingClient.querySkuDetailsAsync(
        params.build(),
        new SkuDetailsResponseListener() {
          @Override
          public void onSkuDetailsResponse(int responseCode, List<SkuDetails> skuDetailsList) {
            if (responseCode == BillingClient.BillingResponse.OK && skuDetailsList != null) {
              for (SkuDetails skuDetails : skuDetailsList) {
                String price = skuDetails.getPrice();
                DLog.d("Price:" + skuDetails.getPrice() + "Desc:" + skuDetails.getDescription());
                mPrice.setText(skuDetails.getPrice());
                mDesc.setText(skuDetails.getDescription());
              }
            }
          }
        });
  }

  private void hasPurchaged() {
    mBillingClient.queryPurchaseHistoryAsync(
        Constants.SKU_ONETIME,
        new PurchaseHistoryResponseListener() {
          @Override
          public void onPurchaseHistoryResponse(int responseCode, List<Purchase> purchasesList) {
            if (responseCode == BillingClient.BillingResponse.OK && purchasesList != null) {
              for (Purchase purchase : purchasesList) {
                if (purchase.getSku().equals(Constants.SKU_ONETIME)) {}
              }
            }
          }
        });
  }
}
