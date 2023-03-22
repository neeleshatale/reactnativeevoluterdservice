package com.reactnativeevoluterdservice;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

public class EvoluteRDModule extends ReactContextBaseJavaModule implements EvoluteRDServiceEvents {

    public static final String TAG = "EvoluteRDModule";
    private final EvoluteRDServiceManager rdServiceManager;
    public Promise promise;
    private Activity activity;

    public EvoluteRDModule(@Nullable ReactApplicationContext reactContext) {
        super(reactContext);
        rdServiceManager = new EvoluteRDServiceManager.Builder(this).create(getCurrentActivity());
    }

    @NonNull
    @Override
    public String getName() {
        return "EvoluteRDModule";
    }

    @ReactMethod
    public void captureBioMetricDetails(String AadharName, String env, Promise promise) {
        String pidOptions = getPidOptions(AadharName, env);
        Log.d(TAG, "---captureBioMetricDetails invoked");
        this.promise = promise;
        final Activity activity = getCurrentActivity();
        rdServiceManager.captureBioDetailsRdService(pidOptions, activity);
    }

    @NonNull
    private String getPidOptions(String UID_Name, String env) {
        String sConnStat = "";
        String sCustOpts = "";
        String pivalues = "";
        String posh = "";
        String sDemo = "";
        String fCount = "1";

        String Env = env;
        boolean isRetainConection = false;
        boolean isBFDRequest = false;

        if (isRetainConection) {
            sConnStat = "<Param " + "name=\"" + "Connection" + "\" " + "value=\"" + "Y" + "\"/>";
        } else {
            sConnStat = "<Param " + "name=\"" + "Connection" + "\" " + "value=\"" + "N" + "\"/>";
        }

        if (UID_Name.equalsIgnoreCase("")) {
            pivalues = "";
        } else {
            pivalues = "<Pi " + "name=\"" + UID_Name + "\" "
                    + "/>";
        }

        if (isBFDRequest) {
            posh = "posh=\"" + "LEFT_THUMB , LEFT_INDEX , LEFT_MIDDLE, LEFT_RING, LEFT_LITTLE, RIGHT_THUMB, RIGHT_INDEX, RIGHT_MIDDLE, RIGHT_RING, RIGHT_LITTLE" + "\"/>";
        } else {
            posh = "posh=\"" + "UNKNOWN" + "\"/>";
        }

        sCustOpts = "<CustOpts>"
                + sConnStat
                + "</CustOpts>";

        sDemo = "<Demo lang=\"07\">"
                + pivalues
                + "</Demo>";

        String inputxml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
                + "<PidOptions ver =\"1.0\">"
                + "<Opts "
                + "env=\"" + Env + "\" "
                + "fCount=\"" + fCount + "\" "
                + "fType=\"" + ("2") + "\" "
                + "iCount=\"" + "" + "\" "
                + "iType=\"" + "" + "\" "
                + "format=\"" + ("0") + "\" "
                + "pidVer=\"" + "2.0" + "\" "
                + "timeout=\"" + "10000" + "\" "
                + "otp=\"" + "" + "\" "
                + "wadh=\"" + "NA" + "\" "
                + posh
                + sDemo
                + sCustOpts
                + "</PidOptions>";
        return inputxml;


//        "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
//                "<PidOptions ver =\"1.0\">\n" +
//                "    <Opts env=\"PP\" fCount=\"1\" fType=\"0\" iCount=\"\" iType=\"\" format=\"0\" pidVer=\"2.0\" timeout=\"10000\" otp=\"\" wadh=\"\" posh=\"UNKNOWN\"/>\n" +
//                "    <Demo lang=\"07\">\n" +
//                "        <Pi name=\"Neelesh Atale\" gender=\"M\" />\n" +
//                "    </Demo>\n" +
//                "    <CustOpts>\n" +
//                "        <Param name=\"null\" value=\"\"/>\n" +
//                "        <Param name=\"Connection\" value=\"N\"/>\n" +
//                "    </CustOpts>\n" +
//                "</PidOptions>";
    }

    @Override
    public void onRDServiceCaptureResponse(String pidData) {
        promise.resolve(pidData);
    }

    @Override
    public void onRDServiceCaptureFailed(String rdServiceInfo) {
        // Called when fingerprint capture fails
        promise.reject("FINGERPRINT_CAPTURE__FAILED", rdServiceInfo);
    }

    @Override
    public void onDeviceNotConnectedToNetwork(String rdServiceInfo) {
        promise.reject("DEVICE_NOT_CONNECTED_TO_NETWORK", rdServiceInfo);
    }

    @Override
    public void onExceptionOccurred(String rdServiceInfo) {
        promise.reject("EXCEPTION_OCCURRED", rdServiceInfo);
    }
}
