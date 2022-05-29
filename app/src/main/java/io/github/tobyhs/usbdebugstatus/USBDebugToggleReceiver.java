package io.github.tobyhs.usbdebugstatus;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings.Global;
import android.widget.RemoteViews;
import android.widget.Toast;

/**
 * Broadcast receiver to toggle USB Debugging when the widget's switch is toggled
 */
public class USBDebugToggleReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        boolean checked = intent.getBooleanExtra(RemoteViews.EXTRA_CHECKED, false);
        try {
            Global.putInt(context.getContentResolver(), Global.ADB_ENABLED, checked ? 1 : 0);
        } catch (SecurityException e) {
            Toast.makeText(context, R.string.write_secure_settings_error, Toast.LENGTH_LONG).show();
        }
    }
}