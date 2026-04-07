# Reto #5 — Pruebas Funcionales con MongoDB

## Descripción

Este reto consiste en ejecutar pruebas funcionales completas del sistema de biblioteca usando **MongoDB Atlas** como base de datos NoSQL, demostrando que el perfil `mongo` está activo y que todos los datos se persisten correctamente en la nube.

---

## Requisitos previos

Antes de iniciar las pruebas, asegúrate de:

- Tener el proyecto corriendo con el perfil `mongo` activo (verificable en la consola de IntelliJ con el mensaje `The following 1 profile is active: "mongo"`)
- Tener conexión a internet (MongoDB Atlas está en la nube)
- Tener **MongoDB Compass** instalado y conectado al cluster

---

## Configuración de Swagger para JWT

Para que los endpoints protegidos funcionen desde Swagger, el proyecto incluye la clase `OpenApiConfig.java` ubicada en:

```
src/main/java/edu/eci/dosw/DOSW_Library/config/OpenApiConfig.java
```

Esta clase registra el esquema `bearerAuth` para que el botón **Authorize 🔒** aparezca en Swagger UI y se envíe el token JWT automáticamente en cada request.

```java
@OpenAPIDefinition(
    info = @Info(title = "DOSW Library API", version = "1.0"),
    security = @SecurityRequirement(name = "bearerAuth")
)
@SecurityScheme(
    name = "bearerAuth",
    scheme = "bearer",
    type = SecuritySchemeType.HTTP,
    bearerFormat = "JWT",
    in = SecuritySchemeIn.HEADER
)
public class OpenApiConfig { }
```

---

## Cómo correr el proyecto

Desde la raíz del proyecto en PowerShell o terminal:

```bash
./mvnw clean spring-boot:run
```

Espera hasta ver en consola:

```
The following 1 profile is active: "mongo"
Started DoswLibraryApplication in X seconds
```

Eso confirma que MongoDB está activo y la app está corriendo en `http://localhost:8080`.

---

## Acceso a Swagger UI

Una vez corriendo la app, abre en el navegador:

```
http://localhost:8080/swagger-ui/index.html
```

Deberías ver la interfaz con todos los endpoints y el botón **Authorize 🔒** en la esquina superior derecha.

> ⚠️ Si no ves el botón Authorize, verifica que la clase `OpenApiConfig.java` existe y reinicia la aplicación.

---

## Paso a paso — Pruebas funcionales

### PASO 1 — Registrar usuario LIBRARIAN

1. En Swagger, busca la sección **auth-controller**
2. Clic en `POST /api/auth/register`
3. Clic en **"Try it out"**
4. Reemplaza el contenido del campo con:

```json
{
  "name": "carlos uribe",
  "email": "carlos@email.com",
  "username": "carlos0869",
  "password": "123456",
  "role": "LIBRARIAN"
}
```

5. Clic en **"Execute"**
6. Respuesta esperada: **`201 Created`** con el token generado ✅

> ℹ️ Si obtienes `400 Username already exists`, el usuario ya fue registrado en una ejecución anterior — MongoDB conservó el dato. Pasa directamente al Paso 2.

---

### PASO 2 — Login

1. Clic en `POST /api/auth/login`
2. Clic en **"Try it out"**
3. Escribe:

```json
{
  "username": "carlos0869",
  "password": "123456"
}
```

4. Clic en **"Execute"**
5. Respuesta esperada: **`200 OK`** con el token JWT ✅

```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJjYXJsb3MwODY5..."
}
```

6. **Copia el token completo** (el texto largo entre comillas, sin las comillas)

---

### PASO 3 — Autorizar en Swagger

1. Arriba a la derecha, clic en el botón **"Authorize 🔒"**
2. Se abre un popup que dice **"Available authorizations"**
3. En el campo **Value**, escribe `Bearer ` seguido del token copiado — exactamente así:

```
Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJjYXJsb3MwODY5...
```

> ⚠️ Asegúrate de escribir `Bearer ` con **espacio** antes del token. Sin el espacio, el sistema rechazará las peticiones con `403`.

4. Clic en **"Authorize"**
5. Clic en **"Close"**

El candado 🔒 debe aparecer **cerrado** tanto en el botón de arriba como al lado de cada endpoint. Eso confirma que el token se enviará automáticamente.

---

### PASO 4 — Crear un libro

1. Busca la sección **book-controller**
2. Clic en `POST /api/books`
3. Clic en **"Try it out"**
4. Escribe:

```json
{
  "title": "Clean Code",
  "author": "Robert Martin",
  "isbn": "978-0132350884",
  "totalStock": 5,
  "availableStock": 5
}
```

5. Clic en **"Execute"**
6. Respuesta esperada: **`201 Created`** ✅

```json
{
  "id": 6797735936090161000,
  "title": "Clean Code",
  "author": "Robert Martin",
  "isbn": "978-0132350884",
  "totalStock": 5,
  "availableStock": 5
}
```

> ⚠️ El `id` que muestra Swagger puede tener imprecisión numérica. Para obtener el `_id` exacto guardado en MongoDB, consulta la colección `books` en MongoDB Compass.

---

### PASO 5 — Listar libros

1. Clic en `GET /api/books`
2. Clic en **"Try it out"**
3. Clic en **"Execute"**
4. Respuesta esperada: **`200 OK`** con el listado que incluye el libro creado ✅

---

### PASO 6 — Crear préstamo

Antes de crear el préstamo, obtén el `_id` exacto del libro desde MongoDB Compass:

1. Abre **MongoDB Compass**
2. Conéctate a tu cluster (`cluster0.arq8ibw.mongodb.net`)
3. Abre la base de datos **`librarydb`** → colección **`books`**
4. Copia el valor del campo `_id` (ejemplo: `6797735936090160942`)

Ahora en Swagger:

1. Clic en `POST /api/loans`
2. Clic en **"Try it out"**
3. Escribe usando el `_id` real del libro:

```json
{
  "bookId": 6797735936090160942
}
```

4. Clic en **"Execute"**
5. Respuesta esperada: **`201 Created`** ✅

```json
{
  "id": 27577206833004250,
  "bookId": 6797735936090161000,
  "loanDate": "2026-04-06",
  "returnDate": "2026-04-20",
  "returned": false
}
```

---

### PASO 7 — Ver préstamos

1. Clic en `GET /api/loans`
2. Clic en **"Try it out"**
3. Clic en **"Execute"**
4. Respuesta esperada: **`200 OK`** con el préstamo registrado ✅

---

### PASO 8 — Devolver el libro

Antes de devolver, obtén el `_id` exacto del préstamo desde MongoDB Compass:

1. En MongoDB Compass, abre la base de datos **`librarydb`** → colección **`loans`**
2. Copia el valor del campo `_id` (ejemplo: `27577206833004248`)

Ahora en Swagger:

1. Clic en `POST /api/loans/{id}/return`
2. Clic en **"Try it out"**
3. En el campo `id` escribe el `_id` exacto del préstamo
4. Clic en **"Execute"**
5. Respuesta esperada: **`200 OK`** con `"returned": true` ✅

```json
{
  "id": 27577206833004250,
  "returned": true,
  "returnDate": "2026-04-07"
}
```

---

### PASO 9 — Verificar en MongoDB Compass

Confirma que todos los datos se guardaron correctamente en la base de datos NoSQL:

1. Abre **MongoDB Compass**
2. Conéctate al cluster `cluster0.arq8ibw.mongodb.net`
3. Abre la base de datos **`librarydb`**

**Colección `users`:**
- Debe mostrar el documento del usuario registrado con campos: `nombre`, `email`, `username`, `password` (encriptado con BCrypt), `rol`

**Colección `books`:**
- Debe mostrar el libro con `availableStock: 4` (se redujo de 5 a 4 al crear el préstamo) ✅

**Colección `loans`:**
- Debe mostrar el préstamo con `devuelto: false` inicialmente, y después de la devolución `devuelto: true` ✅

---

## Resumen de resultados

| Operación | Endpoint | Código esperado | Estado |
|-----------|----------|-----------------|--------|
| Registro de usuario | `POST /api/auth/register` | 201 | ✅ |
| Login con JWT | `POST /api/auth/login` | 200 | ✅ |
| Crear libro | `POST /api/books` | 201 | ✅ |
| Listar libros | `GET /api/books` | 200 | ✅ |
| Crear préstamo | `POST /api/loans` | 201 | ✅ |
| Ver préstamos | `GET /api/loans` | 200 | ✅ |
| Devolver libro | `POST /api/loans/{id}/return` | 200 | ✅ |

---

## Video de pruebas funcionales

[Ver video de pruebas funcionales con MongoDB](LINK_DEL_VIDEO)

> El video muestra el flujo completo: registro, login, autorización JWT en Swagger, operaciones CRUD y verificación de datos en MongoDB Compass.

---

## Notas técnicas

- El `_id` generado por MongoDB para documentos con ID numérico puede presentar imprecisión al ser representado en JSON por Swagger (pérdida de los últimos dígitos). Siempre usa el `_id` directamente desde MongoDB Compass para operaciones que lo requieran.
- El token JWT tiene una validez de 24 horas. Si expira, repite el Paso 2 y el Paso 3.
- Al realizar un préstamo, el campo `availableStock` del libro se reduce automáticamente en 1. Al devolver, se restaura.