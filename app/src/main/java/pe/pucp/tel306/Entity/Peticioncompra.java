package pe.pucp.tel306.Entity;

import java.io.Serializable;

public class Peticioncompra implements Serializable {
    private Producto producto;
    private String nombreComprador;
    private String uiComprador;
    private String pk;
    private String estado;
    private String MotivoRechazo;
    private Double latitud;
    private Double longitud;
    private String correoUser;
    private String ubicacionGps;
    private int cantidad;

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public String getNombreComprador() {
        return nombreComprador;
    }

    public void setNombreComprador(String nombreComprador) {
        this.nombreComprador = nombreComprador;
    }

    public String getUiComprador() {
        return uiComprador;
    }

    public void setUiComprador(String uiComprador) {
        this.uiComprador = uiComprador;
    }

    public String getPk() {
        return pk;
    }

    public void setPk(String pk) {
        this.pk = pk;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getMotivoRechazo() {
        return MotivoRechazo;
    }

    public void setMotivoRechazo(String motivoRechazo) {
        MotivoRechazo = motivoRechazo;
    }

    public Double getLatitud() {
        return latitud;
    }

    public void setLatitud(Double latitud) {
        this.latitud = latitud;
    }

    public Double getLongitud() {
        return longitud;
    }

    public void setLongitud(Double longitud) {
        this.longitud = longitud;
    }

    public String getCorreoUser() {
        return correoUser;
    }

    public void setCorreoUser(String correoUser) {
        this.correoUser = correoUser;
    }

    public String getUbicacionGps() {
        return ubicacionGps;
    }

    public void setUbicacionGps(String ubicacionGps) {
        this.ubicacionGps = ubicacionGps;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }
}
