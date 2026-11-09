package com.ds3midterm;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class BinaryHeap {
    // Este heap mantiene en la raíz la alarma con mayor prioridad.
    // Sirve como "sala de emergencias" donde el operador atiende primero lo más urgente.
    private final List<Registro> heap = new ArrayList<>();

    public void insertar(Registro registro) {
        if (registro == null) {
            throw new IllegalArgumentException("El registro no puede ser nulo.");
        }
        if (registro.getEstado() == Registro.Estado.PENDIENTE) {
            heap.add(registro);
            subir(heap.size() - 1);
        }
    }

    public Registro extraerMax() {
        if (heap.isEmpty()) {
            throw new IllegalStateException("El heap está vacío.");
        }

        Registro maximo = heap.get(0);
        Registro ultimo = heap.remove(heap.size() - 1);

        if (!heap.isEmpty()) {
            heap.set(0, ultimo);
            bajar(0);
        }

        return maximo;
    }

    public boolean estaVacia() {
        return heap.isEmpty();
    }

    public void reconstruirDesde(List<Registro> registros) {
        heap.clear();
        for (Registro registro : registros) {
            if (registro.getEstado() == Registro.Estado.PENDIENTE) {
                heap.add(registro);
            }
        }
        for (int i = heap.size() / 2 - 1; i >= 0; i--) {
            bajar(i);
        }
    }

    public List<Registro> obtenerPendientes() {
        List<Registro> copia = new ArrayList<>(heap);
        copia.sort(comparator());
        return copia;
    }

    public List<Registro> heapSort() {
        List<Registro> copia = new ArrayList<>(heap);
        List<Registro> ordenado = new ArrayList<>();

        while (!copia.isEmpty()) {
            Registro mayor = obtenerMayor(copia);
            ordenado.add(mayor);
            copia.remove(mayor);
        }

        return ordenado;
    }

    private static Comparator<Registro> comparator() {
        return Comparator
                .comparingInt(Registro::getPrioridad)
                .reversed()
                .thenComparingInt(Registro::getId);
    }

    private static Registro obtenerMayor(List<Registro> registros) {
        Registro mayor = registros.get(0);
        for (Registro registro : registros) {
            if (comparator().compare(registro, mayor) > 0) {
                mayor = registro;
            }
        }
        return mayor;
    }

    private void subir(int indice) {
        while (indice > 0) {
            int padre = (indice - 1) / 2;
            if (comparator().compare(heap.get(indice), heap.get(padre)) <= 0) {
                break;
            }

            Registro temporal = heap.get(indice);
            heap.set(indice, heap.get(padre));
            heap.set(padre, temporal);
            indice = padre;
        }
    }

    private void bajar(int indice) {
        int tam = heap.size();
        while (true) {
            int hijoIzq = 2 * indice + 1;
            int hijoDer = 2 * indice + 2;
            int mayor = indice;

            if (hijoIzq < tam && comparator().compare(heap.get(hijoIzq), heap.get(mayor)) > 0) {
                mayor = hijoIzq;
            }

            if (hijoDer < tam && comparator().compare(heap.get(hijoDer), heap.get(mayor)) > 0) {
                mayor = hijoDer;
            }

            if (mayor == indice) {
                break;
            }

            Registro temporal = heap.get(indice);
            heap.set(indice, heap.get(mayor));
            heap.set(mayor, temporal);
            indice = mayor;
        }
    }
}
