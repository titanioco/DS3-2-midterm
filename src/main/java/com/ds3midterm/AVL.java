package com.ds3midterm;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

public class AVL {
    // AVL simple basado en TreeMap.
    // Su objetivo es consultar por ID de forma muy rápida, usando el orden natural de enteros.
    private final TreeMap<Integer, Registro> registros = new TreeMap<>();

    public void insertar(Registro registro) {
        if (registro == null) {
            throw new IllegalArgumentException("El registro no puede ser nulo.");
        }
        registros.put(registro.getId(), registro);
    }

    public boolean existe(int id) {
        return registros.containsKey(id);
    }

    public Registro buscar(int id) {
        return registros.get(id);
    }

    public void actualizar(Registro registro) {
        if (registro == null) {
            throw new IllegalArgumentException("El registro no puede ser nulo.");
        }
        registros.put(registro.getId(), registro);
    }

    public List<Registro> obtenerTodos() {
        return new ArrayList<>(registros.values());
    }

    public List<Registro> obtenerPendientes() {
        List<Registro> pendientes = new ArrayList<>();
        for (Registro registro : registros.values()) {
            if (registro.getEstado() == Registro.Estado.PENDIENTE) {
                pendientes.add(registro);
            }
        }
        return pendientes;
    }

    public int contarPendientes() {
        return obtenerPendientes().size();
    }
}
