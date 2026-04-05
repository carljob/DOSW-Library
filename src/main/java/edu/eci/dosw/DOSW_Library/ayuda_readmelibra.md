# 📚 DOSW Library API

Sistema de gestión de biblioteca con persistencia relacional, autenticación JWT y autorización por roles.

---

## Tabla de Contenidos

1. [Descripción del Proyecto](#descripción-del-proyecto)
2. [Tecnologías Utilizadas](#tecnologías-utilizadas)
3. [Arquitectura del Sistema](#arquitectura-del-sistema)
4. [Diagrama Entidad-Relación (3FN)](#diagrama-entidad-relación-3fn)
5. [Configuración y Ejecución](#configuración-y-ejecución)
6. [Seguridad JWT](#seguridad-jwt)
7. [Endpoints de la API](#endpoints-de-la-api)
8. [Roles y Permisos](#roles-y-permisos)
9. [Validaciones de Negocio](#validaciones-de-negocio)
10. [Manejo de Errores](#manejo-de-errores)
11. [Pruebas](#pruebas)
12. [Video de Demostración](#video-de-demostración)

---

## Descripción del Proyecto

DOSW Library es una API REST para la gestión de una biblioteca. La versión actual incluye:

- ✅ Persistencia en base de datos relacional (PostgreSQL)
- ✅ Autenticación basada en JWT
- ✅ Autorización por roles (USER / LIBRARIAN)
- ✅ Control de inventario de libros
- ✅ Arquitectura por capas desacoplada
- ✅ DTOs, MapStruct, validaciones y manejo de errores

---

## Tecnologías Utilizadas

| Tecnología | Versión | Propósito |
|---|---|---|
| Java | 17 | Lenguaje base |
| Spring Boot | 4.0.3 | Framework principal |
| Spring Security | - | Autenticación y autorización |
| Spring Data JPA | - | Persistencia relacional |
| PostgreSQL | 16 | Base de datos |
| JJWT | 0.12.6 | Generación y validación de tokens JWT |
| MapStruct | 1.6.3 | Mapeo entre entidades y DTOs |
| Lombok | - | Reducción de boilerplate |
| SpringDoc OpenAPI | 2.8.9 | Documentación Swagger |
| Docker / Docker Compose | - | Contenedor de base de datos |
| JUnit 5 / Mockito | - | Pruebas unitarias |

---

## Arquitectura del Sistema

El proyecto sigue una **arquitectura por capas** con separación clara de responsabilidades:

```
src/main/java/edu/eci/dosw/DOSW_Library/
├── controller/              ← Capa de presentación (REST Controllers)
│   ├── dto/                 ← Data Transfer Objects (Request / Response)
│   └── mapper/              ← Mappers MapStruct (Controller ↔ Core)
├── core/                    ← Capa de negocio
│   ├── model/               ← Entidades JPA (Book, User, Loan, Role)
│   ├── service/             ← Servicios de negocio
│   ├── exception/           ← Excepciones personalizadas
│   └── validator/           ← Validadores de negocio
├── repository/              ← Repositorios JPA (BookRepository, UserRepository, LoanRepository)
└── security/                ← Capa de seguridad
    ├── JwtService.java      ← Generación y validación de tokens
    ├── JwtAuthenticationFilter.java ← Filtro JWT por request
    ├── SecurityConfig.java  ← Configuración Spring Security
    └── CustomUserDetailsService.java ← Carga de usuario desde BD
```

### Flujo de una petición autenticada

```
Cliente → [Authorization: Bearer <token>]
        → JwtAuthenticationFilter (valida token, carga usuario en contexto)
        → SecurityConfig (verifica permisos de ruta)
        → Controller (@PreAuthorize valida rol)
        → Service (lógica de negocio)
        → Repository (consulta BD PostgreSQL)
        → Response DTO
```

---

## Diagrama Entidad-Relación (3FN)

### Modelo Relacional

```
┌─────────────────────────┐         ┌──────────────────────────┐
│         users           │         │          books           │
├─────────────────────────┤         ├──────────────────────────┤
│ id          BIGSERIAL PK│         │ id         BIGSERIAL PK  │
│ name        VARCHAR NN  │         │ title      VARCHAR  NN   │
│ email       VARCHAR UQ  │         │ author     VARCHAR  NN   │
│ username    VARCHAR UQ  │         │ isbn       VARCHAR  UQ   │
│ password    VARCHAR NN  │         │ total_stock    INT  NN   │
│ role        VARCHAR NN  │         │ available_stock INT NN   │
└─────────────────────────┘         └──────────────────────────┘
           │1                                    │1
           │                                     │
           │N                                    │N
┌──────────────────────────────────────────────────────┐
│                         loans                        │
├──────────────────────────────────────────────────────┤
│ id          BIGSERIAL  PK                            │
│ user_id     BIGINT     FK → users(id)  NN            │
│ book_id     BIGINT     FK → books(id)  NN            │
│ loan_date   DATE       NN                            │
│ return_date DATE       (nullable)                    │
│ returned    BOOLEAN    NN DEFAULT false              │
└──────────────────────────────────────────────────────┘
```

### Normalización 3FN

- **1FN**: Todos los atributos son atómicos, no hay grupos repetidos.
- **2FN**: Cada atributo no clave depende completamente de la clave primaria (no hay dependencias parciales).
- **3FN**: No hay dependencias transitivas. `role` depende directamente de `users.id`, no de otro atributo no clave. `total_stock` y `available_stock` dependen de `books.id`.

---

## Configuración y Ejecución

### Requisitos previos

- Java 17+
- Maven 3.9+
- Docker y Docker Compose

### 1. Levantar la base de datos

```bash
docker-compose up -d
```

Esto crea un contenedor PostgreSQL con:
- **Host**: localhost:5432
- **Base de datos**: librarydb
- **Usuario**: postgres
- **Contraseña**: postgres

### 2. Configurar `application.yaml`

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/librarydb
    username: postgres
    password: postgres
    driver-class-name: org.postgresql.Driver
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
    database-platform: org.hibernate.dialect.PostgreSQLDialect

security:
  jwt:
    secret: clave-super-secreta-debe-tener-minimo-32-caracteres
    expiration-millis: 86400000  # 24 horas

springdoc:
  swagger-ui:
    path: /swagger-ui.html
```

### 3. Compilar y ejecutar

```bash
./mvnw clean install
./mvnw spring-boot:run
```

### 4. Acceder a Swagger

```
http://localhost:8080/swagger-ui.html
```

---

## Seguridad JWT

### Concepto

La API implementa seguridad **stateless** con JWT (JSON Web Token). Esto significa que el servidor no guarda sesiones; cada request debe enviar el token en el header.

### Flujo de autenticación

```
1. POST /api/auth/register  →  Registra usuario, retorna JWT
2. POST /api/auth/login     →  Valida credenciales, retorna JWT
3. Siguientes requests      →  Header: Authorization: Bearer <token>
```

### ¿Qué contiene el token?

El token JWT está compuesto por 3 partes (separadas por `.`):

```
HEADER.PAYLOAD.SIGNATURE
```

El **payload** contiene:
- `sub`: username del usuario
- `iat`: fecha de emisión
- `exp`: fecha de expiración

El token está **firmado con HMAC-SHA256** usando una clave secreta, lo que garantiza **integridad**: si alguien modifica el payload, la firma no coincidirá y el token será rechazado.

### Cómo usar el token en Postman

1. Hacer `POST /api/auth/login` con body:
```json
{
  "username": "miusuario",
  "password": "mipassword"
}
```
2. Copiar el valor del campo `token` de la respuesta.
3. En cada siguiente request, agregar el header:
```
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

### Configuración Spring Security

La configuración deshabilita sesiones (STATELESS), deshabilita CSRF (no aplica para APIs REST con JWT), y define reglas de acceso por ruta:

```java
.csrf(csrf -> csrf.disable())
.sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
.authorizeHttpRequests(auth -> auth
    .requestMatchers("/api/auth/**").permitAll()          // público
    .requestMatchers(GET, "/api/books/**").hasAnyRole("USER","LIBRARIAN")
    .requestMatchers("/api/books/**").hasRole("LIBRARIAN")
    .requestMatchers("/api/users/**").hasRole("LIBRARIAN")
    .requestMatchers("/api/loans/**").hasAnyRole("USER","LIBRARIAN")
    .anyRequest().authenticated()
)
```

### Filtro JWT

El `JwtAuthenticationFilter` intercepta **cada request** antes de llegar al controller:

1. Extrae el header `Authorization: Bearer <token>`
2. Valida el token con `JwtService`
3. Carga el usuario desde la base de datos
4. Registra la autenticación en el `SecurityContextHolder`

---

## Endpoints de la API

### Auth (públicos)

| Método | Ruta | Descripción | Body |
|---|---|---|---|
| POST | `/api/auth/register` | Registrar nuevo usuario | `UserRequestDTO` |
| POST | `/api/auth/login` | Iniciar sesión | `AuthRequestDTO` |

**Body register / login:**
```json
// Register
{
  "name": "Juan Pérez",
  "email": "juan@email.com",
  "username": "juanp",
  "password": "secret123",
  "role": "USER"   // opcional, defecto USER
}

// Login
{
  "username": "juanp",
  "password": "secret123"
}

// Response (ambos)
{
  "token": "eyJhbGciOiJIUzI1NiJ9..."
}
```

---

### Books (requieren token)

| Método | Ruta | Rol requerido | Descripción |
|---|---|---|---|
| GET | `/api/books` | USER, LIBRARIAN | Listar todos los libros |
| GET | `/api/books/{id}` | USER, LIBRARIAN | Obtener libro por ID |
| POST | `/api/books` | LIBRARIAN | Crear nuevo libro |
| PUT | `/api/books/{id}` | LIBRARIAN | Actualizar libro |
| DELETE | `/api/books/{id}` | LIBRARIAN | Eliminar libro |

**Body crear/actualizar libro:**
```json
{
  "title": "Clean Code",
  "author": "Robert C. Martin",
  "isbn": "978-0132350884",
  "totalStock": 5,
  "availableStock": 5
}
```

**Response libro:**
```json
{
  "id": 1,
  "title": "Clean Code",
  "author": "Robert C. Martin",
  "isbn": "978-0132350884",
  "totalStock": 5,
  "availableStock": 5
}
```

---

### Users (solo LIBRARIAN)

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/api/users` | Listar todos los usuarios |
| GET | `/api/users/{id}` | Obtener usuario por ID |
| POST | `/api/users` | Crear usuario |
| PUT | `/api/users/{id}` | Actualizar usuario |
| DELETE | `/api/users/{id}` | Eliminar usuario |

---

### Loans (requieren token)

| Método | Ruta | Rol requerido | Descripción |
|---|---|---|---|
| GET | `/api/loans` | LIBRARIAN | Listar todos los préstamos |
| GET | `/api/loans/my-loans` | USER, LIBRARIAN | Ver mis propios préstamos |
| GET | `/api/loans/{id}` | LIBRARIAN | Obtener préstamo por ID |
| POST | `/api/loans` | USER, LIBRARIAN | Crear préstamo |
| POST | `/api/loans/{id}/return` | USER, LIBRARIAN | Devolver libro |
| DELETE | `/api/loans/{id}` | LIBRARIAN | Eliminar préstamo |

**Body crear préstamo:**
```json
{
  "bookId": 1
}
```

**Response préstamo:**
```json
{
  "id": 10,
  "bookId": 1,
  "bookTitle": "Clean Code",
  "userId": 3,
  "username": "juanp",
  "loanDate": "2025-04-04",
  "returnDate": null,
  "returned": false
}
```

---

## Roles y Permisos

### LIBRARIAN (Bibliotecario)

Usuario con privilegios administrativos.

| Operación | Endpoint |
|---|---|
| Gestionar libros (CRUD) | `/api/books/**` |
| Gestionar usuarios (CRUD) | `/api/users/**` |
| Ver todos los préstamos | `GET /api/loans` |
| Gestionar préstamos | `/api/loans/**` |
| Devolver cualquier préstamo | `POST /api/loans/{id}/return` |

### USER (Usuario estándar)

| Operación | Endpoint |
|---|---|
| Consultar libros disponibles | `GET /api/books` |
| Ver detalle de libro | `GET /api/books/{id}` |
| Solicitar préstamo | `POST /api/loans` |
| Ver **sus propios** préstamos | `GET /api/loans/my-loans` |
| Devolver **su propio** préstamo | `POST /api/loans/{id}/return` |

> ⚠️ Un USER que intente devolver un préstamo que no le pertenece recibirá `403 Forbidden`.

---

## Validaciones de Negocio

### Libros

- `totalStock` debe ser **mayor a 0** (no se puede crear un libro con 0 o menos copias totales)
- `availableStock` no puede ser **menor a 0**
- `availableStock` no puede ser **mayor a totalStock**
- Si se crea un libro sin especificar `availableStock`, se asigna igual a `totalStock`
- Al actualizar el stock, si el nuevo `availableStock` supera el nuevo `totalStock`, se ajusta automáticamente

### Préstamos

- Solo se puede prestar un libro si `availableStock > 0`
- Al prestar: `availableStock` se reduce en 1
- Al devolver: `availableStock` se incrementa en 1 (máximo hasta `totalStock`)
- No se puede devolver un préstamo que ya tiene `returned = true`
- Un USER solo puede devolver sus propios préstamos; un LIBRARIAN puede devolver cualquiera

### Usuarios

- `username` y `email` deben ser únicos en el sistema
- La contraseña se almacena **cifrada con BCrypt**
- El campo `username` debe tener entre 4 y 50 caracteres
- La contraseña debe tener entre 6 y 100 caracteres

---

## Manejo de Errores

La API devuelve respuestas de error estandarizadas:

```json
{
  "timestamp": "2025-04-04T10:30:00",
  "status": 404,
  "error": "Not Found",
  "message": "Book not found with id: 99"
}
```

| Código HTTP | Situación |
|---|---|
| `400 Bad Request` | Validación de inputs fallida |
| `401 Unauthorized` | Token inválido, expirado o ausente |
| `403 Forbidden` | Usuario autenticado sin permisos para la operación |
| `404 Not Found` | Recurso no encontrado (libro, usuario, préstamo) |
| `409 Conflict` | Libro sin disponibilidad, préstamo ya devuelto, username duplicado |

### Escenarios de seguridad

| Escenario | Resultado esperado |
|---|---|
| Request sin token | `401 Unauthorized` |
| Token con firma inválida | `401 Unauthorized` |
| Token expirado | `401 Unauthorized` |
| USER intentando crear libro | `403 Forbidden` |
| USER accediendo a `/api/users` | `403 Forbidden` |
| Credenciales incorrectas en login | `401 Unauthorized` |
| LIBRARIAN en cualquier endpoint | ✅ Acceso permitido |

---

## Pruebas

### Pruebas unitarias con Mockito

El proyecto incluye pruebas de servicio que verifican:

**LoanServiceTest:**
- ✅ Crear préstamo decrementa el stock del libro
- ✅ Devolver préstamo incrementa el stock del libro
- ✅ Un USER no puede devolver el préstamo de otro usuario
- ✅ Un LIBRARIAN puede devolver cualquier préstamo

**UserServiceTest:**
- ✅ Crear usuario hashea la contraseña con BCrypt
- ✅ Se puede recuperar un usuario por ID

### Ejecutar pruebas

```bash
./mvnw test
```

### Pruebas funcionales con Postman

**Paso 1 - Registrar un bibliotecario:**
```
POST /api/auth/register
Body: { "name": "Admin", "email": "admin@lib.com", "username": "admin", "password": "admin123", "role": "LIBRARIAN" }
→ 201 Created + token
```

**Paso 2 - Login:**
```
POST /api/auth/login
Body: { "username": "admin", "password": "admin123" }
→ 200 OK + token
```

**Paso 3 - Crear libro (con token de LIBRARIAN):**
```
POST /api/books
Header: Authorization: Bearer <token>
Body: { "title": "Clean Code", "author": "Martin", "isbn": "123", "totalStock": 3, "availableStock": 3 }
→ 201 Created
```

**Paso 4 - Registrar usuario normal:**
```
POST /api/auth/register
Body: { "name": "Juan", "email": "juan@email.com", "username": "juan", "password": "juan123", "role": "USER" }
→ 201 Created + token de USER
```

**Paso 5 - USER solicita préstamo:**
```
POST /api/loans
Header: Authorization: Bearer <token-de-USER>
Body: { "bookId": 1 }
→ 201 Created (availableStock pasa de 3 a 2 en BD)
```

**Paso 6 - USER intenta crear libro (debe fallar):**
```
POST /api/books
Header: Authorization: Bearer <token-de-USER>
→ 403 Forbidden
```

**Paso 7 - USER devuelve libro:**
```
POST /api/loans/1/return
Header: Authorization: Bearer <token-de-USER>
→ 200 OK (availableStock vuelve a 3 en BD)
```

---

## Video de Demostración

> 🎥 **[Insertar aquí el enlace al video de YouTube / Drive]**

El video muestra:
1. Inicio de sesión y obtención del JWT
2. Uso del token en Postman / Swagger
3. Pruebas funcionales de todos los endpoints
4. Verificación de cambios en la base de datos
5. Escenarios de acceso permitido y denegado según rol

---

## Estructura del Proyecto

```
DOSW-Library/
├── docker-compose.yml          ← Base de datos PostgreSQL
├── pom.xml                     ← Dependencias Maven
└── src/
    ├── main/
    │   ├── java/edu/eci/dosw/DOSW_Library/
    │   │   ├── DoswLibraryApplication.java
    │   │   ├── controller/
    │   │   │   ├── AuthController.java
    │   │   │   ├── BookController.java
    │   │   │   ├── UserController.java
    │   │   │   ├── LoanController.java
    │   │   │   ├── GlobalExceptionHandler.java
    │   │   │   ├── dto/         (AuthRequest/Response, Book, User, LoanDTOs)
    │   │   │   └── mapper/      (BookMapper, UserMapper, LoanMapper)
    │   │   ├── core/
    │   │   │   ├── model/       (Book, User, Loan, Role)
    │   │   │   ├── service/     (BookService, UserService, LoanService, AuthService)
    │   │   │   ├── exception/   (BookNotFoundException, etc.)
    │   │   │   └── validator/   (BookValidator, LoanValidator)
    │   │   ├── repository/
    │   │   │   ├── BookRepository.java
    │   │   │   ├── UserRepository.java
    │   │   │   └── LoanRepository.java
    │   │   └── security/
    │   │       ├── JwtService.java
    │   │       ├── JwtAuthenticationFilter.java
    │   │       ├── SecurityConfig.java
    │   │       └── CustomUserDetailsService.java
    │   └── resources/
    │       └── application.yaml
    └── test/
        └── java/edu/eci/dosw/DOSW_Library/
            └── core/service/
                ├── LoanServiceTest.java
                └── UserServiceTest.java
```

---

## Autor

**DOSW Company** — Proyecto académico ECI  
Curso: Desarrollo de Software — Corte 2026