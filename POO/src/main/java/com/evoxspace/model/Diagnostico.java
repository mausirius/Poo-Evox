package com.evoxspace.model;

public final class Diagnostico {
    private final String codigoDtc;
    private final TipoFalla tipoFalla;
    private final String descripcion;

    public Diagnostico(String codigoDtc, TipoFalla tipoFalla, String descripcion) {
        if (codigoDtc == null || codigoDtc.isBlank() || tipoFalla == null || descripcion == null || descripcion.isBlank()) {
            throw new IllegalArgumentException("El diagnóstico está incompleto");
        }
        this.codigoDtc = codigoDtc.trim();
        this.tipoFalla = tipoFalla;
        this.descripcion = descripcion.trim();
    }
    public String getCodigoDtc() { return codigoDtc; }
    public TipoFalla getTipoFalla() { return tipoFalla; }
    public String getDescripcion() { return descripcion; }
}
