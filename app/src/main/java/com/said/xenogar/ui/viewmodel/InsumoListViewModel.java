package com.said.xenogar.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.said.xenogar.data.local.entity.Insumo;
import com.said.xenogar.data.repository.AgroRepository;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class InsumoListViewModel extends ViewModel {
    private final AgroRepository repository;
    private final LiveData<List<Insumo>> allInsumos;

    @Inject
    public InsumoListViewModel(AgroRepository repository) {
        this.repository = repository;
        this.allInsumos = repository.getAllInsumos();
    }

    public LiveData<List<Insumo>> getAllInsumos() {
        return allInsumos;
    }

    public void insertInsumo(Insumo insumo) {
        repository.insertInsumo(insumo);
    }

    public void updateInsumo(Insumo insumo) {
        repository.updateInsumo(insumo);
    }

    public void deleteInsumo(Insumo insumo) {
        repository.deleteInsumo(insumo);
    }
}