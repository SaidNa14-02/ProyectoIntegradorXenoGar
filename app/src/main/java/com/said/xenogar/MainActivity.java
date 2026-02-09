package com.said.xenogar;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import com.said.xenogar.databinding.ActivityMainBinding;
import com.said.xenogar.ui.view.CultivoListFragment;
import com.said.xenogar.ui.view.HomeFragment;
import com.said.xenogar.workers.ActivityReminderWorker; // Import added

import dagger.hilt.android.AndroidEntryPoint;

import java.util.concurrent.TimeUnit; // Import added

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(savedInstanceState == null){
            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .add(R.id.fragment_container, new HomeFragment())
                    .commit();
        }
        // Enqueue a one-time work request to check for reminders immediately when the app starts
        OneTimeWorkRequest reminderCheckRequest = new OneTimeWorkRequest.Builder(ActivityReminderWorker.class)
                .setInitialDelay(5, TimeUnit.SECONDS) // A small delay to not block app startup
                .addTag("InitialReminderCheck")
                .build();
        WorkManager.getInstance(this).enqueue(reminderCheckRequest);
    }
}
