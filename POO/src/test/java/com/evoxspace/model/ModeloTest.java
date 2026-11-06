package com.evoxspace.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class ModeloTest {
    @Test
    void creaOrdenYActualizaSuEstado() {
        Cliente cliente = new Cliente("Ana López", "ana@evoxspace.com", "hash");
        VehiculoElectrico vehiculo = new VehiculoElectrico("1HGBH41JXMN109186", "Evox", "S1", 2025);
        cliente.agregarVehiculo(vehiculo);
        OrdenReparacion orden = new OrdenReparacion(vehiculo);
        orden.asignarCotizacion(new Cotizacion(new BigDecimal("1000"), new BigDecimal("2500")));
        orden.actualizarEstado(EstadoReparacion.EN_REPARACION);
        assertEquals(EstadoReparacion.EN_REPARACION, orden.getEstado());
        assertEquals(new BigDecimal("3500"), orden.getCotizacion().getTotal());
    }

    @Test
    void rechazaVinInvalido() {
        assertThrows(IllegalArgumentException.class,
                () -> new VehiculoElectrico("VIN", "Evox", "S1", 2025));
    }
}
