package com.said.xenogar.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.said.xenogar.data.local.entity.Cultivo;
import com.said.xenogar.data.repository.AgroRepository;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class CultivoFormViewModel extends ViewModel {
    public final MutableLiveData<String> nombre = new MutableLiveData<>();
    public final MutableLiveData<String> tipo = new MutableLiveData<>();
    public final MutableLiveData<String> fecha = new MutableLiveData<>();
    public final MutableLiveData<String> existencias = new MutableLiveData<>();
    public final MutableLiveData<String> descripcion = new MutableLiveData<>();
    public final MutableLiveData<String> fechaFin = new MutableLiveData<>();
    public final MutableLiveData<String> fechaFinError = new MutableLiveData<>();
    private final AgroRepository repository;
    private final MutableLiveData<String> nombreError = new MutableLiveData<>();
    private final MutableLiveData<String> tipoError = new MutableLiveData<>();
    private final MutableLiveData<String> fechaError = new MutableLiveData<>();
    private final MutableLiveData<String> existenciasError = new MutableLiveData<>();
    private final MutableLiveData<String> descripcionError = new MutableLiveData<>();
    private final MutableLiveData<Boolean> navegarAtras = new MutableLiveData<>();


    @Inject
    public CultivoFormViewModel(AgroRepository repository) {
        this.repository = repository;
    }

    public LiveData<String> getNombreError() {
        return nombreError;
    }

    public LiveData<String> getTipoError() {
        return tipoError;
    }

    public LiveData<String> getFechaError() {
        return fechaError;
    }

    public LiveData<String> getExistenciasError() {
        return existenciasError;
    }

    public LiveData<String> getDescripcionError() {
        return descripcionError;
    }
    public LiveData<String> getFechaFinError() { return fechaFinError;}
    public MutableLiveData<Boolean> getNavegarAtras() {
        return navegarAtras;
    }


    private boolean esFormularioValido() {
        boolean nombreValido = validarNombre();
        boolean tipoValido = validarTipo();
        boolean fechaValida = validarFecha();
        boolean existenciasValidas = validarExistencias();
        boolean descripcionValida = validarDescripcion();
        boolean fechaFinValida = validarFechaFin();
        return nombreValido & tipoValido & fechaValida & existenciasValidas & descripcionValida & fechaFinValida;
    }

    public void guardarCultivo() {
        if (!esFormularioValido()) {
            return;
        }

        Cultivo nuevoCultivo = new Cultivo();
        nuevoCultivo.setNombre(nombre.getValue().trim());
        nuevoCultivo.setTipo(Cultivo.TipoCultivo.valueOf(tipo.getValue()));
        nuevoCultivo.setExistencias(Integer.parseInt(existencias.getValue().trim()));


        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate localDate = LocalDate.parse(fecha.getValue(), formatter);
        long fechaParaDb = localDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
        nuevoCultivo.setFechaInicio(fechaParaDb);

        if (fechaFin.getValue() != null && !fechaFin.getValue().trim().isEmpty()) {
            LocalDate localDateFin = LocalDate.parse(fechaFin.getValue(), formatter);
            long fechaFinParaDb = localDateFin.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
            nuevoCultivo.setFechaFinalizacion(fechaFinParaDb);
        }

        if (descripcion.getValue() != null) {
            nuevoCultivo.setDescripcion(descripcion.getValue().trim());
        }
        repository.insertCultivo(nuevoCultivo);
        navegarAtras.setValue(true);

    }

    private boolean validarNombre() {
        if (nombre.getValue() == null || nombre.getValue().trim().isEmpty()) {
            nombreError.setValue("El nombre del cultivo es obligatorio");
            return false;
        }
        if (nombre.getValue().matches(".*\\d+.*")) {
            nombreError.setValue("El nombre no puede contener números");
            return false;
        }
        nombreError.setValue(null);
        return true;
    }

    private boolean validarTipo() {
        if (tipo.getValue() == null || tipo.getValue().trim().isEmpty()) {
            tipoError.setValue("Debe seleccionar un tipo de cultivo");
            return false;
        }
        try {
            Cultivo.TipoCultivo.valueOf(tipo.getValue());
            tipoError.setValue(null);
            return true;
        } catch (IllegalArgumentException e) {
            tipoError.setValue("El tipo de cultivo no es válido");
            return false;
        }
    }

    private boolean validarFecha() {
        if (fecha.getValue() == null || fecha.getValue().trim().isEmpty()) {
            fechaError.setValue("La fecha de inicio es obligatoria");
            return false;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        try {
            LocalDate fechaSeleccionada = LocalDate.parse(fecha.getValue(), formatter);
            LocalDate fechaActual = LocalDate.now();
            if (fechaSeleccionada.isAfter(fechaActual)) {
                fechaError.setValue("La fecha no puede ser futura");
                return false;
            }
            fechaError.setValue(null);
            return true;
        } catch (Exception e) {
            fechaError.setValue("El formato de fecha es incorrecto (dd/MM/yyyy)");
            return false;
        }
    }

    private boolean validarFechaFin() {
        if (fechaFin.getValue() != null && !fechaFin.getValue().trim().isEmpty()) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            try {
                LocalDate fechaSeleccionada = LocalDate.parse(fechaFin.getValue(), formatter);

                if (fecha.getValue() == null || fecha.getValue().trim().isEmpty()) {
                    fechaFinError.setValue("La fecha de inicio es necesaria");
                    return false;
                }
                LocalDate fechaInicio = LocalDate.parse(fecha.getValue(), formatter);

                if (fechaSeleccionada.isBefore(fechaInicio)) {
                    fechaFinError.setValue("La fecha de fin debe ser posterior a la fecha de inicio");
                    return false;
                }
                fechaFinError.setValue(null);
                return true;
            } catch (Exception e) {
                fechaFinError.setValue("El formato de fecha es incorrecto (dd/MM/yyyy)");
                return false;
            }
        } else {
            fechaFinError.setValue(null);
            return true;
        }
    }


    private boolean validarExistencias() {
        String existenciasStr = existencias.getValue();
        if (existenciasStr == null || existenciasStr.trim().isEmpty()) {
            existenciasError.setValue("Las existencias son obligatorias");
            return false;
        }
        try {
            int valorExistencias = Integer.parseInt(existenciasStr.trim());
            if (valorExistencias <= 0) {
                existenciasError.setValue("Debe ser mayor que 0");
                return false;
            }
        } catch (NumberFormatException e) {
            existenciasError.setValue("Debe ser un número entero válido");
            return false;
        }
        existenciasError.setValue(null);
        return true;
    }

    private boolean validarDescripcion() {
        if (descripcion.getValue() != null) {
            int maxLength = 500;
            if (descripcion.getValue().length() > maxLength) {
                descripcionError.setValue("La descripción no puede exceder los " + maxLength + " caracteres");
                return false;
            }
        }
        descripcionError.setValue(null);
        return true;
    }

    public void onNavegacionCompleta(){
        navegarAtras.setValue(false);
    }
}
