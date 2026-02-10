package com.said.xenogar.workers;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.work.ListenableWorker;
import androidx.work.WorkerFactory;
import androidx.work.WorkerParameters;

import java.util.Map;
import javax.inject.Inject;
import javax.inject.Provider;

public class HiltWorkerFactory extends WorkerFactory {

    private final Map<Class<? extends ListenableWorker>, Provider<ChildWorkerFactory>> workerFactories;

    @Inject
    public HiltWorkerFactory(Map<Class<? extends ListenableWorker>, Provider<ChildWorkerFactory>> workerFactories) {
        this.workerFactories = workerFactories;
    }

    @Nullable
    @Override
    public ListenableWorker createWorker(
            @NonNull Context appContext,
            @NonNull String workerClassName,
            @NonNull WorkerParameters workerParameters) {

        try {
            Class<?> workerClass = Class.forName(workerClassName);

            for (Map.Entry<Class<? extends ListenableWorker>, Provider<ChildWorkerFactory>> entry : workerFactories.entrySet()) {
                if (entry.getKey().isAssignableFrom(workerClass)) {
                    return entry.getValue().get().create(appContext, workerParameters);
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    public interface ChildWorkerFactory {
        ListenableWorker create(@NonNull Context appContext, @NonNull WorkerParameters workerParameters);
    }
}