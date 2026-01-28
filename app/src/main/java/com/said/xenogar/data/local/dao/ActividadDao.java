package com.said.xenogar.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.said.xenogar.data.local.entity.Actividad;
import com.said.xenogar.data.local.entity.ActividadConInsumo;
import com.said.xenogar.data.local.entity.ActividadInsumo;

import java.util.List;

@Dao
public interface ActividadDao {

    // --- Operaciones para Actividad ---

    @Update
    void update(Actividad actividad);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Actividad actividad);

    @Query("SELECT * FROM actividades WHERE cultivoId = :cultivoId ORDER BY fecha DESC")
    LiveData<List<Actividad>> getActividadesByCultivoId(long cultivoId);

    @Query("SELECT * FROM actividades WHERE id = :actividadId")
    LiveData<Actividad> getActividadById(long actividadId);

    // --- Operaciones para la relación Actividad-Insumo ---

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertActividadInsumo(ActividadInsumo relacion);

    @Delete
    void delete(Actividad actividad);

    @Delete
    void deleteInsumosActividad(List<ActividadInsumo> relacion);

    @Transaction
    default void insertActividadConInsumos(Actividad actividad, List<ActividadInsumo> insumos) {
        long actividadId = insert(actividad);

        if (insumos != null) {
            for (ActividadInsumo relacion : insumos) {
                relacion.setActividadId(actividadId);
                insertActividadInsumo(relacion);
            }
        }
    }
    @Transaction
    @Query("SELECT * FROM actividades WHERE id = :actividadId")
    LiveData<ActividadConInsumo> getActividadConInsumos(long actividadId);

    @Transaction
    @Query("SELECT * FROM actividades WHERE id = :actividadId")
    ActividadConInsumo getActividadConInsumosSync(long actividadId);

    @Query("SELECT * FROM actividad_insumo WHERE actividadId = :actividadId AND insumoId = :insumoId")
    ActividadInsumo getActividadInsumoSync(long actividadId, long insumoId);
}
