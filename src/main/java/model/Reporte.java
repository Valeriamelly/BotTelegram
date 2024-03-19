package model;

import java.time.LocalDate;

public class Reporte {
    private String id;
    private LocalDate fechaReporte;
    private String tipoReporte;
    private String modulo;
    private String componente;
    private String accion;
    private String observacion;
    private String solucion;
    private String prioridad;
    private String norma;

    // Constructor con todos los campos
    public Reporte(String id, LocalDate fechaReporte, String tipoReporte, String modulo, String componente, String accion, String observacion, String solucion, String prioridad, String norma) {
        this.id = id;
        this.fechaReporte = fechaReporte;
        this.tipoReporte = tipoReporte;
        this.modulo = modulo;
        this.componente = componente;
        this.accion = accion;
        this.observacion = observacion;
        this.solucion = solucion;
        this.prioridad = prioridad;
        this.norma = norma;
    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LocalDate getFechaReporte() {
        return fechaReporte;
    }

    public void setFechaReporte(LocalDate fechaReporte) {
        this.fechaReporte = fechaReporte;
    }

    public String getTipoReporte() {
        return tipoReporte;
    }

    public void setTipoReporte(String tipoReporte) {
        this.tipoReporte = tipoReporte;
    }

    public String getModulo() {
        return modulo;
    }

    public void setModulo(String modulo) {
        this.modulo = modulo;
    }

    public String getComponente() {
        return componente;
    }

    public void setComponente(String componente) {
        this.componente = componente;
    }

    public String getAccion() {
        return accion;
    }

    public void setAccion(String accion) {
        this.accion = accion;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public String getSolucion() {
        return solucion;
    }

    public void setSolucion(String solucion) {
        this.solucion = solucion;
    }

    public String getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(String prioridad) {
        this.prioridad = prioridad;
    }

    public String getNorma() {
        return norma;
    }

    public void setNorma(String norma) {
        this.norma = norma;
    }
}
