package com.said.xenogar.data.local.entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;


@TypeConverters(Actividad.Converters.class)
@Entity(tableName = "actividades",
        foreignKeys = @ForeignKey(entity = Cultivo.class, parentColumns = "id", childColumns = "cultivoId", onDelete = ForeignKey.CASCADE),
        indices = {@Index("cultivoId")})
public class Actividad {
    public enum TipoActividad {
        RIEGO("Riego"),
        FUMIGACION("Fumigación"),
        PODA("Poda"),
        COSECHA("Cosecha"),
        OTRO("Otro");

        private final String displayName;
        TipoActividad(String displayName) {
            this.displayName = displayName;
        }
        @Override
        public String toString(){
            return displayName;
        }
    };

    public enum Prioridad {
        ALTA("Alta"),
        MEDIA("Media"),
        BAJA("Baja");
        private final String displayName;
        Prioridad(String displayName) {
            this.displayName = displayName;
        }
        @Override
        public String toString(){
            return displayName;
        }
    };

    public enum Estado {
        PENDIENTE("Pendiente"),
        EN_PROGRESO("En progreso"),
        CANCELADA("Cancelada"),
        POSPUESTA("Pospuesta"),
        COMPLETADA("Completada");
        private final String displayName;
        Estado(String displayName) {
            this.displayName = displayName;
        }
        @Override
        public String toString(){
            return displayName;
        }
    };

    @PrimaryKey(autoGenerate = true)
    private Long id;
    private Long cultivoId;
    private TipoActividad actividad;
    private Prioridad prioridad;
    private long fecha;
    private String descripcion;
    private Estado estado;

    private Actividad(Long cultivoId, TipoActividad actividad, Prioridad prioridad, long fecha, String descripcion, Estado estado){
        this.cultivoId = cultivoId;
        this.actividad = actividad;
        this.prioridad = prioridad;
        this.fecha = fecha;
        this.descripcion = descripcion;
        this.estado = estado;
    }

    //Builder para la creacion de actividades

    public static class ActividadBuilder {
        private final Long cultivoId;
        private final TipoActividad actividad;

        //Campos opcionales
        private Prioridad prioridad = Prioridad.MEDIA;
        private long fecha = System.currentTimeMillis();
        private String descripcion = "";
        private Estado estado = Estado.PENDIENTE;

        //Constructor del builder con campos obligatorios
        public ActividadBuilder (Long cultivoId, TipoActividad actividad){
            this.cultivoId = cultivoId;
            this.actividad = actividad;
        }

        //Setters para campos opcionales
        public ActividadBuilder setPrioridad(Prioridad prioridad) {
            this.prioridad = prioridad;
            return this;
        }

        public ActividadBuilder setFecha(long fecha) {
            this.fecha = fecha;
            return this;
        }

        public ActividadBuilder setDescripcion(String descripcion) {
            this.descripcion = descripcion;
            return this;
        }

        public ActividadBuilder setEstado(Estado estado) {
            this.estado = estado;
            return this;
        }
        public Actividad build(){
            return new Actividad(cultivoId, actividad, prioridad, fecha, descripcion, estado);
        }

    }



    //Setters y getters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCultivoId() {
        return cultivoId;
    }

    public void setCultivoId(Long cultivoId) {
        this.cultivoId = cultivoId;
    }

    public TipoActividad getActividad() {
        return actividad;
    }

    public void setActividad(TipoActividad actividad) {
        this.actividad = actividad;
    }

    public Prioridad getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(Prioridad prioridad) {
        this.prioridad = prioridad;
    }

    public long getFecha() {
        return fecha;
    }

    public void setFecha(long fecha) {
        this.fecha = fecha;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Estado getEstado() {
        return estado;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    //Converters para los enums
    public static class Converters {
        @androidx.room.TypeConverter
        public static String fromTipoActividad(TipoActividad tipoActividad) {
            return tipoActividad == null ? null : tipoActividad.name();

    }
        @androidx.room.TypeConverter
        public static TipoActividad toTipoActividad(String tipoActividad) {
            return tipoActividad == null ? null : TipoActividad.valueOf(tipoActividad);
        }
        @androidx.room.TypeConverter
        public static String fromPrioridad(Prioridad prioridad) {
            return prioridad == null ? null : prioridad.name();
        }
        @androidx.room.TypeConverter
        public static Prioridad toPrioridad(String prioridad) {
            return prioridad == null ? null : Prioridad.valueOf(prioridad);
        }

        @androidx.room.TypeConverter
        public static String fromEstado(Estado estado) {
            return estado == null ? null : estado.name();
        }

        @androidx.room.TypeConverter
        public static Estado toEstado(String estado) {
            return estado == null ? null : Estado.valueOf(estado);
        }
    }
}


