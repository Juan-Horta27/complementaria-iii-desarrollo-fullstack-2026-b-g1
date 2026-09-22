# CRUD REST de usuarios · ScoreSound

Semana 8 · Corte 2 · Desarrollo Fullstack · CORHUILA

API REST para el recurso **usuarios** de ScoreSound, construida con
Spring Boot en cuatro capas: entity, repository, service y controller.
La entity y el repository son los mismos que se modelaron en la
semana 7; esta semana se agregan el service y el controller y se deja
todo funcionando.

## Endpoints

| Acción | Método | URL | Respuesta |
|---|---|---|---|
| Listar | `GET` | `/usuarios` | 200 con la lista |
| Obtener | `GET` | `/usuarios/{id}` | 200, o 404 si no existe |
| Crear | `POST` | `/usuarios` | 201 con el usuario creado |
| Actualizar | `PUT` | `/usuarios/{id}` | 200, o 404 si no existe |
| Borrar | `DELETE` | `/usuarios/{id}` | 204, o 404 si no existe |

Para crear o actualizar se envía un JSON así:

```json
{ "nombre": "Ana Torres", "correo": "ana.torres@correo.com" }
```

El `id` y la `fechaRegistro` los asigna el servidor.

## Cómo está organizado

```
src/main/java/com/corhuila/scoresound/
 ├─ controller/UsuarioController.java   recibe la petición y responde
 ├─ service/UsuarioService.java         lógica de negocio
 ├─ repository/UsuarioRepository.java   acceso a datos
 ├─ entity/Usuario.java                 se mapea a la tabla "usuarios"
 └─ ScoresoundApplication.java          arranca la aplicación
```

El controller solo habla con el service, y el service es el único que
usa el repository. El controller no toca la base de datos ni tiene
reglas; su trabajo es recibir, delegar y traducir el resultado a un
código HTTP.

En el service quedan dos decisiones del negocio: al crear, la fecha de
registro la pone el servidor, y se anula cualquier `id` que venga en
el JSON para que un `POST` nunca termine sobrescribiendo un registro
existente. Al actualizar, solo cambian el nombre y el correo.

## Cómo ejecutarlo

Solo hace falta **Java 17 o superior**. Maven no hay que instalarlo:
viene incluido el Maven Wrapper, que lo descarga la primera vez.

En una terminal, dentro de esta carpeta:

```
.\mvnw.cmd spring-boot:run
```

La primera vez tarda unos minutos porque descarga las dependencias.
Cuando en la consola aparezca `Started ScoresoundApplication`, la API
está escuchando en `http://localhost:8080`.

La base de datos es **H2 en memoria**: se crea sola al arrancar y se
borra al apagar la aplicación. No hay que instalar nada.

## Cómo probarlo

Con la API corriendo, abre **otra** terminal en esta misma carpeta y
ejecuta:

```
powershell -ExecutionPolicy Bypass -File .\probar-crud.ps1
```

El script recorre las cinco operaciones en orden: crea un usuario, lo
busca en la lista, lo obtiene por su id, le cambia el nombre, lo borra
y al final lo vuelve a pedir para confirmar que ya no existe (404).

La evidencia queda en **`evidencia-pruebas.md`**, con el código de
estado esperado y el obtenido en cada paso, el JSON enviado y la
respuesta completa del servidor.
