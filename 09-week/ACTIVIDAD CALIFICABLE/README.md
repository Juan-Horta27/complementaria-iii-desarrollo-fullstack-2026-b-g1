# API REST de usuarios · ScoreSound

Actividad calificable · Corte 2 · Desarrollo Fullstack · CORHUILA

API REST para el recurso **usuarios** de ScoreSound, construida con
Spring Boot en cuatro capas (entity, repository, service y controller)
y con los datos guardados en una base de datos mediante JPA. Junta lo
trabajado entre las semanas 6 y 9: el diseño en capas, la entity y el
repository, el CRUD completo y la documentación con Swagger.

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

El `id` y la `fechaRegistro` los asigna el servidor. El `correo` no se
puede repetir entre usuarios.

## API reference

This API manages the users of ScoreSound, and all of its endpoints live under `/usuarios`. Requests and responses use JSON. To see everyone who is registered, `GET /usuarios` returns the full list with status 200. If you only need one person, `GET /usuarios/{id}` returns that user, or status 404 when the id does not exist. New users are created with `POST /usuarios`, which takes a name and an email in the body and answers with status 201 and the saved user. `PUT /usuarios/{id}` changes the name and the email of an existing user and returns the updated data, or 404 if there is no such user. Finally, `DELETE /usuarios/{id}` removes a user and answers with status 204 and an empty body, or 404 if the user does not exist. The id and the registration date are always set by the server, so the client never sends them. The same endpoints can be tried from the interactive documentation at `http://localhost:8080/swagger-ui.html`.

## Cómo está organizado

```
scoresound-api/src/main/java/com/corhuila/scoresound/
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

En una terminal, dentro de la carpeta `scoresound-api`:

```
.\mvnw.cmd spring-boot:run
```

La primera vez tarda unos minutos porque descarga las dependencias.
Cuando en la consola aparezca `Started ScoresoundApplication`, la API
está escuchando en `http://localhost:8080`.

La base de datos es **H2 en modo archivo**: se crea sola la primera vez
y guarda los datos en la carpeta `data/`, así que los usuarios siguen
ahí aunque se apague y se vuelva a encender la aplicación. No hay que
instalar nada. Para empezar de cero basta con borrar esa carpeta con la
API apagada.

## Documentación con Swagger

Con la API corriendo, la documentación interactiva queda en:

```
http://localhost:8080/swagger-ui.html
```

Ahí aparecen los cinco endpoints con lo que recibe y devuelve cada
uno, y se pueden probar desde el mismo navegador con el botón
*Try it out*.

La descripción en formato OpenAPI, que es la que consumen otras
herramientas como Postman, está en `/v3/api-docs`.

Nada de eso se escribió a mano: springdoc lee los controllers y lo
genera solo, así que la documentación no se desactualiza cuando el
código cambia.

## Cómo probarlo

Con la API corriendo, los endpoints se pueden probar desde Swagger
(`http://localhost:8080/swagger-ui.html`) o desde Postman. Las pruebas
hechas con Postman están en `scoresound-api/reporte-codigos-http.md`, con las capturas
de la carpeta `scoresound-api/evidencias/`: crear (201), listar (200), obtener por id
(200) y un caso de error (404), junto con la captura de Swagger y la
interpretación de cada código.
