package com.said.xenogar.data.local.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

@Entity(tableName = "insumos")
@TypeConverters(Insumo.Converters.class)
public class Insumo {

    public enum TipoInsumo {
        FERTILIZANTE("Fertilizante"),
        PESTICIDA("Pesticida"),
        SEMILLA("Semilla"),
        HERRAMIENTA("Herramienta"),
        OTRO("Otro");

        private final String displayName;
        TipoInsumo(String displayName) { this.displayName = displayName; }
        @Override public String toString() { return displayName; }
    }

    public enum UnidadMedida {
        LITROS("L"),
        KILOGRAMOS("Kg"),
        UNIDADES("Ud"),
        GRAMOS("g"),
        SACOS("Sacos");

        private final String displayName;
        UnidadMedida(String displayName) { this.displayName = displayName; }
        @Override public String toString() { return displayName; }
    }

    @PrimaryKey(autoGenerate = true)
    private Long id;
    private String nombre;
    private TipoInsumo tipo;
    private double cantidadActual;
    private UnidadMedida unidad;
    private double puntoReorden; //

    private Insumo(String nombre, TipoInsumo tipo, double cantidadActual, UnidadMedida unidad, double puntoReorden){
        this.nombre = nombre;
        this.tipo = tipo;
        this.cantidadActual = cantidadActual;
        if(!this.tipo.equals(TipoInsumo.HERRAMIENTA)){
            this.unidad = unidad;
        }
        this.puntoReorden = puntoReorden;
    }

    public static class InsumoBuilder{
        private String nombre;
        private TipoInsumo tipoInsumo;

        //Campos opcionales
        private double cantidadActual = 0.0;
        private UnidadMedida unidad = null;
        private double puntoReorden = 0.0;

        public InsumoBuilder(String nombre, TipoInsumo tipoInsumo){
            this.nombre = nombre;
            this.tipoInsumo = tipoInsumo;
        }

        public InsumoBuilder setCantidadActual(double cantidadActual){
            this.cantidadActual = cantidadActual;
            return this;
        }

        public InsumoBuilder setUnidad(UnidadMedida unidad){
            this.unidad = unidad;
            return this;
        }

        public InsumoBuilder setPuntoReorden(double puntoReorden){
            this.puntoReorden = puntoReorden;
            return this;
        }
        public Insumo build(){
            return new Insumo(nombre, tipoInsumo, cantidadActual, unidad, puntoReorden);
        }

    }

    // Getters y Setters...
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public double getCantidadActual() { return cantidadActual; }
    public void setCantidadActual(double cantidadActual) { this.cantidadActual = cantidadActual; }

    //Converters para insumo
    public static class Converters {
        @androidx.room.TypeConverter
        public static String fromTipoInsumo(TipoInsumo tipo) { return tipo == null ? null : tipo.name(); }
        @androidx.room.TypeConverter
        public static TipoInsumo toTipoInsumo(String tipo) { return tipo == null ? null : TipoInsumo.valueOf(tipo); }

        @androidx.room.TypeConverter
        public static String fromUnidad(UnidadMedida unidad) { return unidad == null ? null : unidad.name(); }
        @androidx.room.TypeConverter
        public static UnidadMedida toUnidad(String unidad) { return unidad == null ? null : UnidadMedida.valueOf(unidad); }
    }
}