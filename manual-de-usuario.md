# Sistema de alarmas de planta — DS3-2 Midterm

Proyecto de consola en Java para registrar alarmas, relacionarlas mediante una jerarquía y atender primero las de mayor prioridad. Es un ejercicio para practicar clases, objetos, colecciones y estructuras de datos.

## ¿Qué hace el programa?

Cada alarma es un objeto `Registro`. Al registrarla, el programa comparte ese objeto entre varias estructuras: el árbol general organiza las relaciones padre-hijo, el heap prioriza las alarmas pendientes y los índices permiten consultarlas por ID.

## Organización del proyecto

```text
DS3-2-midterm/
├── src/main/java/com/ds3midterm/
│   ├── Main.java
│   ├── Registro.java
│   ├── ArbolGeneral.java
│   ├── BST.java
│   ├── AVL.java
│   └── BinaryHeap.java
├── out/                 # archivos .class generados al compilar
└── README.md
```

La carpeta del proyecto es `DS3-2-midterm`. Para compilarlo, la terminal debe estar ubicada en esa carpeta: no en `DataStructures` ni dentro de `src/main/java/com/ds3midterm`.

### ¿Por qué está organizado así?

- `src/main/java` es una organización común en proyectos Java y ayuda a separar el código fuente de los archivos generados al compilar.
- `com.ds3midterm` es el paquete Java. Su ruta corresponde a las carpetas `com/ds3midterm`.
- Las clases usan `PascalCase`, por ejemplo `BinaryHeap`; las variables y los métodos usan `camelCase`, por ejemplo `prioridad` y `obtenerPendientes`. Son convenciones habituales de Java que hacen más fácil leer y compartir código; no son una regla exclusiva de la web.
- `Main` coordina la interacción; `Registro` representa los datos. Las otras clases separan responsabilidades y permiten estudiar cada estructura.

> **Nota sobre las estructuras:** en esta versión, `BST` y `AVL` guardan los registros con `TreeMap` de Java; todavía no son implementaciones manuales de árboles binarios o AVL. `BinaryHeap` sí mantiene un heap en una lista, y `ArbolGeneral` representa las relaciones entre registros.

## Requisitos

Se necesita el Kit de Desarrollo de Java (JDK), que incluye `javac` y `java`. El proyecto se ha probado con Java 21.

## Compilar y ejecutar en Windows PowerShell

Abre PowerShell en la carpeta `DS3-2-midterm` y ejecuta el bloque de compilación. Cuando Java inicie, escribe los comandos del programa en esa misma terminal, uno por línea.

```powershell
New-Item -ItemType Directory -Force .\out | Out-Null
$sources = Get-ChildItem -Path .\src\main\java -Recurse -Filter *.java | ForEach-Object { $_.FullName }

if (-not $sources) { throw "No se encontraron archivos Java. Verifica que la terminal esté en la carpeta DS3-2-midterm." }

javac -d .\out $sources
if ($LASTEXITCODE -ne 0) { throw "Falló la compilación. Revisa los errores de javac antes de iniciar el programa." }

java -cp .\out com.ds3midterm.Main
```

`javac` compila los archivos `.java` y guarda los `.class` en `out`. La última línea inicia la clase `Main`. Si la compilación falla, corrige primero el error que muestra `javac`; el mensaje del bloque solo avisa que no se inició el programa.

## Comandos disponibles

Escribe un comando por línea. Los nombres de comando no distinguen entre mayúsculas y minúsculas.

| Comando | Uso | Resultado |
|---|---|---|
| `REGISTRAR` | `REGISTRAR id nombre prioridad padre` | Crea una alarma pendiente y la agrega a las estructuras. |
| `CONSULTAR` | `CONSULTAR id` | Muestra ID, nombre, prioridad, padre y estado; o `NO ENCONTRADO`. |
| `SIGUIENTE` | `SIGUIENTE` | Atiende la alarma pendiente de mayor prioridad y muestra `id(prioridad)`. |
| `CANCELAR` | `CANCELAR id` | Cancela las alarmas pendientes del registro indicado y sus descendientes; muestra cuántas canceló. |
| `REPORTE` | `REPORTE` | Muestra los IDs pendientes ordenados por prioridad; muestra `VACIO` si no hay. |
| `ESTADISTICAS` | `ESTADISTICAS` | Muestra recorridos, altura, hojas y cantidad de alarmas pendientes. |

### Reglas para registrar

- `id` y `padre` son números enteros. Cada ID debe ser único.
- `nombre` es una palabra sin espacios.
- `prioridad` es un entero entre 1 y 10; un número mayor significa más urgencia.
- Usa `padre` igual a `0` para crear la raíz. Para los demás registros, el padre debe existir.
- Si dos alarmas tienen la misma prioridad, el ID menor va primero.

## Ejemplo completo

Escribe estas líneas, en orden, cuando el programa esté esperando entrada:

```text
REGISTRAR 100 Caldera 8 0
REGISTRAR 110 SensorA 6 100
REGISTRAR 120 Bomba 9 100
REGISTRAR 130 Valvula 6 110
CONSULTAR 110
REPORTE
SIGUIENTE
CONSULTAR 120
CANCELAR 110
REPORTE
ESTADISTICAS
```

Las salidas relevantes son:

```text
110 SensorA 6 100 PENDIENTE
120,100,110,130
120(9)
120 Bomba 9 100 ATENDIDO
2
100
PREORDEN: 100,110,130,120
POSTORDEN: 130,110,120,100
NIVELES: 100,110,120,130
ALTURA: 2
HOJAS: 2
PENDIENTES: 1
```

En el ejemplo, `SIGUIENTE` atiende la alarma más urgente; después, `CANCELAR 110` cancela el registro 110 y su descendiente 130. Las alarmas canceladas siguen apareciendo en los recorridos del árbol, pero ya no cuentan como pendientes.

Para terminar la entrada en Windows, presiona `Ctrl+Z` y luego `Enter`.

## Tipos de datos que aparecen en el ejemplo

- `int`: IDs, prioridad y padre. Ejemplo: `100` o `8`.
- `String`: nombre. Ejemplo: `Caldera`.
- `Registro.Estado` (enumeración): estado de la alarma, como `PENDIENTE`, `ATENDIDO` o `CANCELADO`.
- `Registro`: objeto que agrupa esos valores para representar una alarma.
- `List<Registro>`: lista de objetos `Registro`, usada, por ejemplo, para consultar las alarmas pendientes.

## Más información

Consulta la [guía de integración de alarmas](./plant_alarms_integration_guide.md) para una explicación conceptual más extensa.