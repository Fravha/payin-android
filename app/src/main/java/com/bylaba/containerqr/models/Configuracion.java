package com.bylaba.containerqr.models;

public class Configuracion {
    private int id;
    private String clave;
    private String valor;

    public Configuracion() {
    }

    public Configuracion(int id, String clave, String valor) {
        this.id = id;
        this.clave = clave;
        this.valor = valor;
    }

    public Configuracion(String clave, String valor) {
        this.clave = clave;
        this.valor = valor;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }
}
