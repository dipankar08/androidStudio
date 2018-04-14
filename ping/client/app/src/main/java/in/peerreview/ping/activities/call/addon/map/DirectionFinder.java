package in.peerreview.ping.activities.call.addon.map;

import android.graphics.Color;
import android.os.AsyncTask;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import in.peerreview.ping.R;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DirectionFinder implements IDirectionFinder {
  private static final String DIRECTION_URL_API =
      "https://maps.googleapis.com/maps/api/directions/json?";
  private static final String GOOGLE_API_KEY = "AIzaSyDnwLF2-WfK8cVZt9OoDYJ9Y8kspXhEHfI";
  private Callback listener;
  private LatLng origin;
  private LatLng destination;
  private GoogleMap mGoogleMap;

  private List<Marker> originMarkers = new ArrayList<>();
  private List<Marker> destinationMarkers = new ArrayList<>();
  private List<Polyline> polylinePaths = new ArrayList<>();

  public DirectionFinder(
      LatLng origin, LatLng destination, GoogleMap googleMap, Callback listener) {
    if (origin == null || destination == null) {
      listener.onError("you must select some destination");
      return;
    }
    this.listener = listener;
    this.origin = origin;
    this.destination = destination;
    mGoogleMap = googleMap;
  }

  public void execute() {
    if (originMarkers != null) {
      for (Marker marker : originMarkers) {
        marker.remove();
      }
    }
    if (destinationMarkers != null) {
      for (Marker marker : destinationMarkers) {
        marker.remove();
      }
    }
    if (polylinePaths != null) {
      for (Polyline polyline : polylinePaths) {
        polyline.remove();
      }
    }
    listener.onDirectionFinderStart();
    try {
      new DownloadRawData().execute(createUrl());
    } catch (UnsupportedEncodingException e) {
      listener.onError("Some error");
    }
  }

  private String createUrl() throws UnsupportedEncodingException {
    String urlOrigin = URLEncoder.encode(origin.latitude + "," + origin.longitude, "utf-8");
    String urlDestination =
        URLEncoder.encode(destination.latitude + "," + destination.longitude, "utf-8");
    return DIRECTION_URL_API
        + "origin="
        + urlOrigin
        + "&destination="
        + urlDestination
        + "&key="
        + GOOGLE_API_KEY;
  }

  private class DownloadRawData extends AsyncTask<String, Void, String> {
    @Override
    protected String doInBackground(String... params) {
      String link = params[0];
      try {
        URL url = new URL(link);
        InputStream is = url.openConnection().getInputStream();
        StringBuffer buffer = new StringBuffer();
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));

        String line;
        while ((line = reader.readLine()) != null) {
          buffer.append(line + "\n");
        }
        return buffer.toString();

      } catch (MalformedURLException e) {
        e.printStackTrace();
      } catch (IOException e) {
        e.printStackTrace();
      }
      return null;
    }

    @Override
    protected void onPostExecute(String res) {
      try {
        parseJSon(res);
      } catch (JSONException e) {
        e.printStackTrace();
      }
    }
  }

  private void parseJSon(String data) throws JSONException {
    if (data == null) return;

    List<Route> routes = new ArrayList<Route>();
    JSONObject jsonData = new JSONObject(data);
    JSONArray jsonRoutes = jsonData.getJSONArray("routes");
    for (int i = 0; i < jsonRoutes.length(); i++) {
      JSONObject jsonRoute = jsonRoutes.getJSONObject(i);
      Route route = new Route();

      JSONObject overview_polylineJson = jsonRoute.getJSONObject("overview_polyline");
      JSONArray jsonLegs = jsonRoute.getJSONArray("legs");
      JSONObject jsonLeg = jsonLegs.getJSONObject(0);
      JSONObject jsonDistance = jsonLeg.getJSONObject("distance");
      JSONObject jsonDuration = jsonLeg.getJSONObject("duration");
      JSONObject jsonEndLocation = jsonLeg.getJSONObject("end_location");
      JSONObject jsonStartLocation = jsonLeg.getJSONObject("start_location");

      route.distance = new Distance(jsonDistance.getString("text"), jsonDistance.getInt("value"));
      route.duration = new Duration(jsonDuration.getString("text"), jsonDuration.getInt("value"));
      route.endAddress = jsonLeg.getString("end_address");
      route.startAddress = jsonLeg.getString("start_address");
      route.startLocation =
          new LatLng(jsonStartLocation.getDouble("lat"), jsonStartLocation.getDouble("lng"));
      route.endLocation =
          new LatLng(jsonEndLocation.getDouble("lat"), jsonEndLocation.getDouble("lng"));
      route.points = decodePolyLine(overview_polylineJson.getString("points"));

      routes.add(route);
    }

    handleInternal(routes);
  }

  private void handleInternal(List<Route> routes) {
    polylinePaths = new ArrayList<>();
    originMarkers = new ArrayList<>();
    destinationMarkers = new ArrayList<>();
    LatLngBounds.Builder builder = new LatLngBounds.Builder();

    for (Route route : routes) {
      mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(route.startLocation, 16));
      originMarkers.add(
          mGoogleMap.addMarker(
              new MarkerOptions()
                  .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_marker_geeen_24))
                  .title(route.startAddress)
                  .position(route.startLocation)));
      destinationMarkers.add(
          mGoogleMap.addMarker(
              new MarkerOptions()
                  .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_marker_red_24))
                  .title(route.endAddress)
                  .position(route.endLocation)));
      PolylineOptions polylineOptions =
          new PolylineOptions().geodesic(true).color(Color.BLACK).width(10);
      for (int i = 0; i < route.points.size(); i++) {
        polylineOptions.add(route.points.get(i));
        builder.include(route.points.get(i));
      }

      polylinePaths.add(mGoogleMap.addPolyline(polylineOptions));
      mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 17));
    }
    listener.onDirectionFinderSuccess(
        routes.get(0).startAddress,
        routes.get(0).endAddress,
        routes.get(0).distance.text,
        routes.get(0).duration.text);
  }

  private List<LatLng> decodePolyLine(final String poly) {
    int len = poly.length();
    int index = 0;
    List<LatLng> decoded = new ArrayList<LatLng>();
    int lat = 0;
    int lng = 0;

    while (index < len) {
      int b;
      int shift = 0;
      int result = 0;
      do {
        b = poly.charAt(index++) - 63;
        result |= (b & 0x1f) << shift;
        shift += 5;
      } while (b >= 0x20);
      int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
      lat += dlat;

      shift = 0;
      result = 0;
      do {
        b = poly.charAt(index++) - 63;
        result |= (b & 0x1f) << shift;
        shift += 5;
      } while (b >= 0x20);
      int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
      lng += dlng;

      decoded.add(new LatLng(lat / 100000d, lng / 100000d));
    }

    return decoded;
  }
}
