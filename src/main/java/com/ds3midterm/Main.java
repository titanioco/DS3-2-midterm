package com.ds3midterm;

import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Main {
    // Se mantiene una única colección de datos por estructura, pero todas apuntan al mismo objeto Registro.
    // Esto es clave para no duplicar información y mantener la consistencia del estado.
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
                        // O1: crear la alarma y guardarla en todas las estructuras.
                        registrar(partes);
                        break;
                    case "CONSULTAR":
                        // O2: buscar por ID de manera rápida usando AVL.
                        consultar(partes);
                        break;
                    case "SIGUIENTE":
                        // O3: extraer la alarma más urgente del heap.
                        siguiente();
                        break;
                    case "CANCELAR":
                        // O4: cancelar un subárbol y limpiar el heap.
                        cancelar(partes);
                        break;
                    case "REPORTE":
                        // O5: mostrar las alarmas pendientes ordenadas por prioridad.
                        reporte();
                        break;
                    case "ESTADISTICAS":
                        // O6: mostrar métricas del árbol y del sistema.
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

        // Validación de la prioridad según el dominio del problema.
        if (prioridad < 1 || prioridad > 10) {
            throw new IllegalArgumentException("La prioridad debe estar entre 1 y 10.");
        }

        // No se permiten IDs duplicados. El AVL y el BST sirven como índices.
        if (avl.existe(id) || bst.existe(id)) {
            throw new IllegalArgumentException("El ID ya existe.");
        }

        // Si el padre no es la raíz (0), debe existir en la jerarquía.
        if (padre != 0 && !avl.existe(padre) && !arbolGeneral.existeId(padre)) {
            throw new IllegalArgumentException("El padre indicado no existe.");
        }

        // Se crea una sola instancia de Registro para mantener la consistencia del estado.
        Registro registro = new Registro(id, nombre, prioridad, padre);

        // Se registra la misma referencia en todas las estructuras.
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

        // El heap extrae la alarma más urgente y la marca como atendida.
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

        // Como el heap no permite eliminación eficiente por ID, se reconstruye
        // con solo las alarmas que sigan en estado PENDIENTE.
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
