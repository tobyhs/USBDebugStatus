package io.github.tobyhs.usbdebugstatus;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings.Global;
import android.widget.RemoteViews;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowToast;

import io.github.tobyhs.usbdebugstatus.shadows.ShadowSettingsGlobalWithSecurityException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(AndroidJUnit4.class)
public class USBDebugToggleReceiverTest {
    private final Application application = ApplicationProvider.getApplicationContext();
    private final USBDebugToggleReceiver receiver = new USBDebugToggleReceiver();

    @Test
    public void onReceiveUnchecked() throws Exception {
        Global.putInt(application.getContentResolver(), Global.ADB_ENABLED, 1);
        runReceiver(false);
        assertThat(Global.getInt(application.getContentResolver(), Global.ADB_ENABLED), is(0));
    }

    @Test
    public void onReceiveChecked() throws Exception {
        Global.putInt(application.getContentResolver(), Global.ADB_ENABLED, 0);
        runReceiver(true);
        assertThat(Global.getInt(application.getContentResolver(), Global.ADB_ENABLED), is(1));
    }

    @Test
    @Config(shadows = {ShadowSettingsGlobalWithSecurityException.class})
    public void onReceiveSecurityException() {
        runReceiver(false);
        assertThat(ShadowToast.shownToastCount(), is(1));
        String expectedToastText = application.getResources().
                getString(R.string.write_secure_settings_error);
        assertThat(ShadowToast.getTextOfLatestToast(), is(expectedToastText));
    }

    /**
     * Runs {@link USBDebugToggleReceiver#onReceive(Context, Intent)}
     *
     * @param checked whether the Intent indicates that the toggle switch was checked or not
     */
    private void runReceiver(boolean checked) {
        Intent intent = new Intent(application, USBDebugToggleReceiver.class);
        intent.putExtra(RemoteViews.EXTRA_CHECKED, checked);
        receiver.onReceive(application, intent);
    }
}
