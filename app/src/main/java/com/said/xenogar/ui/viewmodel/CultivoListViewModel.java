package com.said.xenogar.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.said.xenogar.data.local.entity.Cultivo;
import com.said.xenogar.data.repository.AgroRepository;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class CultivoListViewModel extends ViewModel {
    private final AgroRepository repository;
    private final LiveData<List<Cultivo>> listaCultivos;

    @Inject
    public CultivoListViewModel(AgroRepository repository) {
        this.repository = repository;
        this.listaCultivos = repository.getListaCultivos();
    }

    public LiveData<List<Cultivo>> getListaCultivos() {
        return listaCultivos;
    }

    public void insertCultivo(Cultivo cultivo) {
        repository.insertCultivo(cultivo);
    }

    public void updateCultivo(Cultivo cultivo) {
        repository.updateCultivo(cultivo);
    }

    public void deleteCultivo(Cultivo cultivo) {
        repository.deleteCultivo(cultivo);
    }
}
