package com.ds3midterm;

public class Registro implements Comparable<Registro> {
    public enum Estado { PENDIENTE, ATENDIDO, CANCELADO }

    private int id;
    private String nombre;
    private int prioridad;
    private int padre;
    private Estado estado;

    public Registro(int id, String nombre, int prioridad, int padre) {
        this.id = id;
        this.nombre = nombre;
        this.prioridad = prioridad;
        this.padre = padre;
        this.estado = Estado.PENDIENTE;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(int prioridad) {
        this.prioridad = prioridad;
    }

    public int getPadre() {
        return padre;
    }

    public void setPadre(int padre) {
        this.padre = padre;
    }

    public Estado getEstado() {
        return estado;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    @Override
    public int compareTo(Registro otro) {
        if (otro == null) {
            return 1;
        }

        int prioridadCmp = Integer.compare(otro.prioridad, this.prioridad);
        if (prioridadCmp != 0) {
            return prioridadCmp;
        }

        return Integer.compare(this.id, otro.id);
    }

    @Override
    public String toString() {
        return id + "(" + prioridad + ")";
    }
}
