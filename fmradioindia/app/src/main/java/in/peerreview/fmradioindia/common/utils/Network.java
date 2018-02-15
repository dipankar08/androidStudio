package in.peerreview.fmradioindia.common.utils;

import android.content.Context;
import android.util.Log;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import org.json.JSONException;
import org.json.JSONObject;

public class Network implements INetwork {

  // interface
  public interface INetworkCallback {
    void onSuccess(JSONObject jsonObject);

    void onError(String msg);
  }

  public enum CacheControl {
    GET_CACHE_ELSE_LIVE,
    GET_LIVE_ELSE_CACHE,
    GET_LIVE_ONLY,
    GET_CACHE_ONLY,
  }

  // public function
  public Network(Context cx) {
    mContext = cx;
    init();
  }

  @Override
  public void retrive(
      final String url, CacheControl cacheControl, final INetworkCallback networkCallback) {
    retriveInternal(url, cacheControl, networkCallback);
  }

  @Override
  public void send(final String url, final INetworkCallback networkCallback) {}

  // private

  private void init() {
    if (m_Httpclient == null) {
      m_Httpclient = new OkHttpClient();
    }
  }

  private void retriveInternal(
      final String url, CacheControl cacheControl, final INetworkCallback networkCallback) {
    final String key = url.hashCode() + "";
    Request request = new Request.Builder().url(url).build();

    switch (cacheControl) {
      case GET_CACHE_ONLY:
        JSONObject Jobject = readFromCache(key);
        if (Jobject == null) {
          networkCallback.onError("Cache Not found");
        } else {
          networkCallback.onSuccess(Jobject);
        }
        return;
      case GET_LIVE_ONLY:
        Log.d(TAG, "Trying fetch from network....");
        m_Httpclient
            .newCall(request)
            .enqueue(
                new Callback() {
                  @Override
                  public void onFailure(Request request, IOException e) {
                    networkCallback.onError("Internal error:" + e.getMessage());
                  }

                  @Override
                  public void onResponse(Response response) throws IOException {
                    try {
                      String jsonData = response.body().string();
                      JSONObject Jobject = new JSONObject(jsonData);
                      networkCallback.onSuccess(Jobject);
                      storeInCache(key, Jobject);
                    } catch (Exception e) {
                      e.printStackTrace();
                      networkCallback.onError(
                          "Internal error happened while parsing the json object");
                    }
                  }
                });
        return;
      case GET_CACHE_ELSE_LIVE:
        Jobject = readFromCache(key);
        if (Jobject != null) {
          networkCallback.onSuccess(Jobject);
        } else {
          retriveInternal(url, CacheControl.GET_LIVE_ONLY, networkCallback);
        }
        return;
      case GET_LIVE_ELSE_CACHE:
        Log.d(TAG, "Trying fetch from network....");
        m_Httpclient
            .newCall(request)
            .enqueue(
                new Callback() {
                  @Override
                  public void onFailure(Request request, IOException e) {
                    retriveInternal(url, CacheControl.GET_CACHE_ONLY, networkCallback);
                  }

                  @Override
                  public void onResponse(Response response) throws IOException {
                    try {
                      String jsonData = response.body().string();
                      JSONObject Jobject = new JSONObject(jsonData);
                      networkCallback.onSuccess(Jobject);
                      storeInCache(key, Jobject);
                    } catch (Exception e) {
                      e.printStackTrace();
                      retriveInternal(url, CacheControl.GET_CACHE_ONLY, networkCallback);
                    }
                  }
                });
        return;
    }
  }

  private void storeInCache(String filename, JSONObject data) {
    File cDir = mContext.getCacheDir();
    File tempFile = new File(cDir.getPath() + "/" + filename);
    ;
    FileWriter writer = null;
    try {
      writer = new FileWriter(tempFile, false);
      writer.write(data.toString());
      writer.close();
      Log.d(TAG, "Write successfully to: " + filename);
    } catch (IOException e) {
      e.printStackTrace();
      Log.d(TAG, "Write error");
    }
  }

  private JSONObject readFromCache(String filename) {
    File cDir = mContext.getCacheDir();
    File tempFile = new File(cDir.getPath() + "/" + filename);
    String strLine = "";
    StringBuilder text = new StringBuilder();

    /** Reading contents of the temporary file, if already exists */
    try {
      FileReader fReader = new FileReader(tempFile);
      BufferedReader bReader = new BufferedReader(fReader);

      /** Reading the contents of the file , line by line */
      while ((strLine = bReader.readLine()) != null) {
        text.append(strLine + "\n");
      }
      JSONObject x = new JSONObject(text.toString());
      Log.d(TAG, "Read succedssfuly from " + filename);
      return x;
    } catch (FileNotFoundException e) {
      e.printStackTrace();
      Log.d(TAG, "File not found..");
    } catch (IOException e) {
      e.printStackTrace();
      Log.d(TAG, "Read error occures");
    } catch (JSONException e) {
      e.printStackTrace();
      Log.d(TAG, "Not able to read data as Json is not valid");
    }
    return null;
  }

  private static final String TAG = "Network";
  private static OkHttpClient m_Httpclient;
  private boolean mDebug = false;
  private Context mContext;
}
