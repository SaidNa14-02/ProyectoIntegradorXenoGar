package com.said.xenogar.workers;

import android.content.Context;
import android.util.Log; // Import added
import androidx.annotation.NonNull;
import androidx.hilt.work.HiltWorker;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.said.xenogar.data.local.entity.Actividad;
import com.said.xenogar.data.local.entity.Cultivo;
import com.said.xenogar.data.repository.AgroRepository;
import com.said.xenogar.util.NotificationHelper;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.concurrent.TimeUnit; // Import for time unit
import java.util.Date; // Explicitly import Date for clarity

import dagger.assisted.Assisted;
import dagger.assisted.AssistedInject;

@HiltWorker
public class ActivityReminderWorker extends Worker {

    private static final String TAG = "ActivityReminderWorker"; // Tag added for logging

    private final AgroRepository repository;
    private final NotificationHelper notificationHelper;

    @AssistedInject
    public ActivityReminderWorker(@Assisted @NonNull Context context,
                                  @Assisted @NonNull WorkerParameters workerParams,
                                  AgroRepository repository,
                                  NotificationHelper notificationHelper) {
        super(context, workerParams);
        this.repository = repository;
        this.notificationHelper = notificationHelper;
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.d(TAG, "Starting Activity Reminder Worker."); // Log start
        LocalDate today = LocalDate.now();

        try {
            List<Actividad> allActividades = repository.getAllActividadesSync();
            List<Cultivo> allCultivosSync = repository.getAllCultivosSync();

            if (allActividades == null || allCultivosSync == null) {
                Log.e(TAG, "Failed to retrieve activities or cultivos. Retrying."); // Log error
                return Result.retry();
            }

            Log.d(TAG, "Retrieved " + allActividades.size() + " activities and " + allCultivosSync.size() + " cultivos."); // Log data count

            for (Actividad actividad : allActividades) {
                LocalDate actividadDate = new Date(actividad.getFecha()).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

                boolean isDueOrOverdue = actividadDate.isBefore(today) || actividadDate.isEqual(today);
                boolean isPendingOrInProgress = actividad.getEstado() == Actividad.Estado.PENDIENTE || actividad.getEstado() == Actividad.Estado.EN_PROGRESO;

                if (isDueOrOverdue && isPendingOrInProgress) {
                    Cultivo associatedCultivo = null;
                    for (Cultivo cultivo : allCultivosSync) {
                        if (cultivo.getId().equals(actividad.getCultivoId())) {
                            associatedCultivo = cultivo;
                            break;
                        }
                    }

                    if (associatedCultivo != null) {
                        int notificationId = actividad.getId().intValue();
                        notificationHelper.showActivityReminderNotification(notificationId, associatedCultivo, actividad);
                        Log.d(TAG, "Notification shown for activity: " + actividad.getId() + " - " + actividad.getActividad()); // Log notification trigger
                    }
                }
            }
            Log.d(TAG, "Activity Reminder Worker finished successfully."); // Log success
            return Result.success();
        } catch (Exception e) {
            Log.e(TAG, "Error in Activity Reminder Worker: " + e.getMessage(), e); // Log exception
            return Result.failure();
        }
    }
}
