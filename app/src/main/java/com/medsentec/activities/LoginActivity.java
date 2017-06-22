package com.medsentec.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.medsentec.R;
import com.medsentec.particle.ParticleUserFunctions;

import io.particle.android.sdk.cloud.ParticleCloudSDK;

/**
 * TODO: JavaDoc this class
 */
public class LoginActivity extends AppCompatActivity {

    private static final String TAG = LoginActivity.class.getSimpleName();
    private static final int MAX_USERNAME = 200;
    private static final int MAX_PASSWORD = 128;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ParticleCloudSDK.init(this);
        //  TODO: REMOVE AFTER TESTING
        TextView usernameView = (TextView) findViewById(R.id.emailText);
        TextView passwordView = (TextView) findViewById(R.id.passwordText);
        usernameView.setText("dev.justinh@gmail.com");
        passwordView.setText("5sD8q#54@w3PNKxn");
    }

    public void login(View view) {
        TextView usernameView = (TextView) findViewById(R.id.emailText);
        TextView passwordView = (TextView) findViewById(R.id.passwordText);

        String username = usernameView.getText().toString();
        String password = passwordView.getText().toString();

        //  truncate username and password
        if (username.length() > MAX_USERNAME) {
            username = username.substring(0, MAX_USERNAME);
        }
        if (password.length() > MAX_PASSWORD) {
            password = password.substring(0, MAX_PASSWORD);
        }

        ParticleUserFunctions.login(this, username, password);
    }

    public void logout(View view) {
        this.logout();
    }

    private void logout() {
        ParticleUserFunctions.logout(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.logout();
    }
}
