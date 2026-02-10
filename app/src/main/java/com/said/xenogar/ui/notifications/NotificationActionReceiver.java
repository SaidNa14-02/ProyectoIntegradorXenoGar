package com.said.xenogar.ui.notifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import androidx.core.app.NotificationManagerCompat;

import com.said.xenogar.data.local.entity.Actividad;
import com.said.xenogar.data.repository.AgroRepository;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class NotificationActionReceiver extends BroadcastReceiver {

    public static final String ACTION_COMPLETE_ACTIVITY = "ACTION_COMPLETE_ACTIVITY";
    public static final String ACTION_IN_PROGRESS_ACTIVITY = "ACTION_IN_PROGRESS_ACTIVITY";
    public static final String EXTRA_ACTIVITY_ID = "activity_id";
    public static final String EXTRA_NOTIFICATION_ID = "notification_id";

    @Inject
    AgroRepository agroRepository; // Hilt will inject this

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null && intent.getAction() != null) {
            long activityId = intent.getLongExtra(EXTRA_ACTIVITY_ID, -1L);
            int notificationId = intent.getIntExtra(EXTRA_NOTIFICATION_ID, -1);

            if (activityId != -1L) {
                switch (intent.getAction()) {
                    case ACTION_COMPLETE_ACTIVITY:
                        agroRepository.updateActividadStatus(activityId, Actividad.Estado.COMPLETADA);
                        break;
                    case ACTION_IN_PROGRESS_ACTIVITY:
                        agroRepository.updateActividadStatus(activityId, Actividad.Estado.EN_PROGRESO);
                        break;
                }

                // Dismiss the notification
                if (notificationId != -1) {
                    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
                    notificationManager.cancel(notificationId);
                }
            }
        }
    }
}
