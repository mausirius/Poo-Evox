package com.evoxspace.model;

import java.math.BigDecimal;

public final class Cotizacion {
    private final BigDecimal manoDeObra;
    private final BigDecimal piezas;
    private boolean aprobada;

    public Cotizacion(BigDecimal manoDeObra, BigDecimal piezas) {
        if (manoDeObra == null || piezas == null || manoDeObra.signum() < 0 || piezas.signum() < 0) throw new IllegalArgumentException("Importes inválidos");
        this.manoDeObra = manoDeObra;
        this.piezas = piezas;
    }
    public BigDecimal getManoDeObra() { return manoDeObra; }
    public BigDecimal getPiezas() { return piezas; }
    public BigDecimal getTotal() { return manoDeObra.add(piezas); }
    public boolean isAprobada() { return aprobada; }
    public void aprobar() { aprobada = true; }
}
