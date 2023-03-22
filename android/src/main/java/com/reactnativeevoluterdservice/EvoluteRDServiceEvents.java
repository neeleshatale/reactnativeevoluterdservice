package com.reactnativeevoluterdservice;

import android.content.Intent;

public interface EvoluteRDServiceEvents {

    void onRDServiceCaptureResponse(String pidData);

    void onRDServiceCaptureFailed(String rdServiceInfo);

    void onDeviceNotConnectedToNetwork(String rdServiceInfo);

    void onExceptionOccurred(String rdServiceInfo);
}
