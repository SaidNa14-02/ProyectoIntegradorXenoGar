package com.said.xenogar.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.said.xenogar.data.local.entity.Actividad;
import com.said.xenogar.data.local.entity.Cultivo;
import com.said.xenogar.data.local.entity.CultivoConActividades;

import java.util.List;

@Dao
public interface CultivoDao {
    @Insert
    void insert(Cultivo cultivo);

    @Delete
    void delete(Cultivo cultivo);

    @Update
    void update(Cultivo cultivo);

    @Query("SELECT * FROM cultivos ORDER BY tipo, nombre")
    LiveData<List<Cultivo>> getListaCultivos();

    @Query("SELECT * FROM cultivos WHERE id = :cultivoId")
    LiveData<Cultivo> getCultivo(long cultivoId);

    @Transaction
    @Query("SELECT * FROM cultivos WHERE id = :cultivoId")
    public LiveData<CultivoConActividades> getCultivoConActividades(long cultivoId);

}
