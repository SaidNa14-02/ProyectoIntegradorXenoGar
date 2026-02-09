package com.said.xenogar.data.local;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.said.xenogar.data.local.dao.ActividadDao;
import com.said.xenogar.data.local.dao.CultivoDao;
import com.said.xenogar.data.local.entity.Actividad;
import com.said.xenogar.data.local.entity.Cultivo;


@Database(entities = {Cultivo.class, Actividad.class}, version = 2, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public AppDatabase() {}

    public abstract CultivoDao cultivoDao();
    public abstract ActividadDao actividadDao();
}
