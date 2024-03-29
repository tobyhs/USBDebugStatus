package io.github.tobyhs.usbdebugstatus;

import android.Manifest;
import android.app.Application;
import android.app.job.JobScheduler;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.provider.Settings;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.Switch;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.robolectric.shadows.ShadowAppWidgetManager;
import org.robolectric.shadows.ShadowApplication;

import static org.hamcrest.CoreMatchers.everyItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import static org.robolectric.Shadows.shadowOf;

@RunWith(AndroidJUnit4.class)
public class MainAppWidgetTest {
    private final Application application = ApplicationProvider.getApplicationContext();
    private ShadowAppWidgetManager shadowWidgetManager;

    @Before
    public void setup() {
        AppWidgetManager widgetManager = AppWidgetManager.getInstance(application);
        shadowWidgetManager = shadowOf(widgetManager);
    }

    @Test
    public void statusSwitches() {
        Settings.Global.putInt(application.getContentResolver(), Settings.Global.ADB_ENABLED, 0);
        int[] widgetIds = shadowWidgetManager.createWidgets(
                MainAppWidget.class, R.layout.main_app_widget, 2
        );

        checkStatusSwitches(widgetIds, false);

        Settings.Global.putInt(application.getContentResolver(), Settings.Global.ADB_ENABLED, 1);
        new MainAppWidget().updateAll(application);

        checkStatusSwitches(widgetIds, true);
    }

    /**
     * Checks that the widgets with the given IDs have status switches in the expected state.
     *
     * @param widgetIds IDs of widgets to check
     * @param checked whether the switches are expected to be the checked state
     */
    private void checkStatusSwitches(int[] widgetIds, boolean checked) {
        for (int widgetId : widgetIds) {
            View widgetView = shadowWidgetManager.getViewFor(widgetId);
            Switch statusSwitch = widgetView.findViewById(R.id.status);
            assertThat(statusSwitch.isChecked(), is(checked));
        }
    }

    @Test
    public void clickingIconOpensDeveloperOptions() {
        int widgetId = shadowWidgetManager.createWidget(
                MainAppWidget.class, R.layout.main_app_widget
        );
        View widgetView = shadowWidgetManager.getViewFor(widgetId);
        widgetView.findViewById(R.id.widgetIcon).performClick();

        ShadowApplication shadowApp = shadowOf(application);
        String nextAction = shadowApp.getNextStartedActivity().getAction();
        assertThat(nextAction, is(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS));
    }

    @Test
    public void disabledSwitch() {
        ShadowApplication shadowApp = shadowOf(application);
        shadowApp.denyPermissions(Manifest.permission.WRITE_SECURE_SETTINGS);
        int widgetId = shadowWidgetManager.createWidget(
                MainAppWidget.class, R.layout.main_app_widget
        );
        View widgetView = shadowWidgetManager.getViewFor(widgetId);
        Switch statusSwitch = widgetView.findViewById(R.id.status);
        assertThat(statusSwitch.isEnabled(), is(false));
    }

    @Test
    public void enabledSwitch() {
        Settings.Global.putInt(application.getContentResolver(), Settings.Global.ADB_ENABLED, 0);
        ShadowApplication shadowApp = shadowOf(application);
        shadowApp.grantPermissions(Manifest.permission.WRITE_SECURE_SETTINGS);
        int widgetId = shadowWidgetManager.createWidget(
                MainAppWidget.class, R.layout.main_app_widget
        );
        View widgetView = shadowWidgetManager.getViewFor(widgetId);
        Switch statusSwitch = widgetView.findViewById(R.id.status);
        assertThat(statusSwitch.isEnabled(), is(true));

        statusSwitch.toggle();
        statusSwitch.toggle();

        List<Intent> intents = shadowApp.getBroadcastIntents();
        assertThat(intents.size(), is(2));
        Iterable<String> intentClassNames = intents.stream()
                .map(intent -> intent.getComponent().getClassName())
                .collect(Collectors.toList());
        assertThat(intentClassNames, everyItem(is(USBDebugToggleReceiver.class.getName())));
        assertThat(intents.get(0).getBooleanExtra(RemoteViews.EXTRA_CHECKED, false), is(true));
        assertThat(intents.get(1).getBooleanExtra(RemoteViews.EXTRA_CHECKED, true), is(false));
    }

    @Test
    public void onEnabled() {
        new MainAppWidget().onEnabled(application);
        UpdateWidgetsJobServiceTest.assertJobScheduled(application);
    }

    @Test
    public void onDisabled() {
        UpdateWidgetsJobService.schedule(application);
        new MainAppWidget().onDisabled(application);
        JobScheduler jobScheduler = application.getSystemService(JobScheduler.class);
        assertThat(jobScheduler.getPendingJob(UpdateWidgetsJobService.JOB_ID), is(nullValue()));
    }
}
