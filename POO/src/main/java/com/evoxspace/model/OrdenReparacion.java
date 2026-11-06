package com.evoxspace.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public final class OrdenReparacion {
    private final UUID id;
    private final VehiculoElectrico vehiculo;
    private EstadoReparacion estado;
    private final LocalDate fechaIngreso;
    private LocalDate fechaEntregaEstimada;
    private Diagnostico diagnostico;
    private Cotizacion cotizacion;
    private Mecanico mecanico;
    private final List<PiezaRefaccion> piezas = new ArrayList<>();
    private final List<DocumentoEvidencia> documentos = new ArrayList<>();

    public OrdenReparacion(VehiculoElectrico vehiculo) {
        if (vehiculo == null) throw new IllegalArgumentException("El vehículo es obligatorio");
        this.id = UUID.randomUUID();
        this.vehiculo = vehiculo;
        this.estado = EstadoReparacion.EN_DIAGNOSTICO;
        this.fechaIngreso = LocalDate.now();
    }
    public UUID getId() { return id; }
    public VehiculoElectrico getVehiculo() { return vehiculo; }
    public EstadoReparacion getEstado() { return estado; }
    public LocalDate getFechaIngreso() { return fechaIngreso; }
    public LocalDate getFechaEntregaEstimada() { return fechaEntregaEstimada; }
    public Diagnostico getDiagnostico() { return diagnostico; }
    public Cotizacion getCotizacion() { return cotizacion; }
    public Mecanico getMecanico() { return mecanico; }
    public List<PiezaRefaccion> getPiezas() { return Collections.unmodifiableList(piezas); }
    public List<DocumentoEvidencia> getDocumentos() { return Collections.unmodifiableList(documentos); }
    public void actualizarEstado(EstadoReparacion estado) { if (estado == null) throw new IllegalArgumentException("Estado obligatorio"); this.estado = estado; }
    public void asignarDiagnostico(Diagnostico diagnostico) { this.diagnostico = diagnostico; }
    public void asignarCotizacion(Cotizacion cotizacion) { this.cotizacion = cotizacion; }
    public void asignarMecanico(Mecanico mecanico) { this.mecanico = mecanico; }
    public void agregarPieza(PiezaRefaccion pieza) { if (pieza == null) throw new IllegalArgumentException("Pieza obligatoria"); piezas.add(pieza); estado = EstadoReparacion.ESPERANDO_PIEZAS; }
    public void agregarDocumento(DocumentoEvidencia documento) { if (documento == null) throw new IllegalArgumentException("Documento obligatorio"); documentos.add(documento); }
    public void setFechaEntregaEstimada(LocalDate fecha) { if (fecha != null && fecha.isBefore(fechaIngreso)) throw new IllegalArgumentException("La entrega no puede ser anterior al ingreso"); this.fechaEntregaEstimada = fecha; }
}
