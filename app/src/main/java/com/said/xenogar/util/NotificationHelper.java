package com.said.xenogar.util;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.said.xenogar.R;
import com.said.xenogar.data.local.entity.Actividad; // For Actividad.TipoActividad.toString()
import com.said.xenogar.data.local.entity.Cultivo; // For Cultivo.getNombre()
import com.said.xenogar.ui.notifications.NotificationActionReceiver; // Will create this later

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.inject.Inject;

import dagger.hilt.android.qualifiers.ApplicationContext;

public class NotificationHelper {

    private static final String CHANNEL_ID = "activity_reminder_channel";
    private static final String CHANNEL_NAME = "Recordatorios de Actividad";
    private static final String CHANNEL_DESCRIPTION = "Notificaciones para actividades próximas o vencidas";

    private final Context context;
    private final NotificationManagerCompat notificationManager;

    @Inject
    public NotificationHelper(@ApplicationContext Context context) {
        this.context = context;
        this.notificationManager = NotificationManagerCompat.from(context);
        createNotificationChannel();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_LOW // Low importance for less intrusive alerts
            );
            channel.setDescription(CHANNEL_DESCRIPTION);
            channel.enableLights(false);
            channel.enableVibration(false);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void showActivityReminderNotification(int notificationId, Cultivo cultivo, Actividad actividad) {
        String cultivoName = cultivo.getNombre();
        String actividadName = actividad.getActividad().toString();
        String tipoActividad = actividad.getActividad().toString();
        String fechaActividad = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date(actividad.getFecha()));

        String title = context.getString(R.string.notification_title_activity_reminder); // Will add this string resource
        String content = context.getString(R.string.notification_content_activity_reminder,
                cultivoName, actividadName, tipoActividad, fechaActividad); // Will add this string resource

        // Intent for "Mark as Completed" action
        Intent completeIntent = new Intent(context, NotificationActionReceiver.class);
        completeIntent.setAction("ACTION_COMPLETE_ACTIVITY");
        completeIntent.putExtra("activity_id", actividad.getId());
        completeIntent.putExtra("notification_id", notificationId);
        PendingIntent completePendingIntent = PendingIntent.getBroadcast(
                context,
                (int) (actividad.getId() + 1), // Unique request code
                completeIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // Intent for "Mark as In Progress" action
        Intent inProgressIntent = new Intent(context, NotificationActionReceiver.class);
        inProgressIntent.setAction("ACTION_IN_PROGRESS_ACTIVITY");
        inProgressIntent.putExtra("activity_id", actividad.getId());
        inProgressIntent.putExtra("notification_id", notificationId);
        PendingIntent inProgressPendingIntent = PendingIntent.getBroadcast(
                context,
                (int) (actividad.getId() + 2), // Unique request code
                inProgressIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.other_icon) // Using a generic icon for now
                .setContentTitle(title)
                .setContentText(content)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(content))
                .setPriority(NotificationCompat.PRIORITY_LOW) // Low priority for less intrusion
                .addAction(R.drawable.ic_check_circle_outline, context.getString(R.string.notification_action_complete), completePendingIntent) // Will add this string resource and drawable
                .addAction(R.drawable.ic_play_circle_outline, context.getString(R.string.notification_action_in_progress), inProgressPendingIntent) // Will add this string resource and drawable
                .setAutoCancel(true); // Notification disappears when clicked (or action button clicked)

        notificationManager.notify(notificationId, builder.build());
    }
}
