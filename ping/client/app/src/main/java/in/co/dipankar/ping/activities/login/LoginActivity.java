package in.co.dipankar.ping.activities.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
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

import org.json.JSONException;
import org.json.JSONObject;

import in.co.dipankar.ping.R;
import in.co.dipankar.ping.Utils;
import in.co.dipankar.ping.activities.application.PingApplication;
import in.co.dipankar.ping.activities.callscreen.CallActivity;
import in.co.dipankar.ping.common.webrtc.RtcUser;
import in.co.dipankar.ping.contracts.IRtcUser;
import in.co.dipankar.quickandorid.utils.SharedPrefsUtil;

/**
 * Created by dip on 3/16/18.
 */

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

        IRtcUser user = getUser();
        if(user != null){
            navigateToCallScreen(user);
        } else{
            setupGoogleLogin();
            setupFacebookLogin();
        }
    }

    private void setupFacebookLogin() {
        mfacebbokLoginButton = (LoginButton) findViewById(R.id.facebook_login_btn);
        mfacebbokLoginButton.setReadPermissions("public_profile");
        mfacebbokLoginButton.setReadPermissions("email");

        mFacebookCallbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(mFacebookCallbackManager,
        new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                Log.v("LoginActivity", response.toString());
                                if(object == null){
                                    onLoginFailed();
                                }
                                try {
                                    String email = object.getString("email");
                                    String id = object.getString("id");
                                    String name = object.getString("name");
                                    String profile_pic = object.getJSONObject("picture").getJSONObject("data").getString("url");
                                    IRtcUser  user = new RtcUser(name, email, profile_pic, profile_pic);
                                    saveUser(user);
                                    navigateToCallScreen(user);
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
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, mOnConnectionFailedListener)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        mGoogleSignInButton = findViewById(R.id.signInButton);
        TextView textView = (TextView) mGoogleSignInButton.getChildAt(0);
        textView.setText("Continue with Google!");
        mGoogleSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
                startActivityForResult(intent, 777);
            }
        });
    }

    private GoogleApiClient.OnConnectionFailedListener mOnConnectionFailedListener = new GoogleApiClient.OnConnectionFailedListener() {
        @Override
        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        }
    };
    @Override
    protected  void onActivityResult(int requestcode, int resultCode, Intent data){
        super.onActivityResult(requestcode,resultCode,data);
        if(requestcode == 777){
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if(result.isSuccess()){
                String name = result.getSignInAccount().getDisplayName();
                String id = result.getSignInAccount().getEmail();
                String pic = result.getSignInAccount().getPhotoUrl().toString();
                IRtcUser  user = new RtcUser(name, id, pic, pic);
                saveUser(user);
                navigateToCallScreen(user);
            } else{
                onLoginFailed();
            }
        } else{
            mFacebookCallbackManager.onActivityResult(requestcode, resultCode, data);
            super.onActivityResult(requestcode, resultCode, data);
        }
    }

    private void saveUser(IRtcUser user) {
        Gson gson = new Gson();
        String json = gson.toJson(user);
        SharedPrefsUtil.getInstance().setString("saved_user",json);
    }

    private IRtcUser getUser() {
        Gson gson = new Gson();
        String json = SharedPrefsUtil.getInstance().getString("saved_user", null);
        if(json == null){
            return null;
        }
        RtcUser obj = gson.fromJson(json, RtcUser.class);
        return obj;
    }
    private void navigateToCallScreen(IRtcUser user) {
        Intent myIntent = new Intent(this, CallActivity.class);
        myIntent.putExtra("RtcUser", user);
        myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|
                Intent.FLAG_ACTIVITY_CLEAR_TASK|
                Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        this.startActivity(myIntent);
        overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
    }

    private void onLoginFailed(){
        Toast.makeText(this, "Not able to Signin. Try again", Toast.LENGTH_SHORT);
    }
}
