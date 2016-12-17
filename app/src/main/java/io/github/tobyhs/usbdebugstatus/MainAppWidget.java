package io.github.tobyhs.usbdebugstatus;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.provider.Settings;
import android.widget.RemoteViews;

/**
 * A home screen widget that shows whether USB debugging is enabled.
 */
public class MainAppWidget extends AppWidgetProvider {
    public static ComponentName COMPONENT_NAME = new ComponentName(
            "io.github.tobyhs.usbdebugstatus", "io.github.tobyhs.usbdebugstatus.MainAppWidget"
    );

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        boolean adbEnabled = Settings.Global.getInt(
                context.getContentResolver(), Settings.Global.ADB_ENABLED, 0
        ) == 1;

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.main_app_widget);
        int color = adbEnabled ? Color.GREEN : Color.RED;
        views.setInt(R.id.statusIndicator, "setBackgroundColor", color);

        for (int appWidgetId : appWidgetIds) {
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

    /**
     * Updates all widget instances of this provider.
     *
     * @param context context in which this widget is running
     */
    public void updateAll(Context context) {
        AppWidgetManager widgetManager = AppWidgetManager.getInstance(context);
        int[] widgetIds = widgetManager.getAppWidgetIds(COMPONENT_NAME);
        new MainAppWidget().onUpdate(context, widgetManager, widgetIds);
    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        context.startService(new Intent(context, AdbStatusService.class));
    }

    @Override
    public void onDisabled(Context context) {
        context.stopService(new Intent(context, AdbStatusService.class));
        super.onDisabled(context);
    }
}
