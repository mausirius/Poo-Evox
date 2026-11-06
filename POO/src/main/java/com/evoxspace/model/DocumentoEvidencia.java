package com.evoxspace.model;

public record DocumentoEvidencia(String nombre, String ruta) {
    public DocumentoEvidencia {
        if (nombre == null || nombre.isBlank() || ruta == null || ruta.isBlank()) throw new IllegalArgumentException("Documento inválido");
    }
}
