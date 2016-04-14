package fbogin.com.fblogin;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    CallbackManager callbackManager;
    private LoginButton loginButton;
    private TextView txtName;
    private TextView txtEmail;
    private TextView txtGender; ImageView customImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        //AppEventsLogger.activateApp(this);
        setContentView(R.layout.activity_main);
        loginButton = (LoginButton) findViewById(R.id.login_button);
        callbackManager = CallbackManager.Factory.create();
        txtName = (TextView) findViewById(R.id.textName);
        txtEmail = (TextView) findViewById(R.id.textEmail);
        txtGender = (TextView) findViewById(R.id.textGender);
        customImage = (ImageView)findViewById(R.id.customPictureFb);
        //loginButton.setReadPermissions(Arrays.asList("public_profile","user_birthday"));
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                //txtName.setText(loginResult.getAccessToken().getUserId());
                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                try {
                                    String userID = object.getString("id");
                                    txtName.setText("Hi, " + object.getString("name"));
                                    txtEmail.setText("email : " + object.getString("age_range"));
                                    txtGender.setText("gender :" + object.getString("gender"));
                                    Picasso.with(MainActivity.this)
                                            .load("https://graph.facebook.com/" + userID+ "/picture?type=large")
                                            .into(customImage);
                                } catch (JSONException ex) {
                                    ex.printStackTrace();
                                }
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,gender,age_range");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                txtName.setText("Login Cancel");
            }

            @Override
            public void onError(FacebookException error) {
                txtName.setText("Login Error" + error);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppEventsLogger.deactivateApp(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppEventsLogger.activateApp(this);
    }
}
