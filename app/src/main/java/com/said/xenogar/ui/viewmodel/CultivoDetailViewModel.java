package com.said.xenogar.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

import com.said.xenogar.data.local.entity.Cultivo;
import com.said.xenogar.data.repository.AgroRepository;
import com.said.xenogar.ui.view.CultivoDetailFragment;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class CultivoDetailViewModel extends ViewModel {
    private final AgroRepository repository;
    private final LiveData<Cultivo> cultivo;
    private final SavedStateHandle savedStateHandle;

    @Inject
    public CultivoDetailViewModel(AgroRepository repository, SavedStateHandle savedStateHandle) {
        this.repository = repository;
        this.savedStateHandle = savedStateHandle;
        Long cultivoId = savedStateHandle.get(CultivoDetailFragment.ARG_CUlTIVO_ID);
        if (cultivoId != null && cultivoId != 0L) {
            this.cultivo = repository.getCultivo(cultivoId);
        } else {
            this.cultivo = null;
        }

    }

    public LiveData<Cultivo> getDetalleCultivo() {
        return cultivo;
    }

    public void updateCultivo(Cultivo cultivo) {
        repository.updateCultivo(cultivo);
    }

    public void deleteCultivo(Cultivo cultivo) {
        repository.deleteCultivo(cultivo);
    }

    public void increaseStock() {
        Cultivo cultivoActual = cultivo.getValue();
        if (cultivoActual != null) {
            int existenciasACambiar = cultivoActual.getExistencias() + 1;
            cultivoActual.setExistencias(existenciasACambiar);
            updateCultivo(cultivoActual);
        }
    }

    public void decreaseStock() {
        Cultivo cultivoActual = cultivo.getValue();
        if (cultivoActual != null && cultivoActual.getExistencias() > 0) {
            int existenciasACambiar = cultivoActual.getExistencias() - 1;
            cultivoActual.setExistencias(existenciasACambiar);
            updateCultivo(cultivoActual);
        }
    }

}
