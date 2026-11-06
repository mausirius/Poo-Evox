package com.evoxspace.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class Cliente extends Usuario {
    private final List<VehiculoElectrico> vehiculos = new ArrayList<>();

    public Cliente(String nombre, String email, String passwordHash) {
        super(nombre, email, passwordHash);
    }

    public void agregarVehiculo(VehiculoElectrico vehiculo) {
        if (vehiculo == null || vehiculos.contains(vehiculo)) throw new IllegalArgumentException("Vehículo inválido o duplicado");
        vehiculos.add(vehiculo);
    }
    public List<VehiculoElectrico> getVehiculos() {
        return Collections.unmodifiableList(vehiculos);
    }
}
