package com.said.xenogar.data.local.entity;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import java.util.List;

public class ActividadConInsumo {
    @Embedded
    public Actividad actividad;
    @Relation(
            parentColumn = "id",
            entity = Insumo.class,
            entityColumn = "id",
            associateBy = @Junction(
                    value = ActividadInsumo.class,
                    parentColumn = "actividadId",
                    entityColumn = "insumoId"
            )
    )
    public List<Insumo> insumos;
}
