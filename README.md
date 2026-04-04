# DOSW Library - Sistema de Gestión de Biblioteca

![Java](https://img.shields.io/badge/Java-17-blue) ![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-green) ![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-336791) ![License](https://img.shields.io/badge/License-MIT-yellow)

API REST para la gestión integral de una biblioteca con autenticación JWT, autorización basada en roles, persistencia relacional (PostgreSQL), validaciones rigurosas y reglas de negocio para préstamos.

---

## Tabla de Contenidos
1. [Objetivo del Sistema](#objetivo-del-sistema)
2. [Arquitectura Completa](#arquitectura-completa)
3. [Modelo Entidad-Relación (ER)](#modelo-entidad-relación)
4. [Stack Tecnológico](#stack-tecnológico)
5. [Instrucciones de Ejecución](#instrucciones-de-ejecución)
6. [Evidencia Funcional](#evidencia-funcional)
7. [Guía de API](#guía-de-api)

---

## Objetivo del Sistema

La aplicación permite administrar el ciclo completo de una biblioteca:

- Gestión de usuarios (alta, consulta, actualización, eliminación)
- Gestión de libros con control de stock disponible
- Registro de préstamos con límites por rol
- Registro de devoluciones
- Consulta de préstamos por usuario autenticado
- Seguridad mediante **login JWT** y **autorización por roles** (LIBRARIAN/USER)
- Persistencia relacional en **PostgreSQL**
- Pruebas funcionales automatizadas

---

## Arquitectura Completa

La arquitectura está separada por capas para aislar responsabilidades:

```
src/main/java/edu/eci/dosw/DOSW_Library
│
├── DoswLibraryApplication.java          [Punto de entrada]
│
├── controller/                          [Capa REST]
│   ├── AuthController.java              [Autenticación: /api/auth/login]
│   ├── BookController.java              [Libros: CRUD]
│   ├── UserController.java              [Usuarios: CRUD]
│   ├── LoanController.java              [Préstamos: CRUD + devoluciones]
│   ├── GlobalExceptionHandler.java      [Manejo centralizado de errores]
│   ├── dto/                             [DTOs de solicitud/respuesta]
│   └── mapper/                          [Mapeo Entity ↔ DTO]
│
├── core/                                [Lógica de Negocio]
│   ├── model/                           [Modelos de dominio]
│   ├── service/                         [Servicios]
│   ├── strategy/                        [Estrategia de Préstamos]
│   ├── exception/                       [Excepciones Personalizadas]
│   ├── validator/                       [Validadores]
│   └── util/                            [Utilidades]
│
├── persistence/                         [Capa de Datos]
│   ├── entity/                          [Entidades JPA]
│   ├── dao/                             [Data Access Objects]
│   └── mapper/                          [Entity ↔ Model]
│
├── repository/                          [Spring Data JPA]
│
└── security/                            [Seguridad JWT]
    ├── SecurityConfig.java
    ├── JwtService.java
    ├── JwtAuthenticationFilter.java
    └── CustomUserDetailsService.java
```

---


## Modelo Entidad-Relación (ER)
### Diagrama ER/3FN


## Instrucciones de Ejecución

### Prerequisitos

- **Java 17+** instalado
- **Docker y Docker Compose** instalados
- **Maven 3.8+** (incluido en el proyecto con `mvnw`)

### Opción A: Ejecución con PostgreSQL (Docker Compose)

#### Paso 1: Iniciar la base de datos PostgreSQL

Verifica que PostgreSQL esté corriendo:

#### Paso 2: Compilar y ejecutar la aplicación

```bash
# Windows
.\mvnw.cmd spring-boot:run

La aplicación se inicializará en `http://localhost:8080`

#### Paso 3: Acceder a Swagger UI

```
http://localhost:8080/swagger-ui.html
```

---

### 2Opión B: Ejecución con Build JAR

```bash
# Compilar sin pruebas
.\mvnw.cmd -DskipTests package

# Ejecutar JAR
java -jar target/DOSW-Library-0.0.1-SNAPSHOT.jar
```

---

### Ejecutar Pruebas

#### Pruebas unitarias (H2 en memoria):

```bash
.\mvnw.cmd test
```

#### Pruebas de integración PostgreSQL:

```bash
# Requiere Docker + Docker Compose activos
.\mvnw.cmd -Dtest=PostgreSqlBookPersistenceIT test
```

#### Todas las pruebas:

```bash
.\mvnw.cmd clean verify
```


## 🔐 Características de Seguridad

### Autenticación JWT
- Token firmado con HS256
- Expiración configurable (24 horas por defecto)
- Secret key parametrizado en `application.yaml`

### Autorización por Roles

| Rol | Permisos |
|-----|----------|
| **LIBRARIAN** | Gestionar libros, usuarios; ver todos los préstamos |
| **USER** | Ver libros; solicitar/devolver préstamos propios |

### Configuración de Seguridad
- CSRF deshabilitado (API stateless)
- CORS configurado
- Sesiones deshabilitadas
- HTTPS/TLS configurable
- Validaciones estrictas de entrada

---

## Resultados de Pruebas

```
Build Status: ÉXITO
Test Coverage: Todas las capas cubiertas
Integration Tests: PostgreSQL compatible
Security Tests: JWT, roles, validaciones

Comando para reproducir:
.\mvnw.cmd clean verify
```


**Última actualización:** 27 de Marzo de 2026

