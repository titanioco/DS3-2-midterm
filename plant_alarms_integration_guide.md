# Plant Alarm Integration Guide

This guide explains how the alarm system works in this project and how the code pieces fit together in a simple, step-by-step way.

## 1. What the project is doing

The application simulates an alarm system for a plant. Every alarm is represented by a `Registro` object. That object stores:

- the alarm ID
- the equipment / area name
- the priority from 1 to 10
- the parent ID in the plant hierarchy
- the current state: `PENDIENTE`, `ATENDIDO`, or `CANCELADO`

The important idea is that the same `Registro` object is stored in multiple structures at the same time:

- `ArbolGeneral` for the physical plant hierarchy
- `AVL` for fast lookup by ID
- `BST` as a backup registry/history structure
- `BinaryHeap` for emergency priority handling

This is called the “single truth” rule: one object, many views.

## 2. The key object: Registro

`Registro` is the heart of the program.

```java
public class Registro implements Comparable<Registro> {
    public enum Estado { PENDIENTE, ATENDIDO, CANCELADO }

    private int id;
    private String nombre;
    private int prioridad;
    private int padre;
    private Estado estado;
}
```

Every time an alarm is created, it starts as `PENDIENTE`.

```java
public Registro(int id, String nombre, int prioridad, int padre) {
    this.id = id;
    this.nombre = nombre;
    this.prioridad = prioridad;
    this.padre = padre;
    this.estado = Estado.PENDIENTE;
}
```

The sorting rule is:

- higher priority comes first
- if priorities are equal, lower ID comes first

```java
@Override
public int compareTo(Registro otro) {
    int prioridadCmp = Integer.compare(otro.prioridad, this.prioridad);
    if (prioridadCmp != 0) {
        return prioridadCmp;
    }
    return Integer.compare(this.id, otro.id);
}
```

This matters because the heap always needs to know which alarm is most urgent.

## 3. How alarms are stored in the plant tree

The hierarchy of the plant is represented by `ArbolGeneral`.

Each node in the tree stores:

- the `Registro` object
- a list of child nodes

Example structure:

```text
100 (Planta Principal)
├── 10 (Turbina)
│   ├── 11 (Sensor de Presión)
│   └── 12 (Sensor de Temperatura)
└── 20 (Caldera)
    └── 21 (Válvula)
```

The tree is not a BST; it is a general n-ary tree. A node can have many children, and the parent/child relation is based on the `padre` field in the `Registro` object.

When a new alarm is registered:

```java
Registro registro = new Registro(id, nombre, prioridad, padre);
arbolGeneral.agregarHijo(registro);
```

Inside `agregarHijo`, the code looks for the parent ID in the tree, and if that parent exists, the alarm is attached as a child.

If the parent is `0`, the alarm becomes the root of the tree.

## 4. Why the AVL exists

The AVL is used for fast lookup by ID.

This is the structure used when someone types:

```text
CONSULTAR 21
```

The program does:

```java
Registro registro = avl.buscar(id);
```

The AVL is good for this because it keeps the data organized by ID and gives quick access.

The project also uses a BST as a registry-like structure, but the main fast search path is the AVL.

## 5. Why the heap exists

The max-heap is used as the emergency queue. It always keeps the most urgent alarm on top.

The rule is:

- higher priority wins
- if tie, lower ID wins

So when the operator asks for the next alarm:

```text
SIGUIENTE
```

the code does:

```java
Registro registro = heap.extraerMax();
registro.setEstado(Registro.Estado.ATENDIDO);
```

That means:

- the top alarm is taken from the heap
- it becomes `ATENDIDO`
- the system updates the AVL too

The heap is not the place where the entire plant structure is stored; it is only the queue of pending emergencies.

## 6. What happens during registration step by step

This is the main user flow.

Command example:

```text
REGISTRAR 10 Bomba 8 0
```

Meaning:

- `id = 10`
- `nombre = Bomba`
- `prioridad = 8`
- `padre = 0` (root alarm)

The code validates:

- priority must be between 1 and 10
- the ID must not already exist
- the parent must exist if `padre != 0`

Then:

```java
Registro registro = new Registro(10, "Bomba", 8, 0);
```

Then the exact same object is inserted into all structures:

```java
arbolGeneral.agregarHijo(registro);
bst.insertar(registro);
avl.insertar(registro);
heap.insertar(registro);
```

This is critical. The project is designed so all views are pointing to the same in-memory alarm object, not copies.

## 7. Baby-step example

Suppose the plant starts with this hierarchy:

```text
REGISTRAR 1 Planta 5 0
REGISTRAR 2 Caldera 7 1
REGISTRAR 3 Bomba 9 1
REGISTRAR 4 Sensor 4 2
```

After these commands:

- `1` is the root alarm
- `2` and `3` are children of `1`
- `4` is a child of `2`

The tree structure becomes:

```text
1
├── 2
│   └── 4
└── 3
```

The heap sees these pending alarms:

- `3` with priority 9
- `2` with priority 7
- `1` with priority 5
- `4` with priority 4

So the next alarm to attend is `3`.

If the user calls:

```text
SIGUIENTE
```

then `3` becomes `ATENDIDO` and is removed from the pending queue.

## 8. How cancellation works

The tree can cancel a whole branch. Example:

```text
CANCELAR 2
```

This calls:

```java
int cancelados = arbolGeneral.cancelarSubarbol(2);
```

The method finds node `2` and walks all descendants. Each alarm that is still `PENDIENTE` changes to `CANCELADO`.

Example:

- node `2` is cancelled
- node `4` is also cancelled if it was pending
- node `3` is not cancelled because it is not in that subtree

The heap is then rebuilt so that canceled alarms are removed from the emergency queue.

## 9. How the report works

The command:

```text
REPORTE
```

shows the alarms still pending, ordered by priority.

The system does:

```java
List<Registro> pendientes = heap.obtenerPendientes();
```

This is a copy of the heap, sorted for display, while keeping the original heap untouched.

## 10. Statistics and traversals

The command:

```text
ESTADISTICAS
```

prints tree information like:

- preorder traversal
- postorder traversal
- level-order traversal
- tree height
- number of leaves
- number of pending alarms

For example:

```text
PREORDEN: 1,2,4,3
POSTORDEN: 4,2,3,1
NIVELES: 1,2,3,4
ALTURA: 2
HOJAS: 2
PENDIENTES: 3
```

## 11. Complete flow in one sequence

A full realistic flow can be:

```text
REGISTRAR 1 Planta 5 0
REGISTRAR 2 Caldera 7 1
REGISTRAR 3 Bomba 9 1
REGISTRAR 4 Sensor 4 2
CONSULTAR 3
SIGUIENTE
CANCELAR 2
REPORTE
ESTADISTICAS
```

This means:

1. create the plant alarms
2. look up one by ID
3. process the highest-priority alarm
4. cancel the entire branch under `2`
5. show remaining pending alarms
6. display overall tree statistics

## 12. Current state of the code

This repository already implements the main integration pattern described above:

- `Registro` stores alarm data
- `ArbolGeneral` handles hierarchy and cancellation
- `AVL` handles quick lookup by ID
- `BinaryHeap` handles emergency priority ordering
- `Main` orchestrates all commands from console input

The code now behaves as a working backbone that follows the architecture required by the exam and the alarm integration workflow.

## 13. Final idea

The backbone is built around one principle:

- the alarm is one object
- the plant tree shows where it belongs physically
- the AVL finds it fast
- the heap chooses the next alarm to process

That is exactly how this system stays organized and efficient.
