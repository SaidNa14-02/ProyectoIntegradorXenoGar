package com.said.xenogar.workers;

import android.content.Context;
import androidx.annotation.NonNull;
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
import dagger.hilt.android.workers.HiltWorker;

@HiltWorker
public class ActivityReminderWorker extends Worker {

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
        // Define "close to due date" condition: today or overdue
        LocalDate today = LocalDate.now();

        try {
            List<Actividad> allActividades = repository.getAllActividadesSync();
            List<Cultivo> allCultivosSync = repository.getAllCultivosSync();

            if (allActividades == null || allCultivosSync == null) {
                // If data fetching failed, retry later
                return Result.retry();
            }

            for (Actividad actividad : allActividades) {
                // Convert long fecha to LocalDate for comparison
                // Actividad.getFecha() returns milliseconds, convert to Date first
                LocalDate actividadDate = new Date(actividad.getFecha()).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

                // Check alert conditions:
                // 1. Due today or overdue (actividadDate is today or in the past)
                // 2. Status is PENDIENTE or EN_PROGRESO
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
                        // Show notification
                        // Using a unique ID for each activity reminder notification
                        int notificationId = actividad.getId().intValue();
                        notificationHelper.showActivityReminderNotification(notificationId, associatedCultivo, actividad);
                    }
                }
            }
            return Result.success();
        } catch (Exception e) {
            // Log the error
            e.printStackTrace();
            return Result.failure();
        }
    }
}
