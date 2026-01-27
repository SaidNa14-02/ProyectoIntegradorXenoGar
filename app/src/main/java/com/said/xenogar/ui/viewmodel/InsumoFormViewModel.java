package com.said.xenogar.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

import com.said.xenogar.data.local.entity.Insumo;
import com.said.xenogar.data.repository.AgroRepository;
import com.said.xenogar.ui.view.InsumoFormFragment;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class InsumoFormViewModel extends ViewModel {
    public final MutableLiveData<String> nombre = new MutableLiveData<>();
    public final MutableLiveData<String> tipo = new MutableLiveData<>();
    public final MutableLiveData<String> cantidadActual = new MutableLiveData<>();
    public final MutableLiveData<String> unidad = new MutableLiveData<>();
    public final MutableLiveData<String> puntoReorden = new MutableLiveData<>();
    public final MutableLiveData<Boolean> isUnidadSelectionEnabled = new MutableLiveData<>(true); // Inicialmente habilitado

    private final MutableLiveData<String> nombreError = new MutableLiveData<>();
    private final MutableLiveData<String> tipoError = new MutableLiveData<>();
    private final MutableLiveData<String> cantidadActualError = new MutableLiveData<>();
    private final MutableLiveData<String> unidadError = new MutableLiveData<>();
    private final MutableLiveData<String> puntoReordenError = new MutableLiveData<>();
    private final MutableLiveData<Boolean> navegarAtras = new MutableLiveData<>();

    private Long insumoId = null;
    private Insumo insumoExistente;

    private final AgroRepository repository;
    private LiveData<Insumo> insumoLiveData;
    private final Observer<Insumo> insumoObserver;
    private final Observer<String> tipoObserver;

    @Inject
    public InsumoFormViewModel(AgroRepository repository, SavedStateHandle savedStateHandle) {
        this.repository = repository;
        this.insumoObserver = insumo -> {
            if (insumo != null) {
                this.insumoExistente = insumo;
                nombre.setValue(insumo.getNombre());
                tipo.setValue(insumo.getTipo().name());
                cantidadActual.setValue(String.valueOf(insumo.getCantidadActual()));
                unidad.setValue(insumo.getUnidad() != null ? insumo.getUnidad().name() : "");
                puntoReorden.setValue(String.valueOf(insumo.getPuntoReorden()));
            }
        };

        if (savedStateHandle.contains(InsumoFormFragment.ARG_INSUMO_ID)) {
            insumoId = savedStateHandle.get(InsumoFormFragment.ARG_INSUMO_ID);
        }

        // Observar cambios en el tipo para habilitar/deshabilitar la selección de unidad
        tipoObserver = selectedTipo -> {
            boolean enabled = !Insumo.TipoInsumo.HERRAMIENTA.name().equals(selectedTipo);
            isUnidadSelectionEnabled.setValue(enabled);
            if (!enabled) {
                unidad.setValue(null);
            }
        };
        tipo.observeForever(tipoObserver);
    }

    public void cargarInsumo(long id) {
        this.insumoId = id;
        if (insumoLiveData != null) {
            insumoLiveData.removeObserver(insumoObserver);
        }
        insumoLiveData = repository.getInsumo(id);
        insumoLiveData.observeForever(insumoObserver);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (insumoLiveData != null) {
            insumoLiveData.removeObserver(insumoObserver);
        }
        tipo.removeObserver(tipoObserver);
    }

    public LiveData<String> getNombreError() { return nombreError; }
    public LiveData<String> getTipoError() { return tipoError; }
    public LiveData<String> getCantidadActualError() { return cantidadActualError; }
    public LiveData<String> getUnidadError() { return unidadError; }
    public LiveData<String> getPuntoReordenError() { return puntoReordenError; }
    public LiveData<Boolean> getNavegarAtras() { return navegarAtras; }

    private boolean esFormularioValido() {
        boolean nombreValido = validarNombre();
        boolean tipoValido = validarTipo();
        boolean cantidadValida = validarCantidadActual();
        boolean unidadValida = validarUnidad();
        boolean puntoReordenValido = validarPuntoReorden();
        return nombreValido & tipoValido & cantidadValida & unidadValida & puntoReordenValido;
    }

    public void guardarInsumo() {
        if (!esFormularioValido()) {
            return;
        }

        Insumo insumo = (insumoId == null) ? new Insumo() : insumoExistente;

        insumo.setNombre(nombre.getValue().trim());
        insumo.setTipo(Insumo.TipoInsumo.valueOf(tipo.getValue()));
        insumo.setCantidadActual(Double.parseDouble(cantidadActual.getValue().trim()));
        insumo.setPuntoReorden(Double.parseDouble(puntoReorden.getValue().trim()));

        // Solo establecer unidad si el tipo no es HERRAMIENTA
        if (Insumo.TipoInsumo.valueOf(tipo.getValue()) != Insumo.TipoInsumo.HERRAMIENTA) {
            insumo.setUnidad(Insumo.UnidadMedida.valueOf(unidad.getValue()));
        } else {
            insumo.setUnidad(null);
        }

        if (insumoId == null) {
            repository.insertInsumo(insumo);
        } else {
            repository.updateInsumo(insumo);
        }
        navegarAtras.setValue(true);
    }

    private boolean validarNombre() {
        if (nombre.getValue() == null || nombre.getValue().trim().isEmpty()) {
            nombreError.setValue("El nombre del insumo es obligatorio");
            return false;
        }
        nombreError.setValue(null);
        return true;
    }

    private boolean validarTipo() {
        if (tipo.getValue() == null || tipo.getValue().trim().isEmpty()) {
            tipoError.setValue("Debe seleccionar un tipo de insumo");
            return false;
        }
        try {
            Insumo.TipoInsumo.valueOf(tipo.getValue());
            tipoError.setValue(null);
            return true;
        } catch (IllegalArgumentException e) {
            tipoError.setValue("El tipo de insumo no es válido");
            return false;
        }
    }

    private boolean validarCantidadActual() {
        String cantidadStr = cantidadActual.getValue();
        if (cantidadStr == null || cantidadStr.trim().isEmpty()) {
            cantidadActualError.setValue("La cantidad actual es obligatoria");
            return false;
        }
        try {
            double valorCantidad = Double.parseDouble(cantidadStr.trim());
            if (valorCantidad < 0) {
                cantidadActualError.setValue("La cantidad no puede ser negativa");
                return false;
            }
        } catch (NumberFormatException e) {
            cantidadActualError.setValue("Debe ser un número válido");
            return false;
        }
        cantidadActualError.setValue(null);
        return true;
    }

    private boolean validarUnidad() {
        if (tipo.getValue() != null && Insumo.TipoInsumo.valueOf(tipo.getValue()) == Insumo.TipoInsumo.HERRAMIENTA) {
            unidadError.setValue(null);
            return true;
        }

        if (unidad.getValue() == null || unidad.getValue().trim().isEmpty()) {
            unidadError.setValue("Debe seleccionar una unidad de medida");
            return false;
        }
        try {
            Insumo.UnidadMedida.valueOf(unidad.getValue());
            unidadError.setValue(null);
            return true;
        } catch (IllegalArgumentException e) {
            unidadError.setValue("La unidad de medida no es válida");
            return false;
        }
    }

    private boolean validarPuntoReorden() {
        String puntoReordenStr = puntoReorden.getValue();
        if (puntoReordenStr == null || puntoReordenStr.trim().isEmpty()) {
            puntoReordenError.setValue("El punto de reorden es obligatorio");
            return false;
        }
        try {
            double valorPuntoReorden = Double.parseDouble(puntoReordenStr.trim());
            if (valorPuntoReorden < 0) {
                puntoReordenError.setValue("El punto de reorden no puede ser negativo");
                return false;
            }
        } catch (NumberFormatException e) {
            puntoReordenError.setValue("Debe ser un número válido");
            return false;
        }
        puntoReordenError.setValue(null);
        return true;
    }

    public void onNavegacionCompleta() {
        navegarAtras.setValue(false);
    }
}