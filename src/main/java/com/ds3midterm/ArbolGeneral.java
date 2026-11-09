package com.ds3midterm;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class ArbolGeneral {

    // Cada nodo guarda la alarma real y una lista con sus hijos.
    // La idea es modelar la planta física: raíz -> sección -> equipo -> alarma.
    private static class Nodo {
        Registro registro;
        List<Nodo> hijos;

        Nodo(Registro registro) {
            this.registro = registro;
            this.hijos = new ArrayList<>();
        }
    }

    private Nodo raiz;

    public ArbolGeneral() {
        this.raiz = null;
    }

    // Agrega una alarma al árbol según su padre.
    // Si el padre es 0, se convierte en la raíz.
    // En cualquier otro caso, se busca el nodo con ese ID y se añade como hijo.
    public void agregarHijo(Registro registro) {
        if (registro == null) {
            throw new IllegalArgumentException("El registro no puede ser nulo.");
        }

        if (registro.getPadre() == 0) {
            if (raiz != null) {
                throw new IllegalArgumentException("La raíz ya existe.");
            }
            raiz = new Nodo(registro);
            return;
        }

        Nodo padre = buscarNodoPorId(raiz, registro.getPadre());
        if (padre == null) {
            throw new IllegalArgumentException("No se encontró el padre con ID " + registro.getPadre());
        }

        padre.hijos.add(new Nodo(registro));
    }

    // Recorre la estructura desde la raíz, luego izquierda/izquierda en el árbol general.
    public String imprimirPreorden() {
        if (raiz == null) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        preorden(raiz, sb);
        return sb.length() > 0 ? sb.substring(0, sb.length() - 1) : "";
    }

    // Recorre primero los hijos y luego la raíz.
    public String imprimirPostorden() {
        if (raiz == null) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        postorden(raiz, sb);
        return sb.length() > 0 ? sb.substring(0, sb.length() - 1) : "";
    }

    // Recorre por niveles: raíz, luego hijos del primer nivel, etc.
    public String imprimirPorNiveles() {
        if (raiz == null) {
            return "";
        }

        Queue<Nodo> cola = new ArrayDeque<>();
        cola.offer(raiz);
        StringBuilder sb = new StringBuilder();

        while (!cola.isEmpty()) {
            Nodo actual = cola.poll();
            sb.append(actual.registro.getId()).append(",");
            for (Nodo hijo : actual.hijos) {
                cola.offer(hijo);
            }
        }

        return sb.length() > 0 ? sb.substring(0, sb.length() - 1) : "";
    }

    // Altura de un árbol general: número de aristas desde la raíz hasta la hoja más profunda.
    public int calcularAltura() {
        return altura(raiz);
    }

    // Cuenta cuántas hojas tiene el árbol.
    public int contarHojas() {
        return contarHojas(raiz);
    }

    // Cancela todo el subárbol a partir de un nodo.
    // Solo cambia a CANCELADO las alarmas que estén en PENDIENTE.
    public int cancelarSubarbol(int id) {
        Nodo nodo = buscarNodoPorId(raiz, id);
        if (nodo == null) {
            return 0;
        }

        int[] contador = {0};
        cancelarDesde(nodo, contador);
        return contador[0];
    }

    public boolean existeId(int id) {
        return buscarNodoPorId(raiz, id) != null;
    }

    private void preorden(Nodo nodo, StringBuilder sb) {
        if (nodo == null) {
            return;
        }

        sb.append(nodo.registro.getId()).append(",");
        for (Nodo hijo : nodo.hijos) {
            preorden(hijo, sb);
        }
    }

    private void postorden(Nodo nodo, StringBuilder sb) {
        if (nodo == null) {
            return;
        }

        for (Nodo hijo : nodo.hijos) {
            postorden(hijo, sb);
        }
        sb.append(nodo.registro.getId()).append(",");
    }

    private int altura(Nodo nodo) {
        if (nodo == null) {
            return -1;
        }

        int mayorAltura = -1;
        for (Nodo hijo : nodo.hijos) {
            int alturaHijo = altura(hijo);
            if (alturaHijo > mayorAltura) {
                mayorAltura = alturaHijo;
            }
        }

        return mayorAltura + 1;
    }

    private int contarHojas(Nodo nodo) {
        if (nodo == null) {
            return 0;
        }

        if (nodo.hijos.isEmpty()) {
            return 1;
        }

        int total = 0;
        for (Nodo hijo : nodo.hijos) {
            total += contarHojas(hijo);
        }
        return total;
    }

    private void cancelarDesde(Nodo nodo, int[] contador) {
        if (nodo == null) {
            return;
        }

        if (nodo.registro.getEstado() == Registro.Estado.PENDIENTE) {
            nodo.registro.setEstado(Registro.Estado.CANCELADO);
            contador[0]++;
        }

        for (Nodo hijo : nodo.hijos) {
            cancelarDesde(hijo, contador);
        }
    }

    private Nodo buscarNodoPorId(Nodo nodo, int id) {
        if (nodo == null) {
            return null;
        }

        if (nodo.registro.getId() == id) {
            return nodo;
        }

        for (Nodo hijo : nodo.hijos) {
            Nodo encontrado = buscarNodoPorId(hijo, id);
            if (encontrado != null) {
                return encontrado;
            }
        }

        return null;
    }
}
