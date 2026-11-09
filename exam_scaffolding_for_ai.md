# Codex / AI Prompting Guide for Examen 02-ABP-IA

**Context for AI Model:**
We are building a Java 21 application based on the specifications of an exam ("DSX_02_Examen2 -ABPr.pdf"). I am the "Líder de Integración y Árbol General". My responsibility is to create the base `Registro` class, the `ArbolGeneral` data structure, and the `Main` integration class that connects an external BST, AVL, and Max-Heap. 

Below are the base scaffolds. Please complete the `TODO` sections exactly as specified by the rules.

## 1. Registro.java
**Rules:** Must store id, nombre, prioridad (1-10), padre, and estado (PENDIENTE, ATENDIDO, CANCELADO). Tie-breaking for priority: Higher priority first; if equal, lower ID first.

```java
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

    // TODO (AI): Generate Getters and Setters

    @Override
    public int compareTo(Registro otro) {
        // TODO (AI): Implement comparison logic. 
        // 1. Compare 'prioridad' (descending - higher is better)
        // 2. If 'prioridad' is equal, compare 'id' (ascending - lower is better)
        return 0; // Replace with actual logic
    }
    
    @Override
    public String toString() {
        return id + "(" + prioridad + ")";
    }
}
```

## 2. ArbolGeneral.java
**Rules:** General tree (n-ary). Must implement traversals, height, leaf count, and a cascading cancellation that only affects PENDIENTE nodes.

```java
import java.util.*;

public class ArbolGeneral {
    
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

    // TODO (AI): Implement 'agregarHijo(Registro registro)'. 
    // If padre == 0, it's the root. Otherwise, find the parent by ID and add to its 'hijos' list.

    // TODO (AI): Implement 'imprimirPreorden()', 'imprimirPostorden()', 'imprimirPorNiveles()'.
    // Output should be comma-separated IDs as shown in the exam case A.

    // TODO (AI): Implement 'calcularAltura()'. Height of empty is -1, single node is 0. Height = longest path of edges to a leaf.

    // TODO (AI): Implement 'contarHojas()'.

    // TODO (AI): Implement 'cancelarSubarbol(int id)'.
    // 1. Find the node with the given ID.
    // 2. Traverse the node and all descendants.
    // 3. IF a descendant's state is PENDIENTE, change to CANCELADO and increment a counter.
    // 4. Return the total number of canceled nodes.
}
```

## 3. Main.java (Integration)
**Rules:** Read commands from standard input. Handle O1 to O6. Validate O1 strictly. 

```java
import java.util.Scanner;

public class Main {
    // Assuming teammates have built these classes
    private static ArbolGeneral arbolGeneral = new ArbolGeneral();
    // private static BST bst = new BST();
    // private static AVL avl = new AVL();
    // private static BinaryHeap heap = new BinaryHeap();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while (scanner.hasNextLine()) {
            String linea = scanner.nextLine().trim();
            if (linea.isEmpty()) continue;

            String[] partes = linea.split(" ");
            String comando = partes[0].toUpperCase();

            try {
                switch (comando) {
                    case "REGISTRAR":
                        // TODO (AI): Implement O1. 
                        // Parse id, nombre, prioridad, padre.
                        // Validate: Priority between 1-10? Does ID already exist in AVL? Does parent exist (if padre != 0)?
                        // If valid, create ONE instance of 'Registro', pass it to Tree, BST, AVL, and Heap.
                        break;
                    case "CONSULTAR":
                        // TODO (AI): Implement O2. Find in AVL and print details.
                        break;
                    case "SIGUIENTE":
                        // TODO (AI): Implement O3. Extract max from Heap, change state to ATENDIDO.
                        break;
                    case "CANCELAR":
                        // TODO (AI): Implement O4. Call arbolGeneral.cancelarSubarbol(id). 
                        // IMPORTANT: Rebuild the Heap with the remaining PENDIENTE nodes in the AVL to synchronize state.
                        break;
                    case "REPORTE":
                        // TODO (AI): Implement O5. Pass a COPY of the heap's pending items to HeapSort and print.
                        break;
                    case "ESTADISTICAS":
                        // TODO (AI): Implement O6. Print traversals, heights, leaves, and pending count.
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
}
```