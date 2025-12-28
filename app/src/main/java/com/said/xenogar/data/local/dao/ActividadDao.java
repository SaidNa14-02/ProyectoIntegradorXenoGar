package com.said.xenogar.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.said.xenogar.data.local.entity.Actividad;
import com.said.xenogar.data.local.entity.ActividadInsumo;

import java.util.List;

@Dao
public interface ActividadDao {

    // --- Operaciones para Actividad ---

    @Update
    void updateActividad(Actividad actividad);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertActividad(Actividad actividad);

    @Query("SELECT * FROM actividades WHERE cultivoId = :cultivoId ORDER BY fecha DESC")
    LiveData<List<Actividad>> getActividadesByCultivoId(long cultivoId);

    @Query("SELECT * FROM actividades WHERE id = :actividadId")
    LiveData<Actividad> getActividadById(long actividadId);

    // --- Operaciones para la relación Actividad-Insumo ---

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertActividadInsumo(ActividadInsumo relacion);

    @Transaction
    default void insertActividadConInsumos(Actividad actividad, List<ActividadInsumo> insumos) {
        long actividadId = insertActividad(actividad);

        if (insumos != null) {
            for (ActividadInsumo relacion : insumos) {
                relacion.setActividadId(actividadId);
                insertActividadInsumo(relacion);
            }
        }
    }
}
