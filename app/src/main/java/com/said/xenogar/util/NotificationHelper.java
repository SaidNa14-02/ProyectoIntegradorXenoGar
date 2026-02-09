package com.said.xenogar.util;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.said.xenogar.R;
import com.said.xenogar.data.local.entity.Actividad;
import com.said.xenogar.data.local.entity.Cultivo;
import com.said.xenogar.ui.notifications.NotificationActionReceiver;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.hilt.android.qualifiers.ApplicationContext;

@Singleton
public class NotificationHelper {

    private static final String CHANNEL_ID = "activity_reminder_channel";
    private static final String CHANNEL_NAME = "Recordatorios de Actividad";
    private static final String CHANNEL_DESCRIPTION = "Notificaciones para actividades próximas o vencidas";

    private final Context context;
    private final NotificationManager notificationManager;

    @Inject
    public NotificationHelper(@ApplicationContext Context context) {
        this.context = context;
        this.notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        createNotificationChannel();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setDescription(CHANNEL_DESCRIPTION);
            channel.enableLights(true);
            channel.enableVibration(true);

            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    public void showActivityReminderNotification(int notificationId, Cultivo cultivo, Actividad actividad) {
        String cultivoName = cultivo.getNombre();
        String actividadName = actividad.getActividad().toString();
        String tipoActividad = actividad.getActividad().toString();
        String fechaActividad = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date(actividad.getFecha()));

        String title = "Recordatorio de Actividad"; // O usa: context.getString(R.string.notification_title_activity_reminder)
        String content = String.format(Locale.getDefault(),
                "Cultivo: %s\nActividad: %s (%s)\nFecha: %s",
                cultivoName, actividadName, tipoActividad, fechaActividad);

        // Intent for "Mark as Completed" action
        Intent completeIntent = new Intent(context, NotificationActionReceiver.class);
        completeIntent.setAction("ACTION_COMPLETE_ACTIVITY");
        completeIntent.putExtra("activity_id", actividad.getId());
        completeIntent.putExtra("notification_id", notificationId);
        PendingIntent completePendingIntent = PendingIntent.getBroadcast(
                context,
                (int) (actividad.getId() * 10 + 1), // Unique request code
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
                (int) (actividad.getId() * 10 + 2), // Unique request code
                inProgressIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.other_icon) // Asegúrate de que este drawable exista
                .setContentTitle(title)
                .setContentText(content)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(content))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .addAction(android.R.drawable.ic_menu_save, "Completar", completePendingIntent) // Usa iconos del sistema temporalmente
                .addAction(android.R.drawable.ic_media_play, "En Progreso", inProgressPendingIntent)
                .setAutoCancel(true);

        if (notificationManager != null) {
            notificationManager.notify(notificationId, builder.build());
        }
    }
}