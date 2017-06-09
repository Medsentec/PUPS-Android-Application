package com.medsentec.particle;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import com.medsentec.activities.HomeActivity;

import java.io.IOException;

import io.particle.android.sdk.cloud.ParticleCloudException;
import io.particle.android.sdk.cloud.ParticleCloudSDK;
import io.particle.android.sdk.utils.Async;
import io.particle.android.sdk.utils.Toaster;

/**
 * TODO: JavaDoc this class
 * Created by Justin Ho on 6/6/17.
 */

public class ParticleUserFunctions {

    private static final String TAG = ParticleUserFunctions.class.getSimpleName();

    public static void login(final Activity activity, final String username, final String password) {
        Async.ApiWork apiWork = new Async.ApiWork() {
            /**
             * {@inheritDoc}
             */
            @Override
            public Object callApi(Object o) throws ParticleCloudException, IOException {
                ParticleCloudSDK.getCloud().logIn(username, password);
                return null;
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public void onSuccess(Object o) {
                Toaster.s(activity, "Login Successful!");
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //  TODO: consider changing Log.d to Log.i
                        Log.d(TAG, "Switching activity to " + HomeActivity.class.getSimpleName());
                        Intent intent = new Intent(activity, HomeActivity.class);
                        activity.startActivity(intent);
                    }
                });
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public void onFailure(ParticleCloudException exception) {
                Log.e(TAG, "Unable to login: " + exception.getBestMessage());
                Toaster.s(activity, "Login unsuccessful: " + exception.getBestMessage());
            }
        };
        Async.executeAsync(ParticleCloudSDK.getCloud(), apiWork);
    }

    public static void logout(final Activity activity) {
        Async.ApiWork apiWork = new Async.ApiWork() {
            /**
             * {@inheritDoc}
             */
            @Override
            public Object callApi(Object o) throws ParticleCloudException, IOException {
                ParticleCloudSDK.getCloud().logOut();
                return null;
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public void onSuccess(Object o) {
                Toaster.s(activity, "Logout Successful!");
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public void onFailure(ParticleCloudException exception) {
                Log.e(TAG, "Unable to logout: " + exception.getBestMessage());
                Toaster.s(activity, "Logout unsuccessful");
            }
        };
        Async.executeAsync(ParticleCloudSDK.getCloud(), apiWork);
    }

}
