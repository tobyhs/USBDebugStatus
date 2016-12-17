package io.github.tobyhs.usbdebugstatus;

import android.content.Context;

import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class AdbEnabledObserverTest {
    @Test
    public void onChange_updatesAllWidgets() {
        Context context = mock(Context.class);
        MainAppWidget widget = mock(MainAppWidget.class);
        AdbEnabledObserver observer = new AdbEnabledObserver(context, widget);

        observer.onChange(false);

        verify(widget).updateAll(context);
    }
}
