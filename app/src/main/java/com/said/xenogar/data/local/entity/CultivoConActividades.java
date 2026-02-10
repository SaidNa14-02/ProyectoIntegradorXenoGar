package com.said.xenogar.data.local.entity;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

public class CultivoConActividades {
    @Embedded
    public Cultivo cultivo;
    @Relation(
            parentColumn = "id",
            entityColumn = "cultivoId"
    )
    public List<Actividad> actividades;
}
