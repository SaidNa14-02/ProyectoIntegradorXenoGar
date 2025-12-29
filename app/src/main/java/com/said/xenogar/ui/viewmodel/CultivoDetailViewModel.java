package com.said.xenogar.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.said.xenogar.data.local.entity.Cultivo;
import com.said.xenogar.data.repository.AgroRepository;

import javax.inject.Inject;

public class CultivoDetailViewModel extends ViewModel {
    private final AgroRepository repository;
    private final LiveData<Cultivo> cultivo;
    private long idCultivo;

    @Inject
    public CultivoDetailViewModel(AgroRepository repository, long idCultivo) {
        this.repository = repository;
        this.cultivo = repository.getCultivo(idCultivo);
    }

    public LiveData<Cultivo> getDetalleCultivo(){
        return cultivo;
    }

    public void updateCultivo (Cultivo cultivo) {
        repository.updateCultivo(cultivo);
    }

    public void deleteCultivo (Cultivo cultivo){
        repository.deleteCultivo(cultivo);
    }

}
