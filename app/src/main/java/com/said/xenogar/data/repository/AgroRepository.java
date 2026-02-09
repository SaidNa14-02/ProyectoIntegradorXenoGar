package com.said.xenogar.data.repository;

import androidx.lifecycle.LiveData;

import com.said.xenogar.data.local.dao.ActividadDao;
import com.said.xenogar.data.local.dao.CultivoDao;
import com.said.xenogar.data.local.dao.InsumoDao;
import com.said.xenogar.data.local.entity.Actividad;
import com.said.xenogar.data.local.entity.ActividadConInsumo;
import com.said.xenogar.data.local.entity.ActividadInsumo;
import com.said.xenogar.data.local.entity.Cultivo;
import com.said.xenogar.data.local.entity.Insumo;

import java.util.List;
import java.util.concurrent.ExecutorService;

import javax.inject.Inject;

public class AgroRepository {
    private final CultivoDao cultivoDao;
    private final ActividadDao actividadDao;
    private final InsumoDao insumoDao;
    private final ExecutorService executorService;

    @Inject
    public AgroRepository(CultivoDao cultivoDao, ActividadDao actividadDao, InsumoDao insumoDao, ExecutorService executorService) {
        this.cultivoDao = cultivoDao;
        this.actividadDao = actividadDao;
        this.insumoDao = insumoDao;
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

    public LiveData<Actividad> getActividadById(long actividadId){
        return actividadDao.getActividadById(actividadId);
    }

    public LiveData<ActividadConInsumo> getActividadConInsumos(long actividadId) {
        return actividadDao.getActividadConInsumos(actividadId);
    }

    public void insertActividadConInsumos(Actividad actividad, List<ActividadInsumo> insumos){
        executorService.execute(()-> actividadDao.insertActividadConInsumos(actividad, insumos));
    }

    public void updateActividad(Actividad actividad){
        executorService.execute(()-> actividadDao.update(actividad));
    }

    public void deleteActividad(Actividad actividad){
        executorService.execute(() -> actividadDao.delete(actividad));
    }

    public void deleteInsumosActividad(List<ActividadInsumo> insumos){
        executorService.execute(() -> actividadDao.deleteInsumosActividad(insumos));
    }

    // Method to consume insumos when an activity is completed
    public void consumeInsumosForCompletedActividad(long actividadId) {
        executorService.execute(() -> {
            ActividadConInsumo actividadConInsumo = actividadDao.getActividadConInsumosSync(actividadId);

            if (actividadConInsumo != null && actividadConInsumo.insumos != null) {
                for (Insumo insumoConsumido : actividadConInsumo.insumos) { // Iterate over Insumo objects
                    // Fetch the ActividadInsumo to get cantidadUtilizada
                    ActividadInsumo actInsumoData = actividadDao.getActividadInsumoSync(actividadId, insumoConsumido.getId());

                    if (insumoConsumido != null && actInsumoData != null) {
                        double nuevaCantidad = insumoConsumido.getCantidadActual() - actInsumoData.getCantidadUtilizada();
                        insumoConsumido.setCantidadActual(nuevaCantidad);
                        insumoDao.update(insumoConsumido); // Update insumo using DAO
                    }
                }
            }
        });
    }

    //Metodos para el manejo de data de Insumos
    public LiveData<List<Insumo>> getAllInsumos() {
        return insumoDao.getAllInsumos();
    }

    public LiveData<Insumo> getInsumo(long id) {
        return insumoDao.getInsumo(id);
    }

    public void insertInsumo(Insumo insumo) {
        executorService.execute(() -> insumoDao.insert(insumo));
    }

    // NEW: Method to update an Insumo
    public void updateInsumo(Insumo insumo) {
        executorService.execute(() -> insumoDao.update(insumo));
    }

    public void deleteInsumo(Insumo insumo) {
        executorService.execute(() -> insumoDao.delete(insumo));
    }
}
