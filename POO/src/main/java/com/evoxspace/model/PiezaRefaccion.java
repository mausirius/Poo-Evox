package com.evoxspace.model;

import java.time.LocalDate;

public record PiezaRefaccion(String nombre, LocalDate fechaEstimadaLlegada) {
    public PiezaRefaccion {
        if (nombre == null || nombre.isBlank() || fechaEstimadaLlegada == null) throw new IllegalArgumentException("Pieza inválida");
    }
}
