package com.said.xenogar.data.di;

import android.content.Context;

import com.said.xenogar.data.local.AppDatabase;
import com.said.xenogar.data.local.dao.ActividadDao;
import com.said.xenogar.data.local.dao.CultivoDao;
import com.said.xenogar.data.local.dao.InsumoDao;
import com.said.xenogar.data.local.entity.Cultivo;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public class DatabaseModule {
    @Provides
    @Singleton
    public AppDatabase provideDatabase(@ApplicationContext Context context){
        return AppDatabase.getAppDatabase(context);
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
