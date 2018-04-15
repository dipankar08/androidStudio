package in.peerreview.ping.activities.login;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.gson.Gson;
import in.co.dipankar.quickandorid.utils.SharedPrefsUtil;
import in.peerreview.ping.R;
import in.peerreview.ping.Utils;
import in.peerreview.ping.activities.application.PingApplication;
import in.peerreview.ping.activities.home.HomeActivity;
import in.peerreview.ping.common.webrtc.RtcUser;
import in.peerreview.ping.contracts.IRtcUser;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class LoginActivity extends AppCompatActivity {

  private GoogleApiClient googleApiClient;
  private SignInButton mGoogleSignInButton;
  private LoginButton mfacebbokLoginButton;
  private CallbackManager mFacebookCallbackManager;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Utils.makeFullScreen(this);
    setContentView(R.layout.activity_login);
    IRtcUser user = PingApplication.Get().getMe();
    printKeyHash();
    if (user != null) {
      navigateToHomeScreen(user);
    } else {
      setupAnonymousLogin();
      setupGoogleLogin();
      setupFacebookLogin();
    }
    // test();
  }

  public void test() {
    // Crashlytics.getInstance().crash();
  }

  private void setupAnonymousLogin() {
    Button AnonymousButton = findViewById(R.id.anonymous_btn);
    AnonymousButton.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            IRtcUser user = PingApplication.Get().getUserManager().getAnonymousUser(); //
            navigateToHomeScreen(user);
          }
        });
  }

  private void setupFacebookLogin() {
    mfacebbokLoginButton = (LoginButton) findViewById(R.id.facebook_login_btn);
    mfacebbokLoginButton.setReadPermissions("public_profile");
    mfacebbokLoginButton.setReadPermissions("email");

    mFacebookCallbackManager = CallbackManager.Factory.create();
    LoginManager.getInstance()
        .registerCallback(
            mFacebookCallbackManager,
            new FacebookCallback<LoginResult>() {
              @Override
              public void onSuccess(LoginResult loginResult) {
                GraphRequest request =
                    GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                          @Override
                          public void onCompleted(JSONObject object, GraphResponse response) {
                            Log.v("LoginActivity", response.toString());
                            if (object == null) {
                              onLoginFailed();
                            }
                            try {
                              String email = object.getString("email");
                              String id = object.getString("id");
                              String name = object.getString("name");
                              String profile_pic =
                                  object
                                      .getJSONObject("picture")
                                      .getJSONObject("data")
                                      .getString("url");
                              IRtcUser user = new RtcUser(name, email, profile_pic, profile_pic);
                              saveUser(user);
                              navigateToHomeScreen(user);
                            } catch (JSONException e) {
                              e.printStackTrace();
                              onLoginFailed();
                            }
                          }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,picture.type(large)");
                request.setParameters(parameters);
                request.executeAsync();
              }

              @Override
              public void onCancel() {
                onLoginFailed();
              }

              @Override
              public void onError(FacebookException exception) {
                onLoginFailed();
              }
            });
  }

  private void setupGoogleLogin() {
    GoogleSignInOptions gso =
        new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
    googleApiClient =
        new GoogleApiClient.Builder(this)
            .enableAutoManage(this, mOnConnectionFailedListener)
            .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
            .build();
    mGoogleSignInButton = findViewById(R.id.signInButton);
    TextView textView = (TextView) mGoogleSignInButton.getChildAt(0);
    textView.setText("Continue with Google!");
    mGoogleSignInButton.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
            startActivityForResult(intent, 777);
          }
        });
  }

  private GoogleApiClient.OnConnectionFailedListener mOnConnectionFailedListener =
      new GoogleApiClient.OnConnectionFailedListener() {
        @Override
        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {}
      };

  @Override
  protected void onActivityResult(int requestcode, int resultCode, Intent data) {
    super.onActivityResult(requestcode, resultCode, data);
    if (requestcode == 777) {
      GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
      if (result.isSuccess()) {
        String name = result.getSignInAccount().getDisplayName();
        String id = result.getSignInAccount().getEmail();
        String pic = result.getSignInAccount().getPhotoUrl().toString();
        IRtcUser user = new RtcUser(name, id, pic, pic);
        saveUser(user);
        navigateToHomeScreen(user);
      } else {
        onLoginFailed();
      }
    } else {
      mFacebookCallbackManager.onActivityResult(requestcode, resultCode, data);
      super.onActivityResult(requestcode, resultCode, data);
    }
  }

  private void saveUser(IRtcUser user) {
    PingApplication.Get().setMe(user);
  }

  private void navigateToHomeScreen(IRtcUser user) {
    PingApplication.Get().setMe(user);
    Intent myIntent = new Intent(this, HomeActivity.class);
    myIntent.putExtra("RtcUser", user);
    myIntent.addFlags(
        Intent.FLAG_ACTIVITY_CLEAR_TOP
            | Intent.FLAG_ACTIVITY_CLEAR_TASK
            | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
    this.startActivity(myIntent);
    overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
  }

  private void onLoginFailed() {
    Toast.makeText(this, "Not able to Signin. Try again", Toast.LENGTH_SHORT);
  }
  private void printKeyHash() {
    // Add code to print out the key hash
    try {
      PackageInfo info = getPackageManager().getPackageInfo("in.peerreview.ping", PackageManager.GET_SIGNATURES);
      for (Signature signature : info.signatures) {
        MessageDigest md = MessageDigest.getInstance("SHA");
        md.update(signature.toByteArray());
        Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
      }
    } catch (PackageManager.NameNotFoundException e) {
      Log.e("KeyHash:", e.toString());
    } catch (NoSuchAlgorithmException e) {
      Log.e("KeyHash:", e.toString());
    }
  }
}
