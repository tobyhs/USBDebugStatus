package io.github.tobyhs.usbdebugstatus;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.provider.Settings;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.arrayWithSize;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricTestRunner.class)
public class UpdateWidgetsJobServiceTest {
    private UpdateWidgetsJobService jobService = Robolectric.setupService(UpdateWidgetsJobService.class);

    @Test
    public void schedule() {
        UpdateWidgetsJobService.schedule(jobService);
        assertJobScheduled(jobService);
    }

    @Test
    public void onStartJob() {
        MainAppWidget mainAppWidget = mock(MainAppWidget.class);
        jobService.mainAppWidget = mainAppWidget;

        assertThat(jobService.onStartJob(null), is(false));
        verify(mainAppWidget).updateAll(jobService);
        assertJobScheduled(jobService);
    }

    @Test
    public void onStopJob() {
        assertThat(jobService.onStopJob(null), is(false));
    }

    /**
     * Asserts that an {@link UpdateWidgetsJobService} job was scheduled.
     *
     * @param context the application context
     */
    public static void assertJobScheduled(Context context) {
        JobScheduler jobScheduler = context.getSystemService(JobScheduler.class);
        JobInfo jobInfo = jobScheduler.getPendingJob(UpdateWidgetsJobService.JOB_ID);

        ComponentName component = jobInfo.getService();
        assertThat(component.getPackageName(), is("io.github.tobyhs.usbdebugstatus"));
        assertThat(component.getShortClassName(), is(".UpdateWidgetsJobService"));

        assertThat(jobInfo.getTriggerContentUris(), arrayWithSize(1));
        JobInfo.TriggerContentUri triggerUri = jobInfo.getTriggerContentUris()[0];
        assertThat(triggerUri.getUri(), is(Settings.Global.getUriFor(Settings.Global.ADB_ENABLED)));
        assertThat(jobInfo.getTriggerContentMaxDelay(), is(UpdateWidgetsJobService.JOB_MAX_DELAY_MS));
    }
}
