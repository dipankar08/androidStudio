package in.peerreview.ping.activities.call.addon.map;

import android.Manifest;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import in.co.dipankar.quickandorid.utils.DLog;
import in.co.dipankar.quickandorid.utils.RuntimePermissionUtils;
import in.co.dipankar.quickandorid.views.CustomFontTextView;
import in.co.dipankar.quickandorid.views.StateImageButton;
import in.peerreview.ping.R;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapAddonView extends RelativeLayout {

  public interface Callback {
    void onClose();

    void onCommand();
  }

  private Callback mCallback;
  private Context mContext;
  private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 121;
  @Nullable private GoogleMap mGoogleMap;
  private boolean mLocationPermissionGranted;

  private static final String KEY_CAMERA_POSITION = "camera_position";
  private static final String KEY_LOCATION = "location";

  private final LatLng mDefaultLocation = new LatLng(-33.8523341, 151.2106085);
  private static final int DEFAULT_ZOOM = 15;
  private Location mLastKnownLocation;
  private CameraPosition mCameraPosition;

  private GeoDataClient mGeoDataClient;
  private PlaceDetectionClient mPlaceDetectionClient;
  private FusedLocationProviderClient mFusedLocationProviderClient;

  private View mSearchBar;
  private StateImageButton mSearchBtn;
  private AutoCompleteTextView mSearchText;
  private TextView mRouteInfo;

  private View mShareHolder;
  private Button mShereButton;
  private Button mShereCancelButton;
  private CustomFontTextView mShareText;

  private PlaceAutocompleteAdapter mPlaceAutocompleteAdapter;
  private GoogleApiClient mGoogleApiClient;

  private Button mMeBtn, mPeerBtn, mToPeer, mFromMeBtn, mFromPeer;
  private LatLng mMyLocation, mPeerLocation, mCurSearchLocation;

  private static final LatLngBounds LAT_LNG_BOUNDS =
      new LatLngBounds(new LatLng(-40, -160), new LatLng(71, 136));

  public MapAddonView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    initView(context);
  }

  public MapAddonView(Context context, AttributeSet attrs) {
    super(context, attrs);
    initView(context);
  }

  public MapAddonView(Context context) {
    super(context);
    initView(context);
  }

  private void initView(Context context) {
    if (mContext != null) return;
    mContext = context;
    LayoutInflater mInflater = LayoutInflater.from(context);
    mInflater.inflate(in.peerreview.ping.R.layout.view_addin_map_view, this, true);
    SupportMapFragment mapFragment =
        (SupportMapFragment)
            ((FragmentActivity) mContext).getSupportFragmentManager().findFragmentById(R.id.map);

    // Serach Bar
    mSearchBar = (View) findViewById(R.id.serach_query);
    mSearchBtn = (StateImageButton) findViewById(R.id.pathfinder_btn);
    mSearchText = (AutoCompleteTextView) findViewById(R.id.serach_query);
    mRouteInfo = (TextView) findViewById(R.id.route_info);
    mSearchBtn.setCallBack(
        new StateImageButton.Callback() {
          @Override
          public void click(boolean b) {
            if (mPeerLocation == null) {
              drawPath(mMyLocation, mCurSearchLocation);
            } else {
              if (b) {
                drawPath(mMyLocation, mCurSearchLocation);
              } else {
                drawPath(mPeerLocation, mCurSearchLocation);
              }
            }
          }
        });
    mSearchText.setOnEditorActionListener(
        new TextView.OnEditorActionListener() {
          @Override
          public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (actionId == EditorInfo.IME_ACTION_DONE
                || actionId == EditorInfo.IME_ACTION_SEARCH
                || event.getAction() == KeyEvent.ACTION_DOWN
                || event.getAction() == KeyEvent.KEYCODE_ENTER) {
              geoLocate();
            }
            if (actionId == EditorInfo.IME_ACTION_DONE) {
              hideSoftKeyboard();
              return true;
            }
            return false;
          }
        });

    // share bar
    mShareHolder = (View) findViewById(R.id.shareHolder);
    mShereButton = (Button) findViewById(R.id.share_btn);
    mShereCancelButton = (Button) findViewById(R.id.share_btn_cancel);
    mShareText = (CustomFontTextView) findViewById(R.id.share_text);

    mShereButton.setOnClickListener(
        new OnClickListener() {
          @Override
          public void onClick(View v) {
            shareLocation();
          }
        });
    mShereCancelButton.setOnClickListener(
        new OnClickListener() {
          @Override
          public void onClick(View v) {
            mShareHolder.setVisibility(GONE);
          }
        });

    // Google APIs
    mGeoDataClient = Places.getGeoDataClient(mContext, null);
    mPlaceDetectionClient = Places.getPlaceDetectionClient(mContext, null);
    mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(mContext);
    mGoogleApiClient =
        new GoogleApiClient.Builder(mContext)
            .addApi(Places.GEO_DATA_API)
            .addApi(Places.PLACE_DETECTION_API)
            .enableAutoManage((FragmentActivity) mContext, null)
            .build();
    mPlaceAutocompleteAdapter =
        new PlaceAutocompleteAdapter(mContext, mGeoDataClient, LAT_LNG_BOUNDS, null);
    mSearchText.setAdapter(mPlaceAutocompleteAdapter);
    mSearchText.setOnItemClickListener(
        new AdapterView.OnItemClickListener() {
          @Override
          public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            hideSoftKeyboard();
            final AutocompletePrediction item = mPlaceAutocompleteAdapter.getItem(position);
            PendingResult<PlaceBuffer> placeBufferPendingResult =
                Places.GeoDataApi.getPlaceById(mGoogleApiClient, item.getPlaceId());
            placeBufferPendingResult.setResultCallback(
                new ResultCallback<PlaceBuffer>() {
                  @Override
                  public void onResult(@NonNull PlaceBuffer places) {
                    if (places.getStatus().isSuccess()) {
                      if (places.get(0) != null) {
                        mCurSearchLocation = places.get(0).getLatLng();
                        updateLocation(mCurSearchLocation, places.get(0).getAddress().toString());
                      }
                    }
                    places.release();
                  }
                });
          }
        });

    mMeBtn = findViewById(R.id.me_loc);
    mMeBtn.setOnClickListener(
        new OnClickListener() {
          @Override
          public void onClick(View v) {
            updateLocation(mMyLocation, "me");
          }
        });

    mPeerBtn = findViewById(R.id.peer_loc);
    mPeerBtn.setOnClickListener(
        new OnClickListener() {
          @Override
          public void onClick(View v) {
            updateLocation(mPeerLocation, "me");
          }
        });

    mToPeer = findViewById(R.id.me_to_peer);
    mToPeer.setOnClickListener(
        new OnClickListener() {
          @Override
          public void onClick(View v) {
            drawPath(mMyLocation, mPeerLocation);
          }
        });

    mFromMeBtn = findViewById(R.id.from_me);
    mFromMeBtn.setOnClickListener(
        new OnClickListener() {
          @Override
          public void onClick(View v) {
            drawPath(mMyLocation, mCurSearchLocation);
          }
        });

    mFromPeer = findViewById(R.id.from_peer);
    mFromPeer.setOnClickListener(
        new OnClickListener() {
          @Override
          public void onClick(View v) {
            drawPath(mPeerLocation, mCurSearchLocation);
          }
        });

    mapFragment.getMapAsync(
        new OnMapReadyCallback() {
          @Override
          public void onMapReady(GoogleMap googleMap) {
            mGoogleMap = googleMap;
            mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);
            mGoogleMap.getUiSettings().setZoomControlsEnabled(true);
            intMap();
          }
        });

    RuntimePermissionUtils.getInstance()
        .askPermission(
            new String[] {Manifest.permission.ACCESS_FINE_LOCATION},
            new RuntimePermissionUtils.CallBack() {
              @Override
              public void onSuccess() {
                updateLocationUI();
                getDeviceLocation();
              }

              @Override
              public void onFail() {}
            });
  }

  private void shareLocation() {}

  private void intMap() {
    mGoogleMap.setOnMapClickListener(
        new GoogleMap.OnMapClickListener() {
          @Override
          public void onMapClick(LatLng point) {
            // mGoogleMap.addMarker(new MarkerOptions().position(point));
            // mShareHolder.setVisibility(GONE);
          }
        });
    mGoogleMap.setOnMarkerClickListener(
        new GoogleMap.OnMarkerClickListener() {
          @Override
          public boolean onMarkerClick(Marker marker) {
            showShareButton(marker.getPosition(), marker.getTitle());
            return false;
          }
        });
  }

  private void showShareButton(LatLng position, String title) {
    mShareText.setText("This is " + title + ". Tap here to share this location to peer");
    mShareHolder.setVisibility(VISIBLE);
  }

  private void drawPath(LatLng origin, LatLng dest) {
    if (origin == null || dest == null) {
      return;
    }
    new DirectionFinder(
            origin,
            dest,
            mGoogleMap,
            new IDirectionFinder.Callback() {
              @Override
              public void onDirectionFinderStart() {
                mRouteInfo.setVisibility(GONE);
              }

              @Override
              public void onDirectionFinderSuccess(String s, String d, String dis, String dur) {
                mRouteInfo.setText("Distance:" + dis + ". Duration: " + dur);
                mRouteInfo.setVisibility(VISIBLE);
              }

              @Override
              public void onError(String s) {
                mRouteInfo.setText(s);
                mRouteInfo.setVisibility(VISIBLE);
              }
            })
        .execute();
  }

  private void geoLocate() {
    DLog.e("Geo Locating");
    String serachQuery = mSearchText.getText().toString();
    Geocoder geocoder = new Geocoder(mContext);
    List<Address> list = new ArrayList<>();
    try {
      list = geocoder.getFromLocationName(serachQuery, 1);
    } catch (IOException e) {
      DLog.e("Error:" + e);
    }
    if (list.size() > 0) {
      Address address = list.get(0);
      DLog.e("Address is:" + address.toString());
      updateLocation(
          new LatLng(address.getLatitude(), address.getLongitude()),
          address.getAddressLine(0).toString());
    }
  }

  public void updateLocationUI() {
    if (mGoogleMap == null) {
      return;
    }
    try {
      mGoogleMap.setMyLocationEnabled(true);
      mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);
    } catch (SecurityException e) {
      Log.e("Exception: %s", e.getMessage());
    }
  }

  private void getDeviceLocation() {
    try {
      Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
      locationResult.addOnCompleteListener(
          new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
              if (task.isSuccessful()) {
                Location location = task.getResult();
                if (location == null) {
                  onFailed("Location is null");
                }
                mMyLocation = new LatLng(location.getLatitude(), location.getLongitude());
                updateLocation(mMyLocation, "me");
                mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);
              } else {
                updateLocation(mDefaultLocation, "Unknown");
                mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
              }
            }
          });
    } catch (SecurityException e) {
      Log.e("Exception: %s", e.getMessage());
    }
  }

  private void onFailed(String msg) {}

  public void updateLocation(LatLng location, String title) {
    if (location == null) {
      return;
    }
    switch (title) {
      case "you":
        mGoogleMap.addMarker(
            new MarkerOptions()
                .position(location)
                .title(title)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_marker_red_24)));
        break;
      case "me":
        mGoogleMap.addMarker(
            new MarkerOptions()
                .position(location)
                .title(title)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_marker_geeen_24)));
        break;
      default:
        mGoogleMap.addMarker(
            new MarkerOptions()
                .position(location)
                .title(title)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_marker_orrange_24)));
        break;
    }
    mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, DEFAULT_ZOOM));
    showShareButton(location, title);
  }

  private void hideSoftKeyboard() {
    InputMethodManager imm =
        (InputMethodManager)
            mContext.getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
    imm.hideSoftInputFromWindow(getWindowToken(), 0);
  }
}
