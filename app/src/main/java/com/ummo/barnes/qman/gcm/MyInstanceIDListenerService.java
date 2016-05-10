package com.ummo.barnes.qman.gcm;

/**
 * Created by sihle on 12/3/15.
 */
import android.content.Intent;

import com.ummo.barnes.qman.gcm.RegistrationIntentService;
import com.google.android.gms.iid.InstanceIDListenerService;

/**
 * Created by sihle on 11/28/15.
 */
public class MyInstanceIDListenerService extends InstanceIDListenerService {
    @Override
    public void onTokenRefresh() {
        // Fetch updated Instance ID token and notify of changes
        Intent intent = new Intent(this, RegistrationIntentService.class);
        startService(intent);
    }
}