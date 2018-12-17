package io.github.tobyhs.usbdebugstatus;

import android.app.Application;
import android.app.job.JobScheduler;
import android.appwidget.AppWidgetManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.provider.Settings;
import android.view.View;

import androidx.test.core.app.ApplicationProvider;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadows.ShadowAppWidgetManager;
import org.robolectric.shadows.ShadowApplication;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricTestRunner.class)
public class MainAppWidgetTest {
    private Application application = ApplicationProvider.getApplicationContext();
    private ShadowAppWidgetManager shadowWidgetManager;

    @Before
    public void setup() {
        AppWidgetManager widgetManager = AppWidgetManager.getInstance(application);
        shadowWidgetManager = shadowOf(widgetManager);
    }

    @Test
    public void statusIndicators() {
        Settings.Global.putInt(application.getContentResolver(), Settings.Global.ADB_ENABLED, 0);
        int[] widgetIds = shadowWidgetManager.createWidgets(
                MainAppWidget.class, R.layout.main_app_widget, 2
        );

        checkColorOfWidgets(widgetIds, Color.RED);

        Settings.Global.putInt(application.getContentResolver(), Settings.Global.ADB_ENABLED, 1);
        new MainAppWidget().updateAll(application);

        checkColorOfWidgets(widgetIds, Color.GREEN);
    }

    /**
     * Checks that the widgets with the given IDs have status indicators of the right color.
     *
     * @param widgetIds IDs of widgets to check
     * @param color expected color of the status indicators
     */
    private void checkColorOfWidgets(int[] widgetIds, int color) {
        for (int widgetId : widgetIds) {
            View widgetView = shadowWidgetManager.getViewFor(widgetId);
            View statusIndicator = widgetView.findViewById(R.id.statusIndicator);
            ColorDrawable background = (ColorDrawable) statusIndicator.getBackground();
            assertThat(background.getColor(), is(color));
        }
    }

    @Test
    public void clickOpensDeveloperOptions() {
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
