package io.github.tobyhs.usbdebugstatus;

import android.content.Context;
import android.database.ContentObserver;

/**
 * This is an observer for USB debugging that updates the widgets.
 */
class AdbEnabledObserver extends ContentObserver {
    Context context;
    MainAppWidget widget;

    /**
     * @param context context (the object registering this observer)
     * @param widget a {@link MainAppWidget} instance
     */
    AdbEnabledObserver(Context context, MainAppWidget widget) {
        super(null);

        this.context = context;
        this.widget = widget;
    }

    @Override
    public void onChange(boolean selfChange) {
        widget.updateAll(context);
    }
}
