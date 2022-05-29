package io.github.tobyhs.usbdebugstatus.shadows;

import android.content.ContentResolver;
import android.provider.Settings;

import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;

/**
 * A Robolectric shadow for {@code android.provider.Settings.Global} that throws
 * {@code SecurityException} on write attempts.
 */
@Implements(Settings.Global.class)
public class ShadowSettingsGlobalWithSecurityException {
    @SuppressWarnings("unused")
    @Implementation
    protected static boolean putInt(ContentResolver cr, String name, int value) {
        throw new SecurityException();
    }
}
