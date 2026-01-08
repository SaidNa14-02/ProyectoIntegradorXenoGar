package com.said.xenogar.data.local;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.said.xenogar.data.local.dao.ActividadDao;
import com.said.xenogar.data.local.dao.CultivoDao;
import com.said.xenogar.data.local.dao.InsumoDao;
import com.said.xenogar.data.local.entity.Actividad;
import com.said.xenogar.data.local.entity.ActividadInsumo;
import com.said.xenogar.data.local.entity.Cultivo;
import com.said.xenogar.data.local.entity.Insumo;


@Database(entities = {Cultivo.class, Actividad.class, Insumo.class, ActividadInsumo.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public AppDatabase() {}

    public abstract CultivoDao cultivoDao();
    public abstract ActividadDao actividadDao();
    public abstract InsumoDao insumoDao();
}
