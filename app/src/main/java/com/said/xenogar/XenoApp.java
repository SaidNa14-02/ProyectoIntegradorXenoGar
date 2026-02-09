package com.said.xenogar;

import android.app.Application;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import com.said.xenogar.workers.ActivityReminderWorker;
import java.util.concurrent.TimeUnit;
import dagger.hilt.android.HiltAndroidApp;

@HiltAndroidApp
public class XenoApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        schedulePeriodicActivityReminder();
    }

    private void schedulePeriodicActivityReminder() {
        // Run every 4 hours, as requested by the user
        PeriodicWorkRequest activityReminderRequest =
                new PeriodicWorkRequest.Builder(ActivityReminderWorker.class, 4, TimeUnit.HOURS)
                        .setInitialDelay(10, TimeUnit.MINUTES) // Give the app a chance to start up before the first periodic check
                        .addTag("ActivityReminderWork") // Tag for identifying this work
                        .build();

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
                "ActivityReminderWork", // Unique name for this work
                ExistingPeriodicWorkPolicy.KEEP, // If work is already scheduled, keep the existing one
                activityReminderRequest);
    }
}
