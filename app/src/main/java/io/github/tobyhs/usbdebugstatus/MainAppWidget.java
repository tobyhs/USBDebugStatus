package io.github.tobyhs.usbdebugstatus;

import android.app.PendingIntent;
import android.app.job.JobScheduler;
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
    private static final ComponentName COMPONENT_NAME = new ComponentName(
            "io.github.tobyhs.usbdebugstatus", "io.github.tobyhs.usbdebugstatus.MainAppWidget"
    );

    private static final String SET_BACKGROUND_COLOR = "setBackgroundColor";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.main_app_widget);

        Intent devOptsIntent = new Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, devOptsIntent, 0);
        views.setOnClickPendingIntent(R.id.widgetIcon, pendingIntent);

        boolean adbEnabled = Settings.Global.getInt(
                context.getContentResolver(), Settings.Global.ADB_ENABLED, 0
        ) == 1;
        int color = adbEnabled ? Color.GREEN : Color.RED;
        views.setInt(R.id.statusIndicator, SET_BACKGROUND_COLOR, color);

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
        UpdateWidgetsJobService.schedule(context);
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
        JobScheduler jobScheduler = context.getSystemService(JobScheduler.class);
        jobScheduler.cancel(UpdateWidgetsJobService.JOB_ID);
    }
}
