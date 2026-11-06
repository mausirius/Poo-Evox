package com.evoxspace.model;

import java.util.UUID;
import java.util.regex.Pattern;

public final class VehiculoElectrico {
    private static final Pattern VIN = Pattern.compile("^[A-HJ-NPR-Z0-9]{17}$");
    private final UUID id;
    private final String vin;
    private String marca;
    private String modelo;
    private int anio;

    public VehiculoElectrico(String vin, String marca, String modelo, int anio) {
        this.id = UUID.randomUUID();
        if (vin == null || !VIN.matcher(vin.toUpperCase()).matches()) throw new IllegalArgumentException("El VIN debe tener 17 caracteres válidos");
        this.vin = vin.toUpperCase();
        setMarca(marca);
        setModelo(modelo);
        if (anio < 1886 || anio > java.time.Year.now().getValue() + 1) throw new IllegalArgumentException("Año inválido");
        this.anio = anio;
    }
    public UUID getId() { return id; }
    public String getVin() { return vin; }
    public String getMarca() { return marca; }
    public String getModelo() { return modelo; }
    public int getAnio() { return anio; }
    public void setMarca(String marca) { if (marca == null || marca.isBlank()) throw new IllegalArgumentException("Marca obligatoria"); this.marca = marca.trim(); }
    public void setModelo(String modelo) { if (modelo == null || modelo.isBlank()) throw new IllegalArgumentException("Modelo obligatorio"); this.modelo = modelo.trim(); }
    public void setAnio(int anio) { if (anio < 1886) throw new IllegalArgumentException("Año inválido"); this.anio = anio; }
}
