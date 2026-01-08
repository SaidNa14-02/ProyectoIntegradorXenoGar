package com.said.xenogar.data.local.entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

// Define la tabla de enlace
@Entity(
        tableName = "actividad_insumo",
        primaryKeys = {"actividadId", "insumoId"},
        indices = {@Index("insumoId")},
        foreignKeys = {
                @ForeignKey(
                        entity = Actividad.class,
                        parentColumns = "id",
                        childColumns = "actividadId",
                        onDelete = ForeignKey.CASCADE
                ),
                @ForeignKey(
                        entity = Insumo.class,
                        parentColumns = "id",
                        childColumns = "insumoId",
                        onDelete = ForeignKey.CASCADE
                )
        }
)
public class ActividadInsumo {
    private long actividadId;
    private long insumoId;
    private double cantidadUtilizada;

    // --- Constructor, Getters y Setters ---
    public ActividadInsumo(long actividadId, long insumoId, double cantidadUtilizada) {
        this.actividadId = actividadId;
        this.insumoId = insumoId;
        this.cantidadUtilizada = cantidadUtilizada;
    }

    public long getActividadId() { return actividadId; }
    public void setActividadId(long actividadId) { this.actividadId = actividadId; }

    public long getInsumoId() { return insumoId; }
    public void setInsumoId(long insumoId) { this.insumoId = insumoId; }

    public double getCantidadUtilizada() {
        return cantidadUtilizada;
    }

    public void setCantidadUtilizada(double cantidadUtilizada) {
        this.cantidadUtilizada = cantidadUtilizada;
    }
}
