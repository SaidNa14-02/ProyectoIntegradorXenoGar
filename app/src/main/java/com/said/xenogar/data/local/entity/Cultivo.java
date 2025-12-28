package com.said.xenogar.data.local.entity;


import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;


@Entity(tableName = "cultivos")
@TypeConverters(Cultivo.Converters.class)
public class Cultivo {
    public enum TipoCultivo {
        ORNAMENTAL("Ornamental"),
        FRUTAL("Frutal"),
        AROMATICO("Aromático"),
        INDEFINIDO("Indefinido");

        private final String displayName;

        TipoCultivo(String displayName){
            this.displayName = displayName;
        }

        @Override
        public String toString(){
            return displayName;
        }
    }

    //Para la conversion de tipos del enum
    public static class Converters {
        @androidx.room.TypeConverter
        public static String fromTipoCultivo(TipoCultivo tipoCultivo) {
            return tipoCultivo == null ? null : tipoCultivo.name();
        }
        @androidx.room.TypeConverter
        public static TipoCultivo toTipoCultivo(String tipoCultivo) {
            return tipoCultivo == null ? null : TipoCultivo.valueOf(tipoCultivo);
        }
    }

    @PrimaryKey(autoGenerate = true)
    private Long id;
    private String nombre;
    private TipoCultivo tipo = TipoCultivo.INDEFINIDO;
    private String descripcion;
    private long fechaInicio;
    private long fechaFinalizacion;
    private int existencias = 0;


    //Setters y getters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public TipoCultivo getTipo() {
        return tipo;
    }

    public void setTipo(TipoCultivo tipo) {
        this.tipo = tipo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public long getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(long fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public long getFechaFinalizacion() {
        return fechaFinalizacion;
    }

    public void setFechaFinalizacion(long fechaFinalizacion) {
        this.fechaFinalizacion = fechaFinalizacion;
    }

    public int getExistencias() {
        return existencias;
    }

    public void setExistencias(int existencias) {
        this.existencias = existencias;
    }
}
