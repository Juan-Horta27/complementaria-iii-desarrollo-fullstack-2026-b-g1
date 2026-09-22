# Evidencia de pruebas - CRUD REST de usuarios

ScoreSound - Semana 8 - Desarrollo Fullstack - CORHUILA

- Fecha de la prueba: 2026-09-21 21:46:09
- API probada: http://localhost:8080/usuarios
- Resultado: **6 de 6 pruebas aprobadas**

## Resumen

| # | Operacion | Metodo | URL | Esperado | Obtenido | Resultado |
|---|---|---|---|---|---|---|
| 1 | Crear | POST | http://localhost:8080/usuarios | 201 | 201 | Paso |
| 2 | Listar | GET | http://localhost:8080/usuarios | 200 | 200 | Paso |
| 3 | Obtener | GET | http://localhost:8080/usuarios/1 | 200 | 200 | Paso |
| 4 | Actualizar | PUT | http://localhost:8080/usuarios/1 | 200 | 200 | Paso |
| 5 | Borrar | DELETE | http://localhost:8080/usuarios/1 | 204 | 204 | Paso |
| 6 | Confirmar | GET | http://localhost:8080/usuarios/1 | 404 | 404 | Paso |

La prueba 6 vuelve a pedir el usuario despues de borrarlo: el 404 confirma que el borrado se hizo de verdad.

## Detalle de cada peticion

### 1. Crear - POST http://localhost:8080/usuarios

Cuerpo enviado:

```json
{"nombre":"Ana Torres","correo":"ana.torres.214607@correo.com"}
```

Codigo esperado: **201** - Codigo obtenido: **201**

Respuesta:

```json
{
    "id":  1,
    "nombre":  "Ana Torres",
    "correo":  "ana.torres.214607@correo.com",
    "fechaRegistro":  "2026-09-21T21:46:08.1839296"
}
```

### 2. Listar - GET http://localhost:8080/usuarios

Codigo esperado: **200** - Codigo obtenido: **200**

Respuesta:

```json
[
    {
        "id":  1,
        "nombre":  "Ana Torres",
        "correo":  "ana.torres.214607@correo.com",
        "fechaRegistro":  "2026-09-21T21:46:08.18393"
    }
]
```

### 3. Obtener - GET http://localhost:8080/usuarios/1

Codigo esperado: **200** - Codigo obtenido: **200**

Respuesta:

```json
{
    "id":  1,
    "nombre":  "Ana Torres",
    "correo":  "ana.torres.214607@correo.com",
    "fechaRegistro":  "2026-09-21T21:46:08.18393"
}
```

### 4. Actualizar - PUT http://localhost:8080/usuarios/1

Cuerpo enviado:

```json
{"nombre":"Ana Maria Torres","correo":"ana.torres.214607@correo.com"}
```

Codigo esperado: **200** - Codigo obtenido: **200**

Respuesta:

```json
{
    "id":  1,
    "nombre":  "Ana Maria Torres",
    "correo":  "ana.torres.214607@correo.com",
    "fechaRegistro":  "2026-09-21T21:46:08.18393"
}
```

### 5. Borrar - DELETE http://localhost:8080/usuarios/1

Codigo esperado: **204** - Codigo obtenido: **204**

Respuesta:

_(sin cuerpo)_

### 6. Confirmar - GET http://localhost:8080/usuarios/1

Codigo esperado: **404** - Codigo obtenido: **404**

Respuesta:

_(sin cuerpo)_
