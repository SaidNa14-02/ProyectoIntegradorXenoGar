package com.said.xenogar.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.said.xenogar.data.local.entity.Insumo;

import java.util.List;
@Dao
public interface InsumoDao {
    @Insert
    void insert(Insumo insumo);

    @Update
    void update(Insumo insumo);

    @Delete
    void delete(Insumo insumo);

    @Query("SELECT * FROM insumos ORDER BY tipo, nombre")
    LiveData<List<Insumo>> getAllInsumos();

    @Query("SELECT * FROM insumos WHERE id = :insumoId")
    LiveData<Insumo> getInsumo(long insumoId);
    @Query("SELECT * FROM insumos WHERE id = :insumoId")
    Insumo getInsumoSync(long insumoId);
}

