package com.ds3midterm;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

public class BST {
    // BST de apoyo para mantener el historial por ID, aunque el proyecto usa AVL para búsquedas rápidas.
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

    public List<Registro> obtenerTodos() {
        return new ArrayList<>(registros.values());
    }
}
