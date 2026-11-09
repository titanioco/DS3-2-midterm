package com.ds3midterm;

public class Registro implements Comparable<Registro> {
    // El estado representa el ciclo de vida real de la alarma.
    // Una alarma nace como PENDIENTE, luego puede ser ATENDIDA o CANCELADA si la zona falla.
    public enum Estado { PENDIENTE, ATENDIDO, CANCELADO }

    // Identificador único de la alarma.
    private int id;

    // Nombre del equipo o zona que produjo la alarma.
    private String nombre;

    // Prioridad de 1 a 10. Cuanto mayor, más urgente.
    private int prioridad;

    // Padre que define la ubicación de la alarma dentro de la jerarquía física de la planta.
    private int padre;

    // Estado actual. Este mismo objeto se comparte entre el árbol, el AVL y el heap.
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

        // La prioridad más alta gana.
        // En caso de empatar, el ID menor va antes.
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
