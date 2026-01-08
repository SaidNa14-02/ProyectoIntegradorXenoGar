package com.said.xenogar.data.di;

import android.content.Context;

import androidx.room.Room;

import com.said.xenogar.data.local.AppDatabase;
import com.said.xenogar.data.local.dao.ActividadDao;
import com.said.xenogar.data.local.dao.CultivoDao;
import com.said.xenogar.data.local.dao.InsumoDao;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public class DatabaseModule {
    private static final int NUMBER_OF_THREADS = 4;

    @Provides
    @Singleton
    public AppDatabase provideDatabase(@ApplicationContext Context context){
        return Room.databaseBuilder(context, AppDatabase.class, "xenogar_database")
                .fallbackToDestructiveMigration()
                .build();
    }

    @Provides
    @Singleton
    public ExecutorService provideDatabaseWriteExecutor() {
        return Executors.newFixedThreadPool(NUMBER_OF_THREADS);
    }

    @Provides
    public CultivoDao provideCultivoDao(AppDatabase database){
        return database.cultivoDao();
    }

    @Provides
    public InsumoDao provideInsumoDao(AppDatabase database){
        return database.insumoDao();
    }

    @Provides
    public ActividadDao provideActividadDao(AppDatabase database){
        return database.actividadDao();
    }

}
