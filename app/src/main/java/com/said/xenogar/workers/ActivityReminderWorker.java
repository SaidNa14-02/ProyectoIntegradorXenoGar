package com.said.xenogar.workers;

import android.content.Context;
import android.util.Log;
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
import java.util.Date;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedInject;

@HiltWorker
public class ActivityReminderWorker extends Worker {

    private static final String TAG = "ActivityReminderWorker";

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

        LocalDate today = LocalDate.now();


        try {

            List<Actividad> allActividades = repository.getAllActividadesSync();
            List<Cultivo> allCultivosSync = repository.getAllCultivosSync();

            if (allActividades == null || allCultivosSync == null) {
                Log.e(TAG, "Failed to retrieve data. Activities: " + (allActividades == null ? "null" : "ok") +
                        ", Cultivos: " + (allCultivosSync == null ? "null" : "ok"));
                return Result.retry();
            }



            int notificationCount = 0;
            for (Actividad actividad : allActividades) {


                LocalDate actividadDate = new Date(actividad.getFecha()).toInstant()
                        .atZone(ZoneId.systemDefault()).toLocalDate();


                boolean isDueOrOverdue = actividadDate.isBefore(today) || actividadDate.isEqual(today);
                boolean isPendingOrInProgress = actividad.getEstado() == Actividad.Estado.PENDIENTE ||
                        actividad.getEstado() == Actividad.Estado.EN_PROGRESO;



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
                        notificationCount++;
                    } else {
                        Log.w(TAG, "No cultivo found for activity: " + actividad.getId() +
                                " (cultivoId: " + actividad.getCultivoId() + ")");
                    }
                }
            }


            return Result.success();
        } catch (Exception e) {
            Log.e(TAG, "Error in Activity Reminder Worker: " + e.getMessage(), e);
            return Result.failure();
        }
    }
}