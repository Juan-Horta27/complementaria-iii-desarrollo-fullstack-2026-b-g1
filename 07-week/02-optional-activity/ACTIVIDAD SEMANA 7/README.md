# Capa de datos · Entity y repository (JPA) · ScoreSound

Semana 7 · Corte 2 · Desarrollo Fullstack · CORHUILA

## El caso

ScoreSound es una app para músicos que practican solos: se sube el PDF
de una partitura, la app lo interpreta y lo reproduce, y trae un
metrónomo integrado. Todo queda en una biblioteca personal.

Este documento modela la capa de datos de ese backend: las entities que
se mapean a tablas y los repositories que las consultan. Es la
continuación de la arquitectura en capas de la semana 6.

## Archivos

```
entity/
 ├─ Usuario.java              se mapea a la tabla "usuarios"
 └─ Partitura.java            se mapea a la tabla "partituras"
repository/
 ├─ UsuarioRepository.java    acceso a datos de usuarios
 └─ PartituraRepository.java  acceso a datos de partituras
```

## El mapeo de `Partitura`

Cada atributo de la clase es una columna de la tabla, y cada fila de la
tabla se convierte en un objeto `Partitura`.

| Atributo Java | Tipo | Columna | Anotación relevante |
|---|---|---|---|
| `id` | `Long` | `id` | `@Id` + `@GeneratedValue` |
| `titulo` | `String` | `titulo` | obligatorio, 150 caracteres |
| `compositor` | `String` | `compositor` | opcional |
| `instrumento` | `String` | `instrumento` | obligatorio |
| `tonalidad` | `String` | `tonalidad` | opcional |
| `rutaArchivo` | `String` | `ruta_archivo` | `@Column(name=...)` |
| `procesada` | `boolean` | `procesada` | obligatorio |
| `fechaSubida` | `LocalDateTime` | `fecha_subida` | `@Column(name=...)` |
| `usuario` | `Usuario` | `usuario_id` | `@ManyToOne` + `@JoinColumn` |

Tres decisiones del modelo que vale la pena explicar:

**El PDF no se guarda en la base de datos.** Solo se guarda su ruta en
el servidor. Meter archivos binarios en una tabla la vuelve pesada y
lenta; con la ruta basta para ir a buscarlo cuando haga falta.

**`procesada` existe por una regla del negocio.** Convertir un PDF a
notas puede fallar. Cuando eso pasa, la partitura se guarda igual con
este campo en `false`, para que el usuario no pierda el archivo que
subió y se pueda reintentar la conversión más tarde.

**Los nombres de columna con guion bajo.** Java usa `rutaArchivo` y las
bases de datos relacionales suelen usar `ruta_archivo`. La anotación
`@Column(name = "ruta_archivo")` hace la traducción.

## La relación entre las dos entities

Un usuario tiene muchas partituras; cada partitura pertenece a un solo
usuario. Por eso en `Partitura` va `@ManyToOne`:

```java
@ManyToOne(fetch = FetchType.LAZY, optional = false)
@JoinColumn(name = "usuario_id", nullable = false)
private Usuario usuario;
```

En la tabla `partituras` eso se traduce en una columna `usuario_id` que
apunta a la tabla `usuarios`: una llave foránea.

Se usa `FetchType.LAZY` para que al listar la biblioteca no se traiga
también el usuario completo en cada fila. `optional = false` significa
que no puede existir una partitura sin dueño.

## Los repositories

Los dos son **interfaces**, no clases. No se escribe la implementación:
Spring la genera al arrancar la aplicación. Al extender `JpaRepository`
ya vienen listos `save()`, `findAll()`, `findById()`, `deleteById()`,
`count()` y `existsById()`, sin una línea de SQL.

Los dos tipos entre `<>` son la entity que maneja y el tipo de su clave
primaria. En ambos casos: `<Partitura, Long>` y `<Usuario, Long>`.

### Consultas declaradas en `PartituraRepository`

```java
List<Partitura> findByUsuarioIdOrderByFechaSubidaDesc(Long usuarioId);
boolean existsByTituloAndUsuarioId(String titulo, Long usuarioId);
List<Partitura> findByTituloContainingIgnoreCase(String texto);
List<Partitura> findByInstrumentoAndProcesadaTrue(String instrumento);
```

Ninguna tiene cuerpo: son declaraciones. Spring lee el nombre del
método como si fuera una frase y escribe la consulta.

| Método | Cómo lo interpreta Spring | Para qué sirve en la app |
|---|---|---|
| `findByUsuarioIdOrderByFechaSubidaDesc` | Navega de la partitura a su usuario, compara el id y ordena por fecha descendente | Llenar la biblioteca al entrar, con lo último arriba |
| `existsByTituloAndUsuarioId` | Busca coincidencia de título y usuario, devuelve sí o no | Evitar que se suba dos veces la misma partitura |
| `findByTituloContainingIgnoreCase` | Título que contenga el texto, sin distinguir mayúsculas | El buscador de la biblioteca |
| `findByInstrumentoAndProcesadaTrue` | Filtra por instrumento y solo las ya convertidas | Ver solo lo que se puede reproducir |

Fíjate en `findByUsuarioId`: `usuario` es un objeto, no un número, pero
Spring entiende que debe entrar a ese objeto y comparar su `id`. Esa
navegación es gratis gracias a la relación.

### Consultas de `UsuarioRepository`

```java
Optional<Usuario> findByCorreo(String correo);
boolean existsByCorreo(String correo);
```

La primera devuelve `Optional` porque ese correo puede no existir. Es
una forma de obligar a quien la use a contemplar ese caso en vez de
arriesgarse a un error en tiempo de ejecución.

## Operaciones CRUD que necesita ScoreSound

### Create · registrar algo nuevo

Se usa `save()` **sin id**. Al no traer clave primaria, JPA entiende
que es un registro nuevo y ejecuta un `INSERT`.

- `partituraRepository.save(partitura)` cuando el usuario sube un PDF.
  Antes de llamarlo, el service ya verificó con
  `existsByTituloAndUsuarioId` que no esté repetida.
- `usuarioRepository.save(usuario)` al registrar una cuenta, después de
  comprobar con `existsByCorreo` que ese correo esté libre.

### Read · consultar

- `findByUsuarioIdOrderByFechaSubidaDesc` para pintar la biblioteca.
- `findById` para abrir una partitura y reproducirla.
- `findByTituloContainingIgnoreCase` para el buscador.
- `findByInstrumentoAndProcesadaTrue` para el filtro por instrumento.
- `findByCorreo` al iniciar sesión.

Es la operación más frecuente de la app: se lee muchas más veces de las
que se escribe.

### Update · modificar

Se usa **el mismo `save()`**, pero con el objeto **con id**. JPA ve que
ya tiene clave primaria, busca esa fila y ejecuta un `UPDATE`.

- Corregir el título o el instrumento de una partitura mal catalogada.
- Poner `procesada = true` cuando la conversión del PDF termina bien.
- Actualizar el nombre de la cuenta del usuario.

### Delete · eliminar

- `deleteById(id)` cuando el usuario saca una partitura de su
  biblioteca. El service se encarga además de borrar el PDF del disco,
  porque el repository solo sabe de la base de datos.

### Resumen

| Letra | Operación | Método | Ejemplo en ScoreSound |
|---|---|---|---|
| **C** | Create | `save()` sin id | Subir una partitura nueva |
| **R** | Read | `findAll`, `findById`, consultas propias | Cargar la biblioteca |
| **U** | Update | `save()` con id | Marcarla como procesada |
| **D** | Delete | `deleteById(id)` | Sacarla de la biblioteca |

El detalle que conviene tener claro: **`save()` cubre la C y la U**. No
hay un método `update` aparte. La diferencia la hace el id: sin id
inserta, con id actualiza.

## Quién usa estos repositories

Nunca el controller. Siempre el **service**, tal como quedó definido en
la arquitectura de la semana 6:

```
Controller  ->  Service  ->  Repository  ->  Entity  ->  Base de datos
```

El repository solo ejecuta consultas; las reglas (que el archivo sea
PDF, que no haya duplicados, qué hacer si falla la conversión) viven en
el service.
