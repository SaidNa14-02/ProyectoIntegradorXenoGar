package com.said.xenogar.data.repository;

import androidx.lifecycle.LiveData;

import com.said.xenogar.data.local.dao.ActividadDao;
import com.said.xenogar.data.local.dao.CultivoDao;
import com.said.xenogar.data.local.entity.Actividad;
import com.said.xenogar.data.local.entity.Cultivo;

import java.util.List;
import java.util.concurrent.ExecutorService;

import javax.inject.Inject;

public class AgroRepository {
    private final CultivoDao cultivoDao;
    private final ActividadDao actividadDao;
    private final ExecutorService executorService;

    @Inject
    public AgroRepository(CultivoDao cultivoDao, ActividadDao actividadDao, ExecutorService executorService) {
        this.cultivoDao = cultivoDao;
        this.actividadDao = actividadDao;
        this.executorService = executorService;
    }

    //Metodos para el manejo de data de cultivos
    public LiveData<List<Cultivo>> getListaCultivos(){
        return cultivoDao.getListaCultivos();
    }

    public LiveData<Cultivo> getCultivo(long id){
        return cultivoDao.getCultivo(id);
    }

    public void insertCultivo(Cultivo cultivo){
        executorService.execute(() -> cultivoDao.insert(cultivo));
    }

    public void updateCultivo(Cultivo cultivo){
        executorService.execute(() -> cultivoDao.update(cultivo));
    }

    public void deleteCultivo(Cultivo cultivo){
        executorService.execute(()-> cultivoDao.delete(cultivo));
    }

    //Metodos para el manejo de data de actividades

    public LiveData<List<Actividad>> getActividadesByCultivoId(long cultivoId){
        return actividadDao.getActividadesByCultivoId(cultivoId);
    }

    public LiveData<List<Actividad>> getAllActividades() {
        return actividadDao.getAllActividades();
    }

    public LiveData<List<Actividad>> getActividadesBetweenDates(String startDate, String endDate) {
        return actividadDao.getActividadesBetweenDates(startDate, endDate);
    }

    public LiveData<Actividad> getActividadById(long actividadId){
        return actividadDao.getActividadById(actividadId);
    }

    public void insertActividad(Actividad actividad){
        executorService.execute(()-> actividadDao.insert(actividad));
    }

    public void updateActividad(Actividad actividad){
        executorService.execute(()-> actividadDao.update(actividad));
    }

    public void deleteActividad(Actividad actividad){
        executorService.execute(() -> actividadDao.delete(actividad));
    }

    public ExecutorService getExecutorService() {
        return executorService;
    }
}
