package io.github.tobyhs.usbdebugstatus;

import android.content.Intent;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowContentResolver;
import org.robolectric.util.ServiceController;

import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = Build.VERSION_CODES.LOLLIPOP_MR1)
public class AdbStatusServiceTest {
    @Test
    public void registersAdbEnabledObserver() {
        ServiceController<AdbStatusService> controller = Robolectric.buildService(AdbStatusService.class);
        controller.create();
        AdbStatusService service = controller.get();
        Uri uri = Settings.Global.getUriFor(Settings.Global.ADB_ENABLED);
        ShadowContentResolver shadowContentResolver = shadowOf(service.getContentResolver());

        AdbEnabledObserver observer = (AdbEnabledObserver) shadowContentResolver
                .getContentObservers(uri).iterator().next();
        assertThat((AdbStatusService) observer.context, is(service));
        assertThat(observer.widget, is(notNullValue()));

        controller.destroy();
        Collection<ContentObserver> observers = shadowContentResolver.getContentObservers(uri);
        assertThat(observers, is(empty()));
    }

    @Test
    public void onBind_returnsNull() {
        AdbStatusService service = new AdbStatusService();
        assertThat(service.onBind(new Intent()), is(nullValue()));
    }
}
