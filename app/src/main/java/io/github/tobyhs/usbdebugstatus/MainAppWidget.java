package io.github.tobyhs.usbdebugstatus;

import android.app.PendingIntent;
import android.app.job.JobScheduler;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.widget.RemoteViews;

/**
 * A home screen widget that shows whether USB debugging is enabled.
 */
public class MainAppWidget extends AppWidgetProvider {
    private static final ComponentName COMPONENT_NAME = new ComponentName(
            "io.github.tobyhs.usbdebugstatus", "io.github.tobyhs.usbdebugstatus.MainAppWidget"
    );

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.main_app_widget);

        PendingIntent devOptsPendingIntent = PendingIntent.getActivity(
                context,
                0,
                new Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS),
                PendingIntent.FLAG_IMMUTABLE
        );
        views.setOnClickPendingIntent(R.id.widgetIcon, devOptsPendingIntent);

        boolean adbEnabled = Settings.Global.getInt(
                context.getContentResolver(), Settings.Global.ADB_ENABLED, 0
        ) == 1;
        views.setCompoundButtonChecked(R.id.status, adbEnabled);

        PendingIntent toggleReceiverPendingIntent = PendingIntent.getBroadcast(
                context,
                0,
                new Intent(context, USBDebugToggleReceiver.class),
                PendingIntent.FLAG_MUTABLE
        );
        views.setOnCheckedChangeResponse(
                R.id.status,
                RemoteViews.RemoteResponse.fromPendingIntent(toggleReceiverPendingIntent)
        );

        appWidgetManager.updateAppWidget(appWidgetIds, views);
    }

    /**
     * Updates all widget instances of this provider.
     *
     * @param context context in which this widget is running
     */
    public void updateAll(Context context) {
        AppWidgetManager widgetManager = AppWidgetManager.getInstance(context);
        int[] widgetIds = widgetManager.getAppWidgetIds(COMPONENT_NAME);
        onUpdate(context, widgetManager, widgetIds);
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
