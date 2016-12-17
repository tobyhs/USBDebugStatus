package io.github.tobyhs.usbdebugstatus;

import android.app.Service;
import android.content.Intent;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.IBinder;
import android.provider.Settings;

/**
 * This service registers an observer to observe whether USB debugging is enabled.
 */
public class AdbStatusService extends Service {
    ContentObserver adbEnabledObserver;

    @Override
    public void onCreate() {
        super.onCreate();

        Uri uri = Settings.Global.getUriFor(Settings.Global.ADB_ENABLED);
        adbEnabledObserver = new AdbEnabledObserver(this, new MainAppWidget());
        getContentResolver().registerContentObserver(uri, false, adbEnabledObserver);
    }

    @Override
    public void onDestroy() {
        getContentResolver().unregisterContentObserver(adbEnabledObserver);
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
