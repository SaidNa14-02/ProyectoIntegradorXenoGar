package com.said.xenogar.data.local;


import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.said.xenogar.data.local.dao.ActividadDao;
import com.said.xenogar.data.local.dao.CultivoDao;
import com.said.xenogar.data.local.dao.InsumoDao;
import com.said.xenogar.data.local.entity.Actividad;
import com.said.xenogar.data.local.entity.ActividadInsumo;
import com.said.xenogar.data.local.entity.Cultivo;
import com.said.xenogar.data.local.entity.Insumo;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Actividad.class, Insumo.class, ActividadInsumo.class, Cultivo.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract ActividadDao actividadDao();
    public abstract InsumoDao insumoDao();
    public abstract CultivoDao cultivoDao();

    private static volatile AppDatabase INSTANCE;
    private static final int NUMERO_HILOS = 4;

    private static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(NUMERO_HILOS);

    private AppDatabase(){}

    //Singleton para manejar una sola instancia de la base de datos
    public static AppDatabase getAppDatabase(final Context context){
        if( INSTANCE == null) {
            synchronized (AppDatabase.class){
                if (INSTANCE == null){
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "xenogar_database")
                            .build();
                }
            }
        }
        return INSTANCE;
    }

}
