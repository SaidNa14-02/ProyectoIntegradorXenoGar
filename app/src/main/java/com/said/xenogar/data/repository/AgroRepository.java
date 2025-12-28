package com.said.xenogar.data.repository;

import androidx.lifecycle.LiveData;

import com.said.xenogar.data.local.AppDatabase;
import com.said.xenogar.data.local.dao.ActividadDao;
import com.said.xenogar.data.local.dao.CultivoDao;
import com.said.xenogar.data.local.dao.InsumoDao;
import com.said.xenogar.data.local.entity.Actividad;
import com.said.xenogar.data.local.entity.ActividadInsumo;
import com.said.xenogar.data.local.entity.Cultivo;
import com.said.xenogar.data.local.entity.Insumo;

import java.util.List;

import javax.inject.Inject;

public class AgroRepository {
    private final CultivoDao cultivoDao;
    private final ActividadDao actividadDao;
    private final InsumoDao insumoDao;

    @Inject
    public AgroRepository(CultivoDao cultivoDao, ActividadDao actividadDao, InsumoDao insumoDao) {
        this.cultivoDao = cultivoDao;
        this.actividadDao = actividadDao;
        this.insumoDao = insumoDao;
    }

    //Metodos para el manejo de data de cultivos
    public LiveData<List<Cultivo>> getListaCultivos(){
        return cultivoDao.getListaCultivos();
    }

    public LiveData<Cultivo> getCultivo(long id){
        return cultivoDao.getCultivo(id);
    }

    public void insertCultivo(Cultivo cultivo){
        AppDatabase.databaseWriteExecutor.execute(() -> cultivoDao.insert(cultivo));
    }

    public void updateCultivo(Cultivo cultivo){
        AppDatabase.databaseWriteExecutor.execute(() -> cultivoDao.update(cultivo));
    }

    public void deleteCultivo(Cultivo cultivo){
        AppDatabase.databaseWriteExecutor.execute(()-> cultivoDao.delete(cultivo));
    }

    //Metodos para el manejo de data de actividades

    public LiveData<List<Actividad>> getActividadesByCultivoId(long cultivoId){
        return actividadDao.getActividadesByCultivoId(cultivoId);
    }

    public LiveData<Actividad> getActividadById(long actividadId){
        return actividadDao.getActividadById(actividadId);
    }

    public void insertActividadConInsumos(Actividad actividad, List<ActividadInsumo> insumos){
        AppDatabase.databaseWriteExecutor.execute(()-> actividadDao.insertActividadConInsumos(actividad, insumos));
    }

    public void updateActividad(Actividad actividad){
        AppDatabase.databaseWriteExecutor.execute(()-> actividadDao.update(actividad));
    }

    public void deleteActividad(Actividad actividad){
        AppDatabase.databaseWriteExecutor.execute(() -> actividadDao.delete(actividad));
    }

    public void deleteInsumosActividad(List<ActividadInsumo> insumos){
        AppDatabase.databaseWriteExecutor.execute(() -> actividadDao.deleteInsumosActividad(insumos));
    }

    //Metodos para el manejo de data de Insumos
    public LiveData<List<Insumo>> getAllInsumos() {
        return insumoDao.getAllInsumos();
    }

    public LiveData<Insumo> getInsumo(long id) {
        return insumoDao.getInsumo(id);
    }

    public void insertInsumo(Insumo insumo) {
        AppDatabase.databaseWriteExecutor.execute(() -> insumoDao.insert(insumo));
    }

    public void updateInsumo(Insumo insumo) {
        AppDatabase.databaseWriteExecutor.execute(() -> insumoDao.update(insumo));
    }

    public void deleteInsumo(Insumo insumo) {
        AppDatabase.databaseWriteExecutor.execute(() -> insumoDao.delete(insumo));
    }
}
