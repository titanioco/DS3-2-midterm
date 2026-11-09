package com.ds3midterm;

import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Main {
    private static final ArbolGeneral arbolGeneral = new ArbolGeneral();
    private static final BST bst = new BST();
    private static final AVL avl = new AVL();
    private static final BinaryHeap heap = new BinaryHeap();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while (scanner.hasNextLine()) {
            String linea = scanner.nextLine().trim();
            if (linea.isEmpty()) {
                continue;
            }

            String[] partes = linea.split("\\s+");
            String comando = partes[0].toUpperCase();

            try {
                switch (comando) {
                    case "REGISTRAR":
                        registrar(partes);
                        break;
                    case "CONSULTAR":
                        consultar(partes);
                        break;
                    case "SIGUIENTE":
                        siguiente();
                        break;
                    case "CANCELAR":
                        cancelar(partes);
                        break;
                    case "REPORTE":
                        reporte();
                        break;
                    case "ESTADISTICAS":
                        estadisticas();
                        break;
                    default:
                        System.out.println("Comando desconocido: " + comando);
                }
            } catch (Exception e) {
                System.out.println("Error procesando comando: " + e.getMessage());
            }
        }

        scanner.close();
    }

    private static void registrar(String[] partes) {
        if (partes.length != 5) {
            throw new IllegalArgumentException("Formato inválido. Usa: REGISTRAR id nombre prioridad padre");
        }

        int id = Integer.parseInt(partes[1]);
        String nombre = partes[2];
        int prioridad = Integer.parseInt(partes[3]);
        int padre = Integer.parseInt(partes[4]);

        if (prioridad < 1 || prioridad > 10) {
            throw new IllegalArgumentException("La prioridad debe estar entre 1 y 10.");
        }

        if (avl.existe(id) || bst.existe(id)) {
            throw new IllegalArgumentException("El ID ya existe.");
        }

        if (padre != 0 && !avl.existe(padre) && !arbolGeneral.existeId(padre)) {
            throw new IllegalArgumentException("El padre indicado no existe.");
        }

        Registro registro = new Registro(id, nombre, prioridad, padre);
        arbolGeneral.agregarHijo(registro);
        bst.insertar(registro);
        avl.insertar(registro);
        heap.insertar(registro);
    }

    private static void consultar(String[] partes) {
        if (partes.length != 2) {
            throw new IllegalArgumentException("Formato inválido. Usa: CONSULTAR id");
        }

        int id = Integer.parseInt(partes[1]);
        Registro registro = avl.buscar(id);

        if (registro == null) {
            System.out.println("NO ENCONTRADO");
            return;
        }

        System.out.println(
            registro.getId() + " " +
            registro.getNombre() + " " +
            registro.getPrioridad() + " " +
            registro.getPadre() + " " +
            registro.getEstado()
        );
    }

    private static void siguiente() {
        if (heap.estaVacia()) {
            System.out.println("SIN REGISTROS PENDIENTES");
            return;
        }

        Registro registro = heap.extraerMax();
        registro.setEstado(Registro.Estado.ATENDIDO);
        avl.actualizar(registro);

        System.out.println(registro.getId() + "(" + registro.getPrioridad() + ")");
    }

    private static void cancelar(String[] partes) {
        if (partes.length != 2) {
            throw new IllegalArgumentException("Formato inválido. Usa: CANCELAR id");
        }

        int id = Integer.parseInt(partes[1]);
        int cancelados = arbolGeneral.cancelarSubarbol(id);
        reconstruirHeap();
        System.out.println(cancelados);
    }

    private static void reporte() {
        List<Registro> pendientes = heap.obtenerPendientes();
        if (pendientes.isEmpty()) {
            System.out.println("VACIO");
            return;
        }

        String salida = pendientes.stream()
            .map(registro -> String.valueOf(registro.getId()))
            .collect(Collectors.joining(","));

        System.out.println(salida);
    }

    private static void estadisticas() {
        String preorden = arbolGeneral.imprimirPreorden();
        String postorden = arbolGeneral.imprimirPostorden();
        String niveles = arbolGeneral.imprimirPorNiveles();

        System.out.println("PREORDEN: " + (preorden.isEmpty() ? "VACIO" : preorden));
        System.out.println("POSTORDEN: " + (postorden.isEmpty() ? "VACIO" : postorden));
        System.out.println("NIVELES: " + (niveles.isEmpty() ? "VACIO" : niveles));
        System.out.println("ALTURA: " + arbolGeneral.calcularAltura());
        System.out.println("HOJAS: " + arbolGeneral.contarHojas());
        System.out.println("PENDIENTES: " + avl.contarPendientes());
    }

    private static void reconstruirHeap() {
        heap.reconstruirDesde(avl.obtenerPendientes());
    }
}
