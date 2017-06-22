package com.medsentec.particle;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.medsentec.activities.HomeActivity;

import java.io.IOException;
import java.util.List;

import io.particle.android.sdk.cloud.ParticleCloudException;
import io.particle.android.sdk.cloud.ParticleCloudSDK;
import io.particle.android.sdk.cloud.ParticleDevice;
import io.particle.android.sdk.utils.Async;
import io.particle.android.sdk.utils.Toaster;

/**
 * TODO: JavaDoc this class
 * Created by Justin Ho on 6/6/17.
 */

public class ParticleUserFunctions {

    private static final String TAG = ParticleUserFunctions.class.getSimpleName();

    public static void login(final Activity activity, final String username, final String password) {
        final Async.ApiWork apiWork = new Async.ApiWork() {
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
        final Async.ApiWork apiWork = new Async.ApiWork() {
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

    public static void getDevices(final Activity activity, final ViewGroup viewGroup) {
        final Async.ApiWork apiWork = new Async.ApiWork() {
            List<ParticleDevice> devices;
            /**
             * {@inheritDoc}
             */
            @Override
            public Object callApi(Object o) throws ParticleCloudException, IOException {
                try {
                    devices = ParticleCloudSDK.getCloud().getDevices();
                }
                catch (ParticleCloudException e) {
                    Log.e(TAG, e.getBestMessage());
                    Toaster.s(activity, e.getBestMessage());
                }
                return null;
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public void onSuccess(Object o) {
                for (final ParticleDevice particleDevice : devices) {
                    final Button button = new Button(activity);
                    button.setText(particleDevice.getName());
                    button.setOnClickListener(new DeviceButtonListener(activity, particleDevice.getID()));
                    viewGroup.addView(button);
                }
                viewGroup.setBackgroundColor(Color.DKGRAY);
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public void onFailure(ParticleCloudException exception) {
                Log.e(TAG, exception.getBestMessage());
                Toaster.s(activity, exception.getBestMessage());
            }
        };
        Async.executeAsync(ParticleCloudSDK.getCloud(), apiWork);
    }

    /**
     * TODO: JavaDocs if we keep this
     */
    private static class DeviceButtonListener implements View.OnClickListener {

        private final Activity activity;
        private final String id;

        public DeviceButtonListener(Activity activity, String id) {
            this.activity = activity;
            this.id = id;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void onClick(View v) {

            final Async.ApiWork apiWork = new Async.ApiWork() {
                /**
                 * {@inheritDoc}
                 */
                @Override
                public Object callApi(Object o) throws ParticleCloudException, IOException {
                    try {
                        //  TODO: remove this later, using it for testing for now
                        ParticleDevice particleDevice = ParticleCloudSDK.getCloud().getDevice(DeviceButtonListener.this.id);
                        String message = (String) particleDevice.getVariable("myString");
                        Toaster.l(DeviceButtonListener.this.activity, message);
                    }
                    catch (ParticleCloudException e) {
                        Log.e(TAG, e.getBestMessage());
                    }
                    catch (ParticleDevice.VariableDoesNotExistException e) {
                        Toaster.s(DeviceButtonListener.this.activity, e.getMessage());
                    }
                    return null;
                }

                /**
                 * {@inheritDoc}
                 */
                @Override
                public void onSuccess(Object o) {
                    //  TODO: fill this in later, only testing for now
                }

                /**
                 * {@inheritDoc}
                 */
                @Override
                public void onFailure(ParticleCloudException exception) {
                    Log.e(TAG, exception.getBestMessage());
                    Toaster.s(activity, exception.getBestMessage());
                }
            };
            Async.executeAsync(ParticleCloudSDK.getCloud(), apiWork);
        }
    }

}
