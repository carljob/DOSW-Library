# Library Management API

API REST para la gestion de biblioteca construida con Spring Boot.

El proyecto implementa autenticacion JWT, autorizacion por roles, persistencia relacional con JPA/Hibernate, validaciones, mapeo DTO con MapStruct y reglas de negocio para prestamos y control de inventario.

---

## 1) Objetivo del sistema

La aplicacion permite administrar el ciclo completo de una biblioteca:

- Gestion de usuarios (alta, consulta, actualizacion y eliminacion)
- Gestion de libros con control de stock
- Registro de prestamos
- Registro de devoluciones
- Consulta de prestamos por usuario autenticado
- Seguridad mediante login y token JWT

---

## 2) Arquitectura completa

La arquitectura del proyecto esta separada por capas para aislar responsabilidades y facilitar mantenimiento, pruebas y evolucion.

```text
src/main/java/edu/eci/dosw/DOSW_Library
│
├── DoswLibraryApplication.java
│
├── controller
│   ├── AuthController.java
│   ├── BookController.java
│   ├── UserController.java
│   ├── LoanController.java
│   ├── GlobalExceptionHandler.java
│   ├── dto
│   │   ├── AuthRequestDTO.java
│   │   ├── AuthResponseDTO.java
│   │   ├── BookRequestDTO.java
│   │   ├── BookResponseDTO.java
│   │   ├── UserRequestDTO.java
│   │   ├── UserResponseDTO.java
│   │   ├── LoanRequestDTO.java
│   │   └── LoanResponseDTO.java
│   └── mapper
│       ├── BookMapper.java
│       ├── UserMapper.java
│       └── LoanMapper.java
│
├── core
│   ├── model
│   │   ├── Book.java
│   │   ├── User.java
│   │   ├── Loan.java
│   │   └── Role.java
│   ├── service
│   │   ├── AuthService.java
│   │   ├── BookService.java
│   │   ├── UserService.java
│   │   └── LoanService.java
│   ├── strategy
│   │   ├── LoanPolicyStrategy.java
│   │   ├── StandardLoanPolicyStrategy.java
│   │   ├── PremiumLoanPolicyStrategy.java
│   │   └── LoanPolicyContext.java
│   ├── validator
│   │   ├── BookValidator.java
│   │   ├── UserValidator.java
│   │   └── LoanValidator.java
│   ├── util
│   │   ├── DateUtil.java
│   │   └── ValidationUtil.java
│   └── exception
│       ├── BookNotFoundException.java
│       ├── BookNotAvailableException.java
│       ├── UserNotFoundException.java
│       ├── ResourceNotFoundException.java
│       ├── UnauthorizedOperationException.java
│       └── LoanLimitExceededException.java
│
├── repository
│   ├── BookRepository.java
│   ├── UserRepository.java
│   └── LoanRepository.java
│
└── security
    ├── SecurityConfig.java
    ├── JwtService.java
    ├── JwtAuthenticationFilter.java
    └── CustomUserDetailsService.java
```

### Descripcion completa de cada capa

#### `controller`
Capa de entrada HTTP.

Responsabilidades:
- Exponer endpoints REST
- Recibir DTOs de entrada
- Invocar servicios
- Devolver DTOs de respuesta
- Aplicar restricciones por rol con `@PreAuthorize`

Controladores actuales:
- `AuthController`: registro e inicio de sesion
- `BookController`: operaciones sobre libros
- `UserController`: administracion de usuarios
- `LoanController`: prestamos y devoluciones

#### `controller.dto`
Contratos de API (entrada/salida), desacoplados de entidades JPA.

Responsabilidades:
- Definir payloads de request/response
- Incluir validaciones de Bean Validation en requests

DTOs actuales:
- Auth: `AuthRequestDTO`, `AuthResponseDTO`
- Book: `BookRequestDTO`, `BookResponseDTO`
- User: `UserRequestDTO`, `UserResponseDTO`
- Loan: `LoanRequestDTO`, `LoanResponseDTO`

#### `controller.mapper`
Mapeadores con MapStruct entre entidades y DTOs.

Responsabilidades:
- Conversión `Entity -> ResponseDTO`
- Conversión `RequestDTO -> Entity`
- Mantener limpia la capa controller/service

Mappers actuales:
- `BookMapper`
- `UserMapper`
- `LoanMapper`

#### `core.model`
Modelo de dominio (entidades JPA y enums principales).

Responsabilidades:
- Representar el estado del negocio
- Definir relaciones entre entidades

Modelos actuales:
- `Book`
- `User`
- `Loan`
- `Role` (enum de autorizacion)

#### `core.service`
Capa de logica de negocio.

Responsabilidades:
- Aplicar reglas de negocio
- Coordinar operaciones transaccionales
- Integrar repositorios, validadores y utilidades

Servicios actuales:
- `AuthService`
- `BookService`
- `UserService`
- `LoanService`

#### `core.strategy`
Implementacion del Strategy Pattern para politicas de prestamo.

Responsabilidades:
- Encapsular politicas por rol
- Resolver estrategia mediante contexto

Clases actuales:
- `LoanPolicyStrategy`
- `StandardLoanPolicyStrategy`
- `PremiumLoanPolicyStrategy`
- `LoanPolicyContext`

#### `core.validator`
Validadores de reglas de datos de entrada/consistencia basica.

Responsabilidades:
- Validar campos requeridos
- Validar rangos y consistencia de datos

Validadores actuales:
- `BookValidator`
- `UserValidator`
- `LoanValidator`

#### `core.exception`
Excepciones de negocio y control de errores del dominio.

Responsabilidades:
- Representar errores funcionales de forma explicita
- Permitir respuestas HTTP claras desde el handler global

Excepciones actuales:
- `BookNotFoundException`
- `BookNotAvailableException`
- `UserNotFoundException`
- `ResourceNotFoundException`
- `UnauthorizedOperationException`
- `LoanLimitExceededException`

#### `core.util`
Utilidades transversales reutilizables.

Responsabilidades:
- Operaciones auxiliares comunes

Utilidades actuales:
- `DateUtil`
- `ValidationUtil`

#### `repository`
Acceso a datos con Spring Data JPA.

Responsabilidades:
- Persistencia relacional
- Consultas por criterio

Repositorios actuales:
- `BookRepository`
- `UserRepository`
- `LoanRepository`

#### `security`
Capa de seguridad para autenticacion y autorizacion.

Responsabilidades:
- Generar/validar JWT
- Cargar usuarios autenticables
- Filtrar requests con token
- Definir reglas de acceso

Componentes actuales:
- `SecurityConfig`
- `JwtService`
- `JwtAuthenticationFilter`
- `CustomUserDetailsService`

---

## 3) Modelo de datos y reglas de inventario

### `Book`
- `id`
- `title`
- `author`
- `isbn` (unico)
- `totalStock`
- `availableStock`

### `User`
- `id`
- `name`
- `email` (unico)
- `username` (unico)
- `password` (encriptado)
- `role` (`USER`, `LIBRARIAN`)

### `Loan`
- `id`
- `book`
- `user`
- `loanDate`
- `returnDate`
- `returned`

### Reglas de inventario
- Solo se puede prestar si `availableStock > 0`
- En prestamo: `availableStock--`
- En devolucion: `availableStock++`
- `availableStock` no puede ser mayor que `totalStock`

---

## 4) Seguridad (JWT + Roles)

### Flujo de autenticacion
1. Registro: `POST /api/auth/register`
2. Login: `POST /api/auth/login`
3. Respuesta login: token JWT
4. Consumo protegido con header:
   - `Authorization: Bearer <token>`

### Roles
- `USER`
- `LIBRARIAN`

### Restricciones principales
- `LIBRARIAN`: CRUD de libros, gestion de usuarios, consulta global de prestamos
- `USER`: consultar libros, crear prestamos, devolver propios prestamos, consultar `my-loans`

---

## 5) Endpoints

### Auth
```http
POST /api/auth/register
POST /api/auth/login
```

### Books
```http
GET    /api/books
GET    /api/books/{id}
POST   /api/books
PUT    /api/books/{id}
DELETE /api/books/{id}
```

### Users
```http
GET    /api/users
GET    /api/users/{id}
POST   /api/users
PUT    /api/users/{id}
DELETE /api/users/{id}
```

### Loans
```http
GET    /api/loans
GET    /api/loans/{id}
GET    /api/loans/my-loans
POST   /api/loans
POST   /api/loans/{id}/return
DELETE /api/loans/{id}
```

---

## 6) Manejo de errores

El proyecto maneja errores de forma centralizada con `GlobalExceptionHandler`.

Formato de error:

```json
{
  "timestamp": "2026-03-21T10:30:00",
  "status": 404,
  "error": "Not Found",
  "message": "Resource not found"
}
```

---

## 7) Tecnologias

- Java 21
- Spring Boot
- Spring Web
- Spring Data JPA
- Spring Security
- JWT (`jjwt`)
- MapStruct
- Bean Validation (`jakarta.validation`)
- H2 Database
- Maven
- JUnit 5 + Mockito

---

## 8) Ejecucion del proyecto

### Compilar
```bash
mvn clean install
```

### Ejecutar
```bash
mvn spring-boot:run
```

Aplicacion:

```text
http://localhost:8080
```

Consola H2:

```text
http://localhost:8080/h2-console
```

---

## 9) Pruebas

```bash
mvn test
```

---

## 10) Estado actual

El proyecto se encuentra en una arquitectura limpia por capas con:

- Persistencia relacional
- Seguridad JWT
- Autorizacion por roles
- DTOs y mapeo con MapStruct
- Validaciones y manejo centralizado de errores
- Pruebas unitarias
