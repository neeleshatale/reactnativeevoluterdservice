package com.reactnativeevoluterdservice;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.util.List;


public class EvoluteRDServiceManager {

    private static final int CAPTURE = 1;
    private static final String TAG = "EvoluteRDServiceManager";
    private static Activity activity = null;
    private static EvoluteRDServiceEvents mRDEvent;

    private EvoluteRDServiceManager(@NonNull final Builder builder, Activity activity) {
        mRDEvent = builder._rdevent;
        this.activity = activity;
    }

    public static class Builder {
        private EvoluteRDServiceEvents _rdevent = null;

        public Builder(@NonNull final EvoluteRDServiceEvents eventActivity) {
            _rdevent = eventActivity;
        }

        public EvoluteRDServiceManager create(Activity activity) {
            if (_rdevent == null) {
                throw new IllegalStateException("First set your Activity that implements RDServiceEvent by calling setRDServiceEventActivity()");
            }
            return new EvoluteRDServiceManager(this, activity);
        }
    }

    public void captureBioDetailsRdService(String pidOptions, Activity activity) {
        this.activity = activity;
        if (networkConnected()) {
            try {
                connectRDservice(pidOptions);
            } catch (Exception e) {
                e.printStackTrace();
                mRDEvent.onExceptionOccurred(e.toString());
            }
        } else {
            mRDEvent.onDeviceNotConnectedToNetwork("Device is not connected to any network");
        }
    }

    public static void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e(TAG, "onActivityResult requestCode : " + requestCode + " | " + "resultCode : " + resultCode);
        switch (requestCode) {
            case CAPTURE:
                if (resultCode == Activity.RESULT_OK) {
                    String piddata = data.getStringExtra("PID_DATA");
                    Log.e(TAG, "PidData : \n" + piddata);

                    if (piddata != null && piddata.contains("errCode=\"0\"")) {
                        Log.e(TAG, "PidData Success");
                        Toast.makeText(activity, piddata, Toast.LENGTH_SHORT).show();
                        mRDEvent.onRDServiceCaptureResponse(piddata);
                    } else {
                        Log.e(TAG, "PidData Response : \n" + piddata);
                        mRDEvent.onRDServiceCaptureFailed("Empty PidData Response");
                    }
                }
                break;
        }
    }

    private void connectRDservice(String pidOptions) {
        try {
            Intent act = new Intent("in.gov.uidai.rdservice.fp.CAPTURE");
            PackageManager packageManager = activity.getPackageManager();
            List<ResolveInfo> activities = packageManager.queryIntentActivities(act, PackageManager.MATCH_DEFAULT_ONLY);
            Log.e(TAG, "No of activities = " + activities.size());
            act.putExtra("PID_OPTIONS", pidOptions);
            activity.startActivityForResult(act, CAPTURE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean networkConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        assert connectivityManager != null;
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }
}
