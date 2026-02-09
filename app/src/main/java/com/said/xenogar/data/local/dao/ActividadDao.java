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
    @Query("SELECT * FROM actividades")
    LiveData<List<Actividad>> getAllActividades();

    @Query("SELECT * FROM actividades WHERE fecha BETWEEN :startDate AND :endDate")
    LiveData<List<Actividad>> getActividadesBetweenDates(String startDate, String endDate);

    @Delete
    void delete(Actividad actividad);
}
