package com.said.xenogar;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import com.said.xenogar.databinding.ActivityMainBinding;
import com.said.xenogar.ui.view.CultivoListFragment;
import com.said.xenogar.ui.view.HomeFragment;
import com.said.xenogar.workers.ActivityReminderWorker;

import dagger.hilt.android.AndroidEntryPoint;

import java.util.concurrent.TimeUnit;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    private static final String TAG = "MainActivity";
    private static final int NOTIFICATION_PERMISSION_CODE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Request notification permission for Android 13+
        requestNotificationPermission();

        if(savedInstanceState == null){
            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .add(R.id.fragment_container, new HomeFragment())
                    .commit();
        }

        // Enqueue work request
        scheduleReminderCheck();
    }

    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        NOTIFICATION_PERMISSION_CODE);
            } else {
    
            }
        } else {
            Log.d(TAG, "Android version < 13, no need to request notification permission");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == NOTIFICATION_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                scheduleReminderCheck();
            } else {
                Log.e(TAG, "Notification permission denied!");
            }
        }
    }

    private void scheduleReminderCheck() {

        OneTimeWorkRequest reminderCheckRequest = new OneTimeWorkRequest.Builder(ActivityReminderWorker.class)
                .setInitialDelay(3, TimeUnit.SECONDS)
                .addTag("InitialReminderCheck")
                .build();

        WorkManager.getInstance(this).enqueue(reminderCheckRequest);

    }
}