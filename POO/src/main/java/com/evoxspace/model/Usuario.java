package com.evoxspace.model;

import java.util.Objects;
import java.util.UUID;
import java.util.regex.Pattern;

public abstract class Usuario {
    private static final Pattern EMAIL = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");
    private final UUID id;
    private String nombre;
    private String email;
    private String passwordHash;

    protected Usuario(String nombre, String email, String passwordHash) {
        this.id = UUID.randomUUID();
        setNombre(nombre);
        setEmail(email);
        setPasswordHash(passwordHash);
    }

    public UUID getId() { return id; }
    public String getNombre() { return nombre; }
    public final void setNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) throw new IllegalArgumentException("El nombre es obligatorio");
        this.nombre = nombre.trim();
    }
    public String getEmail() { return email; }
    public final void setEmail(String email) {
        if (email == null || !EMAIL.matcher(email.trim()).matches()) {
            throw new IllegalArgumentException("El correo no tiene un formato válido");
        }
        this.email = email.trim().toLowerCase();
    }
    public String getPasswordHash() { return passwordHash; }
    public final void setPasswordHash(String passwordHash) {
        if (passwordHash == null || passwordHash.isBlank()) throw new IllegalArgumentException("La contraseña es obligatoria");
        this.passwordHash = passwordHash;
    }

    @Override public boolean equals(Object other) {
        return other instanceof Usuario usuario && id.equals(usuario.id);
    }
    @Override public int hashCode() { return Objects.hash(id); }
}
