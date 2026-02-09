package com.said.xenogar.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

import com.said.xenogar.data.local.entity.Actividad;
import com.said.xenogar.data.repository.AgroRepository;
import com.said.xenogar.ui.view.ActividadesFragment;

import java.util.ArrayList; // Import needed
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class ActividadesListViewModel extends ViewModel {
    private final AgroRepository repository;
    private final LiveData<List<Actividad>> actividades;
    private final Long cultivoId;

    @Inject
    public ActividadesListViewModel(AgroRepository repository, SavedStateHandle savedStateHandle) {
        this.repository = repository;
        this.cultivoId = savedStateHandle.get(ActividadesFragment.ARG_CULTIVO_ID);
        if (cultivoId != null && cultivoId != 0L) {
            this.actividades = repository.getActividadesByCultivoId(cultivoId);
        } else {
            this.actividades = new androidx.lifecycle.MutableLiveData<>(new ArrayList<>());
        }
    }

    public LiveData<List<Actividad>> getActividades() {
        return actividades;
    }

    public void insertActividad(Actividad actividad) {
        repository.insertActividad(actividad);
    }

    public void updateActividad(Actividad actividad) {
        repository.updateActividad(actividad);
    }

    public void deleteActividad(Actividad actividad) {
        repository.deleteActividad(actividad);
    }
}
