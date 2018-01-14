package io.github.tobyhs.usbdebugstatus;

import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.net.Uri;
import android.provider.Settings;

/**
 * A JobService to update all the widgets.
 */
public class UpdateWidgetsJobService extends JobService {
    static final int JOB_ID = 1;
    static final JobInfo JOB_INFO;

    MainAppWidget mainAppWidget = new MainAppWidget();

    static {
        ComponentName component = ComponentName.createRelative(
                "io.github.tobyhs.usbdebugstatus", ".UpdateWidgetsJobService"
        );
        Uri uri = Settings.Global.getUriFor(Settings.Global.ADB_ENABLED);
        JobInfo.TriggerContentUri triggerUri = new JobInfo.TriggerContentUri(uri, 0);
        JOB_INFO = new JobInfo.Builder(JOB_ID, component).addTriggerContentUri(triggerUri).build();
    }

    /**
     * Schedules this job.
     *
     * @param context the application context
     */
    public static void schedule(Context context) {
        JobScheduler jobScheduler = context.getSystemService(JobScheduler.class);
        jobScheduler.schedule(JOB_INFO);
    }

    @Override
    public boolean onStartJob(JobParameters params) {
        mainAppWidget.updateAll(this);
        schedule(this);
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }
}
