package com.bylaba.containerqr.models;

public class Qr {
    private int id;
    private int actividadId;
    private int tipoQrId;
    private String nombre;
    private String rutaImagen;
    private boolean activo;

    public Qr() {
    }

    public Qr(int id, int actividadId, int tipoQrId, String nombre, String rutaImagen, boolean activo) {
        this.id = id;
        this.actividadId = actividadId;
        this.tipoQrId = tipoQrId;
        this.nombre = nombre;
        this.rutaImagen = rutaImagen;
        this.activo = activo;
    }

    public Qr(int actividadId, int tipoQrId, String nombre, String rutaImagen, boolean activo) {
        this.actividadId = actividadId;
        this.tipoQrId = tipoQrId;
        this.nombre = nombre;
        this.rutaImagen = rutaImagen;
        this.activo = activo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getActividadId() {
        return actividadId;
    }

    public void setActividadId(int actividadId) {
        this.actividadId = actividadId;
    }

    public int getTipoQrId() {
        return tipoQrId;
    }

    public void setTipoQrId(int tipoQrId) {
        this.tipoQrId = tipoQrId;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getRutaImagen() {
        return rutaImagen;
    }

    public void setRutaImagen(String rutaImagen) {
        this.rutaImagen = rutaImagen;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }
}
