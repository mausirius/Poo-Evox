package com.evoxspace.model;

public final class Mecanico extends Usuario {
    private final String especialidad;

    public Mecanico(String nombre, String email, String passwordHash, String especialidad) {
        super(nombre, email, passwordHash);
        if (especialidad == null || especialidad.isBlank()) throw new IllegalArgumentException("La especialidad es obligatoria");
        this.especialidad = especialidad.trim();
    }
    public String getEspecialidad() { return especialidad; }
}
