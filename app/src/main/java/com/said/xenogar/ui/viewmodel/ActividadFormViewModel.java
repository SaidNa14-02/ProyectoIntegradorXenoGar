package com.said.xenogar.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

import com.said.xenogar.data.local.entity.Actividad;
import com.said.xenogar.data.local.entity.ActividadInsumo;
import com.said.xenogar.data.local.entity.Insumo;
import com.said.xenogar.data.repository.AgroRepository;
import com.said.xenogar.ui.view.ActividadFormFragment;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class ActividadFormViewModel extends ViewModel {

    // LiveData for form fields
    public final MutableLiveData<String> tipoActividad = new MutableLiveData<>();
    public final MutableLiveData<String> prioridad = new MutableLiveData<>();
    public final MutableLiveData<String> fecha = new MutableLiveData<>();
    public final MutableLiveData<String> descripcion = new MutableLiveData<>();
    public final MutableLiveData<String> estado = new MutableLiveData<>();

    // Error LiveData
    private final MutableLiveData<String> tipoActividadError = new MutableLiveData<>();
    private final MutableLiveData<String> prioridadError = new MutableLiveData<>();
    private final MutableLiveData<String> fechaError = new MutableLiveData<>();
    private final MutableLiveData<String> descripcionError = new MutableLiveData<>();
    private final MutableLiveData<String> estadoError = new MutableLiveData<>();
    private final MutableLiveData<Boolean> navegarAtras = new MutableLiveData<>();


    private final AgroRepository repository;
    private final SavedStateHandle savedStateHandle;

    private Long actividadId = null;
    private Long cultivoId = null;
    private Actividad actividadExistente;

    private LiveData<Actividad> actividadLiveData;
    private final Observer<Actividad> actividadObserver;

    public LiveData<List<Insumo>> allInsumos;

    // NEW: LiveData to manage insumos associated with the current activity
    public final MutableLiveData<List<ActividadInsumo>> selectedInsumos = new MutableLiveData<>(new ArrayList<>());


    @Inject
    public ActividadFormViewModel(AgroRepository repository, SavedStateHandle savedStateHandle) {
        this.repository = repository;
        this.savedStateHandle = savedStateHandle;

        this.actividadObserver = actividad -> {
            if (actividad != null) {
                this.actividadExistente = actividad;
                actividadId = actividad.getId();
                cultivoId = actividad.getCultivoId();

                tipoActividad.setValue(actividad.getActividad().name());
                prioridad.setValue(actividad.getPrioridad().name());
                descripcion.setValue(actividad.getDescripcion());
                estado.setValue(actividad.getEstado().name());

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                if (actividad.getFecha() == 0L) { // Assuming 0L means no date set
                    fecha.setValue(LocalDate.now().format(formatter));
                } else {
                    fecha.setValue(Instant.ofEpochMilli(actividad.getFecha()).atZone(ZoneId.systemDefault()).toLocalDate().format(formatter));
                }

            }
        };

        // Initialize allInsumos
        this.allInsumos = repository.getAllInsumos(); // NEW: Initialize allInsumos

        Long initialCultivoId = savedStateHandle.get(ActividadFormFragment.ARG_CULTIVO_ID_FORM);
        if (initialCultivoId != null && initialCultivoId != 0L) {
            this.cultivoId = initialCultivoId;
        }
    }

    // NEW: Methods to manage selected insumos
    public void addSelectedInsumo(ActividadInsumo insumo) {
        List<ActividadInsumo> currentList = selectedInsumos.getValue();
        if (currentList == null) {
            currentList = new ArrayList<>();
        }
        currentList.add(insumo);
        selectedInsumos.setValue(currentList);
    }

    public void updateSelectedInsumo(ActividadInsumo insumo) {
        List<ActividadInsumo> currentList = selectedInsumos.getValue();
        if (currentList != null) {
            int index = -1;
            for (int i = 0; i < currentList.size(); i++) {
                if (currentList.get(i).getInsumoId() == insumo.getInsumoId()) {
                    index = i;
                    break;
                }
            }
            if (index != -1) {
                currentList.set(index, insumo);
                selectedInsumos.setValue(currentList);
            }
        }
    }

    public void removeSelectedInsumo(ActividadInsumo insumo) {
        List<ActividadInsumo> currentList = selectedInsumos.getValue();
        if (currentList != null) {
            currentList.removeIf(ai -> ai.getInsumoId() == insumo.getInsumoId());
            selectedInsumos.setValue(currentList);
        }
    }


    // NEW: Getter for selectedInsumos
    public LiveData<List<ActividadInsumo>> getSelectedInsumos() {
        return selectedInsumos;
    }

    public void cargarActividad(long id) {
        this.actividadId = id;
        if (actividadLiveData != null) {
            actividadLiveData.removeObserver(actividadObserver);
        }
        actividadLiveData = repository.getActividadById(id);
        actividadLiveData.observeForever(actividadObserver);
    }

    public void setCultivoId(Long id) {
        this.cultivoId = id;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (actividadLiveData != null) {
            actividadLiveData.removeObserver(actividadObserver);
        }
    }

    public LiveData<String> getTipoActividadError() {
        return tipoActividadError;
    }

    public LiveData<String> getPrioridadError() {
        return prioridadError;
    }

    public LiveData<String> getFechaError() {
        return fechaError;
    }

    public LiveData<String> getDescripcionError() {
        return descripcionError;
    }

    public LiveData<String> getEstadoError() {
        return estadoError;
    }

    public LiveData<Boolean> getNavegarAtras() {
        return navegarAtras;
    }

    public void onNavegacionCompleta() {
        navegarAtras.setValue(false);
    }


    private boolean validarTipoActividad() {
        if (tipoActividad.getValue() == null || tipoActividad.getValue().trim().isEmpty()) {
            tipoActividadError.setValue("Debe seleccionar un tipo de actividad");
            return false;
        }
        try {
            Actividad.TipoActividad.valueOf(tipoActividad.getValue());
            tipoActividadError.setValue(null);
            return true;
        } catch (IllegalArgumentException e) {
            tipoActividadError.setValue("El tipo de actividad no es válido");
            return false;
        }
    }

    private boolean validarPrioridad() {
        if (prioridad.getValue() == null || prioridad.getValue().trim().isEmpty()) {
            prioridadError.setValue("Debe seleccionar una prioridad");
            return false;
        }
        try {
            Actividad.Prioridad.valueOf(prioridad.getValue());
            prioridadError.setValue(null);
            return true;
        } catch (IllegalArgumentException e) {
            prioridadError.setValue("La prioridad no es válida");
            return false;
        }
    }

    private boolean validarFecha() {
        if (fecha.getValue() == null || fecha.getValue().trim().isEmpty()) {
            fechaError.setValue("La fecha de la actividad es obligatoria");
            return false;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        try {
            LocalDate fechaSeleccionada = LocalDate.parse(fecha.getValue(), formatter);
            // Optionally, you could add logic here to prevent future dates or restrict date ranges
            fechaError.setValue(null);
            return true;
        } catch (Exception e) {
            fechaError.setValue("El formato de fecha es incorrecto (dd/MM/yyyy)");
            return false;
        }
    }

    private boolean validarEstado() {
        if (estado.getValue() == null || estado.getValue().trim().isEmpty()) {
            estadoError.setValue("Debe seleccionar un estado");
            return false;
        }
        try {
            Actividad.Estado.valueOf(estado.getValue());
            estadoError.setValue(null);
            return true;
        } catch (IllegalArgumentException e) {
            estadoError.setValue("El estado no es válido");
            return false;
        }
    }

    private boolean validarDescripcion() {
        if (descripcion.getValue() != null) {
            int maxLength = 500; // Define your max length
            if (descripcion.getValue().length() > maxLength) {
                descripcionError.setValue("La descripción no puede exceder los " + maxLength + " caracteres");
                return false;
            }
        }
        descripcionError.setValue(null);
        return true;
    }

    private boolean esFormularioValido() {
        boolean tipoActividadValida = validarTipoActividad();
        boolean prioridadValida = validarPrioridad();
        boolean fechaValida = validarFecha();
        boolean estadoValido = validarEstado();
        boolean descripcionValida = validarDescripcion();
        return tipoActividadValida && prioridadValida && fechaValida && estadoValido && descripcionValida;
    }

    public void guardarActividad() {
        if (!esFormularioValido()) {
            return;
        }

        // Ensure cultivoId is set
        if (cultivoId == null) {
            // This should ideally not happen if navigation is set up correctly,
            // but add a check or throw an error if necessary
            navegarAtras.setValue(false); // Do not navigate if no cultivo is associated
            return;
        }

        // Convert date string to long
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate localDate = LocalDate.parse(fecha.getValue(), formatter);
        long fechaParaDb = localDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();

        Actividad actividadAGuardar;

        if (actividadId == null) {
            // Create new Actividad using the Builder
            actividadAGuardar = new Actividad.ActividadBuilder(cultivoId, Actividad.TipoActividad.valueOf(tipoActividad.getValue()))
                    .setPrioridad(Actividad.Prioridad.valueOf(prioridad.getValue()))
                    .setFecha(fechaParaDb)
                    .setDescripcion(descripcion.getValue() != null ? descripcion.getValue().trim() : "")
                    .setEstado(Actividad.Estado.valueOf(estado.getValue()))
                    .build();
            // Pass the selected insumos list
            repository.insertActividadConInsumos(actividadAGuardar, selectedInsumos.getValue());
        } else {
            // Update existing Actividad
            actividadAGuardar = actividadExistente; // Start with the existing object
            actividadAGuardar.setActividad(Actividad.TipoActividad.valueOf(tipoActividad.getValue()));
            actividadAGuardar.setPrioridad(Actividad.Prioridad.valueOf(prioridad.getValue()));
            actividadAGuardar.setFecha(fechaParaDb);
            actividadAGuardar.setDescripcion(descripcion.getValue() != null ? descripcion.getValue().trim() : "");
            actividadAGuardar.setEstado(Actividad.Estado.valueOf(estado.getValue()));
            repository.updateActividad(actividadAGuardar);
        }

        navegarAtras.setValue(true); // Indicate successful save and readiness to navigate back
    }
}