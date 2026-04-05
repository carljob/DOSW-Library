# 📚 DOSW Library API — Cómo construir cada parte desde cero
### Guía tutorial para parcial práctico — Paso a paso con ejemplos reales

---

## 📋 Tabla de Contenido

1. [Configurar el proyecto (pom.xml + application.yaml)](#1-configurar-el-proyecto)
2. [Crear el Modelo de Dominio](#2-crear-el-modelo-de-dominio)
3. [Crear la Entidad JPA (tabla en BD)](#3-crear-la-entidad-jpa)
4. [Crear el DAO (repositorio JPA)](#4-crear-el-dao)
5. [Crear el Mapper de Persistencia](#5-crear-el-mapper-de-persistencia)
6. [Crear la Interfaz de Repositorio de Dominio](#6-crear-la-interfaz-de-repositorio-de-dominio)
7. [Crear la Implementación del Repositorio](#7-crear-la-implementación-del-repositorio)
8. [Crear el Servicio (lógica de negocio)](#8-crear-el-servicio)
9. [Crear los DTOs](#9-crear-los-dtos)
10. [Crear el Mapper de Controller (MapStruct)](#10-crear-el-mapper-de-controller)
11. [Crear el Controller (endpoints HTTP)](#11-crear-el-controller)
12. [Crear las Excepciones personalizadas](#12-crear-las-excepciones-personalizadas)
13. [Crear el GlobalExceptionHandler](#13-crear-el-globalexceptionhandler)
14. [Implementar la Seguridad JWT](#14-implementar-la-seguridad-jwt)
15. [Flujo completo de prueba con Swagger](#15-flujo-completo-de-prueba-con-swagger)

---

## 1. Configurar el proyecto

### Paso 1 — Agregar dependencias en pom.xml

Abre `pom.xml` y agrega dentro de `<dependencies>`:

```xml
<!-- Web (para hacer endpoints REST) -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>

<!-- JPA (para hablar con la base de datos) -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>

<!-- Driver de PostgreSQL -->
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <scope>runtime</scope>
</dependency>

<!-- Validaciones (@NotBlank, @Min, etc.) -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>

<!-- Seguridad -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>

<!-- JWT -->
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.12.6</version>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-impl</artifactId>
    <version>0.12.6</version>
    <scope>runtime</scope>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-jackson</artifactId>
    <version>0.12.6</version>
    <scope>runtime</scope>
</dependency>

<!-- MapStruct (mapeos automáticos) -->
<dependency>
    <groupId>org.mapstruct</groupId>
    <artifactId>mapstruct</artifactId>
    <version>1.6.3</version>
</dependency>

<!-- Lombok (getters/setters automáticos) -->
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <optional>true</optional>
</dependency>

<!-- Swagger UI (documentación de la API) -->
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.8.9</version>
</dependency>
```

También agrega el procesador de MapStruct en `<build><plugins>`:

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-compiler-plugin</artifactId>
    <configuration>
        <annotationProcessorPaths>
            <path>
                <groupId>org.projectlombok</groupId>
                <artifactId>lombok</artifactId>
            </path>
            <path>
                <groupId>org.mapstruct</groupId>
                <artifactId>mapstruct-processor</artifactId>
                <version>1.6.3</version>
            </path>
        </annotationProcessorPaths>
    </configuration>
</plugin>
```

### Paso 2 — Configurar application.yaml

Crea o edita `src/main/resources/application.yaml`:

```yaml
spring:
  application:
    name: DOSW-Library

  datasource:
    # ${VARIABLE:valor_default} → usa variable de entorno, si no existe usa el default
    url: ${DB_URL:jdbc:postgresql://localhost:5432/librarydb}
    username: ${DB_USERNAME:postgres}
    password: ${DB_PASSWORD:postgres}
    driver-class-name: org.postgresql.Driver

  jpa:
    hibernate:
      ddl-auto: update    # Crea o actualiza las tablas automáticamente al arrancar
    show-sql: true         # Muestra las queries SQL en consola

security:
  jwt:
    secret: ${JWT_SECRET:mi-clave-secreta-muy-larga-de-al-menos-32-caracteres}
    expiration-millis: ${JWT_EXPIRATION_MILLIS:86400000}   # 24 horas en milisegundos
```

> ⚠️ El `secret` de JWT debe tener mínimo 32 caracteres, si no lanza error al arrancar.

---

## 2. Crear el Modelo de Dominio

El modelo de dominio representa **las entidades del negocio** (no tiene nada de JPA ni de HTTP).

### Cómo se crea — Ejemplo: `Book`

Crea la clase en `core/model/Book.java`:

```java
package edu.eci.dosw.DOSW_Library.core.model;

// Esta clase representa un libro en la lógica del negocio.
// NO tiene anotaciones de JPA ni de Spring.
public class Book {

    private Long id;
    private String title;
    private String author;
    private String isbn;
    private int totalStock;
    private int availableStock;

    // Constructor vacío (obligatorio para frameworks)
    public Book() {}

    // Constructor con todos los campos
    public Book(Long id, String title, String author, String isbn,
                int totalStock, int availableStock) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.totalStock = totalStock;
        this.availableStock = availableStock;
    }

    // Getters y Setters de cada campo
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }

    public int getTotalStock() { return totalStock; }
    public void setTotalStock(int totalStock) { this.totalStock = totalStock; }

    public int getAvailableStock() { return availableStock; }
    public void setAvailableStock(int availableStock) { this.availableStock = availableStock; }
}
```

### Cómo se crea — Ejemplo: `User` con Spring Security

`User` es especial porque implementa `UserDetails` para que Spring Security lo pueda usar:

```java
package edu.eci.dosw.DOSW_Library.core.model;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.List;

// Implementa UserDetails para integrarse con Spring Security
public class User implements UserDetails {

    private Long id;
    private String name;
    private String email;
    private String username;
    private String password;
    private Role role;

    public User() {}

    public User(Long id, String name, String email,
                String username, String password, Role role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.username = username;
        this.password = password;
        this.role = role;
    }

    // Método clave: devuelve los roles del usuario como autoridades de Spring Security
    // IMPORTANTE: Spring Security espera el prefijo "ROLE_" + nombre del rol
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
        // Ejemplo: si role = USER → devuelve "ROLE_USER"
        //          si role = LIBRARIAN → devuelve "ROLE_LIBRARIAN"
    }

    // Métodos requeridos por UserDetails (retornan true para simplificar)
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }

    // Getters y setters...
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    @Override public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    @Override public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
}
```

### Enum de Roles

Crea `core/model/Role.java`:

```java
package edu.eci.dosw.DOSW_Library.core.model;

public enum Role {
    USER,       // Usuario normal de la biblioteca
    LIBRARIAN   // Administrador / Bibliotecario
}
```

---

## 3. Crear la Entidad JPA

La entidad JPA **mapea la clase a una tabla de base de datos**. Va en `persistence/entity/`.

### Cómo se crea — Ejemplo: `BookEntity`

```java
package edu.eci.dosw.DOSW_Library.persistence.entity;

import jakarta.persistence.*;   // ← Importar todas las anotaciones JPA

@Entity                         // ← Le dice a JPA "esto es una tabla"
@Table(name = "books")          // ← Nombre de la tabla en PostgreSQL
public class BookEntity {

    @Id                                                    // ← Llave primaria
    @GeneratedValue(strategy = GenerationType.IDENTITY)    // ← Auto-incremental (1,2,3...)
    private Long id;

    @Column(nullable = false)                // ← Campo NOT NULL
    private String title;

    @Column(nullable = false)
    private String author;

    @Column(nullable = false, unique = true) // ← NOT NULL y UNIQUE
    private String isbn;

    @Column(nullable = false)
    private int totalStock;

    @Column(nullable = false)
    private int availableStock;

    // Getters y Setters (igual que el modelo de dominio)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    // ... resto de getters/setters
}
```

### Cómo se crea — Ejemplo: `LoanEntity` con relaciones

Cuando una tabla tiene **llaves foráneas** (FK), usas `@ManyToOne`:

```java
@Entity
@Table(name = "loans")
public class LoanEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relación: muchos préstamos → un libro
    // @JoinColumn crea la columna "book_id" con FK a la tabla books
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "book_id")
    private BookEntity book;

    // Relación: muchos préstamos → un usuario
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    private LocalDate loanDate;
    private LocalDate returnDate;
    private boolean returned;

    // Getters y Setters...
}
```

> 💡 `FetchType.LAZY` = no carga el libro/usuario completo hasta que lo necesites.
> `FetchType.EAGER` = lo carga siempre junto con el préstamo (más queries = más lento).

### Cómo se crea — Ejemplo: `UserEntity` con Enum

Para guardar un enum como texto en la BD:

```java
@Entity
@Table(name = "users")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;    // ← Se guarda hasheado (BCrypt)

    @Enumerated(EnumType.STRING)   // ← Guarda "USER" o "LIBRARIAN" como texto
    @Column(nullable = false)
    private Role role;

    // Getters y Setters...
}
```

---

## 4. Crear el DAO

El DAO es una interfaz que **extiende JpaRepository** y te da operaciones CRUD gratis.
Va en `persistence/dao/`.

### Cómo se crea — Ejemplo: `JpaBookDao`

```java
package edu.eci.dosw.DOSW_Library.persistence.dao;

import edu.eci.dosw.DOSW_Library.persistence.entity.BookEntity;
import org.springframework.data.jpa.repository.JpaRepository;

// JpaRepository<TipoEntidad, TipoDelId>
public interface JpaBookDao extends JpaRepository<BookEntity, Long> {

    // Los métodos básicos (save, findById, findAll, deleteById) ya vienen gratis.
    
    // Puedes agregar métodos personalizados usando convenciones de nombre:
    // findBy + NombreCampo → genera el SELECT automáticamente
    Optional<BookEntity> findByIsbn(String isbn);
    // JPA genera: SELECT * FROM books WHERE isbn = ?
}
```

### Cómo se crea — Ejemplo: `JpaLoanDao`

```java
public interface JpaLoanDao extends JpaRepository<LoanEntity, Long> {

    // Buscar todos los préstamos de un usuario
    List<LoanEntity> findByUserId(Long userId);
    // JPA genera: SELECT * FROM loans WHERE user_id = ?

    // Contar préstamos activos (no devueltos) de un usuario
    long countByUserIdAndReturnedFalse(Long userId);
    // JPA genera: SELECT COUNT(*) FROM loans WHERE user_id = ? AND returned = false
}
```

> 💡 La magia de JPA: el nombre del método **es** la query.
> `findBy` + `NombreCampo` + `And` + `OtroCampo` + `True/False` = query automática.

---

## 5. Crear el Mapper de Persistencia

Convierte entre la entidad JPA y el modelo de dominio. Va en `persistence/mapper/`.

### Cómo se crea — Ejemplo: `BookPersistenceMapper`

```java
package edu.eci.dosw.DOSW_Library.persistence.mapper;

import edu.eci.dosw.DOSW_Library.core.model.Book;
import edu.eci.dosw.DOSW_Library.persistence.entity.BookEntity;
import org.springframework.stereotype.Component;

@Component   // ← Spring lo inyecta donde lo necesites
public class BookPersistenceMapper {

    // Entidad JPA → Modelo de dominio (para que el servicio pueda trabajar con él)
    public Book toDomain(BookEntity entity) {
        if (entity == null) return null;
        return new Book(
            entity.getId(),
            entity.getTitle(),
            entity.getAuthor(),
            entity.getIsbn(),
            entity.getTotalStock(),
            entity.getAvailableStock()
        );
    }

    // Modelo de dominio → Entidad JPA (para guardar en BD)
    public BookEntity toEntity(Book book) {
        if (book == null) return null;
        BookEntity entity = new BookEntity();
        entity.setId(book.getId());
        entity.setTitle(book.getTitle());
        entity.setAuthor(book.getAuthor());
        entity.setIsbn(book.getIsbn());
        entity.setTotalStock(book.getTotalStock());
        entity.setAvailableStock(book.getAvailableStock());
        return entity;
    }
}
```

### Cómo se crea — Ejemplo: `LoanPersistenceMapper`

Cuando el modelo tiene objetos anidados (Book, User dentro de Loan):

```java
@Component
public class LoanPersistenceMapper {

    // Necesita los otros mappers para convertir los objetos anidados
    private final BookPersistenceMapper bookMapper;
    private final UserPersistenceMapper userMapper;

    public LoanPersistenceMapper(BookPersistenceMapper bookMapper,
                                  UserPersistenceMapper userMapper) {
        this.bookMapper = bookMapper;
        this.userMapper = userMapper;
    }

    public Loan toDomain(LoanEntity entity) {
        if (entity == null) return null;
        return new Loan(
            entity.getId(),
            bookMapper.toDomain(entity.getBook()),   // ← Convierte BookEntity → Book
            userMapper.toDomain(entity.getUser()),   // ← Convierte UserEntity → User
            entity.getLoanDate(),
            entity.getReturnDate(),
            entity.isReturned()
        );
    }

    public LoanEntity toEntity(Loan loan) {
        if (loan == null) return null;
        LoanEntity entity = new LoanEntity();
        entity.setId(loan.getId());
        entity.setBook(bookMapper.toEntity(loan.getBook()));
        entity.setUser(userMapper.toEntity(loan.getUser()));
        entity.setLoanDate(loan.getLoanDate());
        entity.setReturnDate(loan.getReturnDate());
        entity.setReturned(loan.isReturned());
        return entity;
    }
}
```

---

## 6. Crear la Interfaz de Repositorio de Dominio

Esta interfaz define **qué operaciones** puede hacer el repositorio, sin importar si es JPA, MongoDB u otra cosa. Va en `repository/` (no en `persistence/`).

### Cómo se crea — Ejemplo: `BookRepository`

```java
package edu.eci.dosw.DOSW_Library.repository;

import edu.eci.dosw.DOSW_Library.core.model.Book;
import java.util.List;
import java.util.Optional;

// Interfaz del dominio: trabaja con Book (no BookEntity)
// El servicio solo conoce esta interfaz, no sabe si por debajo usa JPA o MongoDB
public interface BookRepository {

    List<Book> findAll();

    Optional<Book> findById(Long id);   // Optional = puede ser null (libro no encontrado)

    Book save(Book book);               // Crea o actualiza

    void delete(Book book);

    Optional<Book> findByIsbn(String isbn);
}
```

---

## 7. Crear la Implementación del Repositorio

Aquí conectamos la interfaz del dominio con el DAO de JPA. Va en `persistence/repository/`.

### Cómo se crea — Ejemplo: `BookRepositoryImpl`

```java
package edu.eci.dosw.DOSW_Library.persistence.repository;

import edu.eci.dosw.DOSW_Library.core.model.Book;
import edu.eci.dosw.DOSW_Library.persistence.dao.JpaBookDao;
import edu.eci.dosw.DOSW_Library.persistence.mapper.BookPersistenceMapper;
import edu.eci.dosw.DOSW_Library.repository.BookRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository   // ← Marca esta clase como componente de persistencia
public class BookRepositoryImpl implements BookRepository {

    private final JpaBookDao dao;               // ← El DAO de JPA
    private final BookPersistenceMapper mapper; // ← El mapper

    // Constructor (inyección de dependencias)
    public BookRepositoryImpl(JpaBookDao dao, BookPersistenceMapper mapper) {
        this.dao = dao;
        this.mapper = mapper;
    }

    @Override
    public List<Book> findAll() {
        // dao.findAll() → retorna List<BookEntity>
        // .stream().map(mapper::toDomain).toList() → convierte cada entidad a Book
        return dao.findAll()
                  .stream()
                  .map(mapper::toDomain)
                  .toList();
    }

    @Override
    public Optional<Book> findById(Long id) {
        // dao.findById() retorna Optional<BookEntity>
        // .map(mapper::toDomain) convierte el contenido si existe
        return dao.findById(id).map(mapper::toDomain);
    }

    @Override
    public Book save(Book book) {
        // 1. Convertir Book → BookEntity
        // 2. Guardar en BD con JPA
        // 3. Convertir el resultado BookEntity → Book y retornarlo
        return mapper.toDomain(dao.save(mapper.toEntity(book)));
    }

    @Override
    public void delete(Book book) {
        dao.delete(mapper.toEntity(book));
    }

    @Override
    public Optional<Book> findByIsbn(String isbn) {
        return dao.findByIsbn(isbn).map(mapper::toDomain);
    }
}
```

---

## 8. Crear el Servicio

El servicio contiene **la lógica del negocio**. Usa la interfaz de repositorio, nunca el DAO directamente.
Va en `core/service/`.

### Cómo se crea — Ejemplo: `BookService`

```java
package edu.eci.dosw.DOSW_Library.core.service;

import edu.eci.dosw.DOSW_Library.core.exception.BookNotFoundException;
import edu.eci.dosw.DOSW_Library.core.model.Book;
import edu.eci.dosw.DOSW_Library.repository.BookRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service   // ← Marca esta clase como servicio de Spring
public class BookService {

    // Inyectar la INTERFAZ, no la implementación concreta
    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    // Obtener todos los libros
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    // Obtener libro por ID — lanza excepción si no existe
    public Book getBookById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException("Book not found with id: " + id));
        // orElseThrow = si el Optional está vacío, lanza la excepción
    }

    // Crear libro con validaciones
    @Transactional   // ← Si algo falla, hace rollback (deshace todo)
    public Book createBook(Book book) {
        // Si no mandaron availableStock, usar el totalStock
        if (book.getAvailableStock() == 0) {
            book.setAvailableStock(book.getTotalStock());
        }
        // Validar que el stock sea válido
        if (book.getTotalStock() <= 0) {
            throw new IllegalArgumentException("Total stock must be greater than 0");
        }
        return bookRepository.save(book);
    }

    // Actualizar libro
    @Transactional
    public Book updateBook(Long id, Book updatedBook) {
        // 1. Buscar el libro existente (lanza 404 si no existe)
        Book existing = getBookById(id);

        // 2. Actualizar sus campos
        existing.setTitle(updatedBook.getTitle());
        existing.setAuthor(updatedBook.getAuthor());
        existing.setIsbn(updatedBook.getIsbn());
        existing.setTotalStock(updatedBook.getTotalStock());

        // 3. El stock disponible no puede superar el total
        if (updatedBook.getAvailableStock() > updatedBook.getTotalStock()) {
            existing.setAvailableStock(updatedBook.getTotalStock());
        } else {
            existing.setAvailableStock(updatedBook.getAvailableStock());
        }

        // 4. Guardar y retornar
        return bookRepository.save(existing);
    }

    // Eliminar libro
    @Transactional
    public void deleteBook(Long id) {
        Book existing = getBookById(id);   // ← Verifica que existe antes de borrar
        bookRepository.delete(existing);
    }

    // Decrementar stock al prestar
    @Transactional
    public void decrementAvailableCopies(Long id) {
        Book book = getBookById(id);
        if (book.getAvailableStock() <= 0) {
            throw new BookNotAvailableException("Book has no available stock: " + id);
        }
        book.setAvailableStock(book.getAvailableStock() - 1);
        bookRepository.save(book);
    }

    // Incrementar stock al devolver
    @Transactional
    public void incrementAvailableCopies(Long id) {
        Book book = getBookById(id);
        // No puede superar el máximo total
        if (book.getAvailableStock() < book.getTotalStock()) {
            book.setAvailableStock(book.getAvailableStock() + 1);
        }
        bookRepository.save(book);
    }
}
```

---

## 9. Crear los DTOs

Los DTOs definen **qué recibe y qué devuelve** la API. Van en `controller/dto/`.

### Cómo se crea — DTO de Entrada (Request)

```java
package edu.eci.dosw.DOSW_Library.controller.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

// Este objeto es lo que el usuario manda en el body del POST/PUT
public class BookRequestDTO {

    @NotBlank(message = "Title is required")   // ← No puede ser vacío ni null
    private String title;

    @NotBlank(message = "Author is required")
    private String author;

    @NotBlank(message = "ISBN is required")
    private String isbn;

    @Min(value = 1, message = "Total stock must be at least 1")   // ← Mínimo 1
    private int totalStock;

    @Min(value = 0, message = "Available stock cannot be negative")
    private int availableStock;

    // Getters y Setters
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    // ... resto
}
```

### Cómo se crea — DTO de Salida (Response)

```java
// Este objeto es lo que el API retorna al usuario
public class BookResponseDTO {
    private Long id;
    private String title;
    private String author;
    private String isbn;
    private int totalStock;
    private int availableStock;

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    // ...
}
```

### Cómo se crea — DTOs de Autenticación

```java
// Lo que el usuario envía para registrarse
public class UserRequestDTO {
    @NotBlank
    private String name;

    @NotBlank @Email                    // ← Formato de email válido
    private String email;

    @NotBlank @Size(min = 4, max = 50)  // ← Entre 4 y 50 caracteres
    private String username;

    @NotBlank @Size(min = 6, max = 100)
    private String password;

    private Role role;   // Opcional, por defecto USER

    // Getters y Setters...
}

// Lo que el usuario envía para hacer login
public class AuthRequestDTO {
    @NotBlank
    private String username;

    @NotBlank
    private String password;

    // Getters y Setters...
}

// Lo que el API devuelve después del login
public class AuthResponseDTO {
    private String token;

    public AuthResponseDTO(String token) {
        this.token = token;
    }

    public String getToken() { return token; }
}
```

---

## 10. Crear el Mapper de Controller (MapStruct)

MapStruct genera automáticamente el código de conversión. Va en `controller/mapper/`.

### Cómo se crea — Ejemplo: `BookMapper`

```java
package edu.eci.dosw.DOSW_Library.controller.mapper;

import edu.eci.dosw.DOSW_Library.controller.dto.BookRequestDTO;
import edu.eci.dosw.DOSW_Library.controller.dto.BookResponseDTO;
import edu.eci.dosw.DOSW_Library.core.model.Book;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")   // ← "spring" para que Spring lo inyecte como @Bean
public interface BookMapper {

    // Convierte el DTO de entrada al modelo de dominio
    // MapStruct busca campos con el MISMO NOMBRE y los mapea automáticamente
    Book toEntity(BookRequestDTO dto);

    // Convierte el modelo de dominio al DTO de respuesta
    BookResponseDTO toResponse(Book book);
}
```

> 💡 Para que MapStruct funcione, los nombres de los campos en el DTO y en el modelo
> deben ser **iguales**. Si son diferentes, usas:
> ```java
> @Mapping(source = "nombreEnDTO", target = "nombreEnModelo")
> ```

### Cómo se crea — Mapper con campos ignorados

```java
@Mapper(componentModel = "spring")
public interface LoanMapper {

    // Ignora el campo "id" al convertir de request a dominio (el id lo asigna la BD)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "book", ignore = true)   // El book se busca por bookId en el servicio
    @Mapping(target = "user", ignore = true)
    Loan toEntity(LoanRequestDTO dto);

    // Para la respuesta, mapea campos anidados
    @Mapping(source = "book.title", target = "bookTitle")
    @Mapping(source = "user.username", target = "username")
    LoanResponseDTO toResponse(Loan loan);
}
```

---

## 11. Crear el Controller

El controller **recibe las peticiones HTTP** y las delega al servicio. Va en `controller/`.

### Cómo se crea — Ejemplo: `BookController`

```java
package edu.eci.dosw.DOSW_Library.controller;

import edu.eci.dosw.DOSW_Library.controller.dto.BookRequestDTO;
import edu.eci.dosw.DOSW_Library.controller.dto.BookResponseDTO;
import edu.eci.dosw.DOSW_Library.controller.mapper.BookMapper;
import edu.eci.dosw.DOSW_Library.core.model.Book;
import edu.eci.dosw.DOSW_Library.core.service.BookService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController               // ← Combina @Controller + @ResponseBody (devuelve JSON)
@RequestMapping("/api/books") // ← Prefijo de todas las rutas de este controller
public class BookController {

    private final BookService bookService;
    private final BookMapper bookMapper;

    // Inyección por constructor (mejor práctica)
    public BookController(BookService bookService, BookMapper bookMapper) {
        this.bookService = bookService;
        this.bookMapper = bookMapper;
    }

    // GET /api/books → listar todos los libros
    @GetMapping
    @PreAuthorize("hasAnyRole('USER','LIBRARIAN')")   // ← Cualquier usuario autenticado
    public List<BookResponseDTO> getAllBooks() {
        return bookService.getAllBooks()
                          .stream()
                          .map(bookMapper::toResponse)   // Convierte cada Book a DTO
                          .toList();
    }

    // GET /api/books/5 → obtener libro por ID
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER','LIBRARIAN')")
    public BookResponseDTO getBookById(@PathVariable Long id) {
        // @PathVariable extrae el {id} de la URL
        return bookMapper.toResponse(bookService.getBookById(id));
    }

    // POST /api/books → crear libro
    @PostMapping
    @PreAuthorize("hasRole('LIBRARIAN')")            // ← Solo LIBRARIAN
    @ResponseStatus(HttpStatus.CREATED)              // ← Retorna 201 en lugar de 200
    public BookResponseDTO createBook(@Valid @RequestBody BookRequestDTO request) {
        // @Valid activa las validaciones del DTO
        // @RequestBody lee el JSON del body de la petición
        Book book = bookService.createBook(bookMapper.toEntity(request));
        return bookMapper.toResponse(book);
    }

    // PUT /api/books/5 → actualizar libro
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('LIBRARIAN')")
    public BookResponseDTO updateBook(@PathVariable Long id,
                                       @Valid @RequestBody BookRequestDTO request) {
        Book updated = bookService.updateBook(id, bookMapper.toEntity(request));
        return bookMapper.toResponse(updated);
    }

    // DELETE /api/books/5 → eliminar libro
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('LIBRARIAN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)   // ← Retorna 204 (sin body)
    public void deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
    }
}
```

### Cómo se crea — `LoanController` con usuario autenticado

```java
@RestController
@RequestMapping("/api/loans")
public class LoanController {

    private final LoanService loanService;
    private final LoanMapper loanMapper;

    public LoanController(LoanService loanService, LoanMapper loanMapper) {
        this.loanService = loanService;
        this.loanMapper = loanMapper;
    }

    // GET /api/loans → solo LIBRARIAN ve todos los préstamos
    @GetMapping
    @PreAuthorize("hasRole('LIBRARIAN')")
    public List<LoanResponseDTO> getAllLoans() {
        return loanService.getAllLoans().stream().map(loanMapper::toResponse).toList();
    }

    // GET /api/loans/my-loans → cada usuario ve SOLO SUS préstamos
    @GetMapping("/my-loans")
    @PreAuthorize("hasAnyRole('USER','LIBRARIAN')")
    public List<LoanResponseDTO> getMyLoans(@AuthenticationPrincipal User user) {
        // @AuthenticationPrincipal inyecta el usuario del token JWT actual
        return loanService.getLoansForUser(user.getId())
                          .stream().map(loanMapper::toResponse).toList();
    }

    // POST /api/loans → crear préstamo (el usuario del token es el dueño)
    @PostMapping
    @PreAuthorize("hasAnyRole('USER','LIBRARIAN')")
    @ResponseStatus(HttpStatus.CREATED)
    public LoanResponseDTO createLoan(@AuthenticationPrincipal User user,
                                       @Valid @RequestBody LoanRequestDTO request) {
        // user.getId() viene del token JWT, no del body → el usuario NO puede prestarse en nombre de otro
        Loan loan = loanService.createLoan(user.getId(), request.getBookId());
        return loanMapper.toResponse(loan);
    }

    // POST /api/loans/5/return → devolver un préstamo
    @PostMapping("/{id}/return")
    @PreAuthorize("hasAnyRole('USER','LIBRARIAN')")
    public LoanResponseDTO returnLoan(@PathVariable Long id,
                                       @AuthenticationPrincipal User user) {
        return loanMapper.toResponse(
            loanService.returnLoan(id, user.getId(), user.getRole())
        );
    }
}
```

### Cómo se crea — `AuthController`

```java
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // POST /api/auth/register → registrar nuevo usuario y retornar token
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponseDTO register(@Valid @RequestBody UserRequestDTO request) {
        return authService.register(request);
    }

    // POST /api/auth/login → iniciar sesión y retornar token
    @PostMapping("/login")
    public AuthResponseDTO login(@Valid @RequestBody AuthRequestDTO request) {
        return authService.login(request);
    }
}
```

---

## 12. Crear las Excepciones Personalizadas

Las excepciones del dominio van en `core/exception/`.

### Cómo se crean

```java
// Para cuando no se encuentra un libro
public class BookNotFoundException extends RuntimeException {
    public BookNotFoundException(String message) {
        super(message);   // Llama al constructor de RuntimeException con el mensaje
    }
}

// Para cuando el libro no tiene stock
public class BookNotAvailableException extends RuntimeException {
    public BookNotAvailableException(String message) {
        super(message);
    }
}

// Para cuando un usuario intenta acceder a algo que no le pertenece
public class UnauthorizedOperationException extends RuntimeException {
    public UnauthorizedOperationException(String message) {
        super(message);
    }
}

// Para cuando se supera el límite de préstamos
public class LoanLimitExceededException extends RuntimeException {
    public LoanLimitExceededException(String message) {
        super(message);
    }
}
```

> 💡 Extienden `RuntimeException` (no `Exception`) para que no sea necesario declarar
> `throws` en los métodos — Spring las captura automáticamente.

---

## 13. Crear el GlobalExceptionHandler

Centraliza todos los errores de la aplicación. Va en `controller/`.

### Cómo se crea

```java
package edu.eci.dosw.DOSW_Library.controller;

import edu.eci.dosw.DOSW_Library.core.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice   // ← Aplica a todos los @RestController del proyecto
public class GlobalExceptionHandler {

    // Maneja BookNotFoundException → HTTP 404
    @ExceptionHandler(BookNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleBookNotFound(BookNotFoundException ex) {
        return Map.of("error", ex.getMessage());
        // Respuesta: {"error": "Book not found with id: 99"}
    }

    // Maneja BookNotAvailableException → HTTP 422
    @ExceptionHandler(BookNotAvailableException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public Map<String, String> handleBookNotAvailable(BookNotAvailableException ex) {
        return Map.of("error", ex.getMessage());
    }

    // Maneja UnauthorizedOperationException → HTTP 403
    @ExceptionHandler(UnauthorizedOperationException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Map<String, String> handleUnauthorized(UnauthorizedOperationException ex) {
        return Map.of("error", ex.getMessage());
    }

    // Maneja LoanLimitExceededException → HTTP 422
    @ExceptionHandler(LoanLimitExceededException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public Map<String, String> handleLoanLimit(LoanLimitExceededException ex) {
        return Map.of("error", ex.getMessage());
    }

    // Maneja errores de validación (@Valid) → HTTP 400
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        // Itera por cada campo que falló la validación
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            errors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }
        return errors;
        // Respuesta: {"title": "Title is required", "isbn": "ISBN is required"}
    }

    // Maneja cualquier otra excepción no controlada → HTTP 500
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> handleGeneral(Exception ex) {
        return Map.of("error", "Internal server error: " + ex.getMessage());
    }
}
```

---

## 14. Implementar la Seguridad JWT

### Paso 1 — Crear el JwtService

```java
package edu.eci.dosw.DOSW_Library.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {

    private final SecretKey signingKey;
    private final long expirationMillis;

    // @Value inyecta el valor desde application.yaml
    public JwtService(
            @Value("${security.jwt.secret}") String secret,
            @Value("${security.jwt.expiration-millis}") long expirationMillis) {
        this.signingKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMillis = expirationMillis;
    }

    // Genera un token para el usuario
    public String generateToken(UserDetails userDetails) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + expirationMillis);

        return Jwts.builder()
                .subject(userDetails.getUsername())    // Username en el "sub" del token
                .issuedAt(now)                         // Fecha de creación
                .expiration(expiration)                // Fecha de expiración
                .signWith(signingKey, SignatureAlgorithm.HS256)  // Firma digital
                .compact();                            // Genera el String del token
    }

    // Verifica si un token es válido para el usuario dado
    public boolean isTokenValid(String token, UserDetails userDetails) {
        String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    // Extrae el username del token
    public String extractUsername(String token) {
        return extractClaims(token).getSubject();
    }

    private boolean isTokenExpired(String token) {
        return extractClaims(token).getExpiration().before(new Date());
    }

    // Parsea y verifica la firma del token
    private Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(signingKey)   // ← Verifica con la misma clave
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
```

### Paso 2 — Crear el CustomUserDetailsService

```java
package edu.eci.dosw.DOSW_Library.security;

import edu.eci.dosw.DOSW_Library.repository.UserRepository;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Spring Security llama a este método cuando necesita verificar credenciales
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        // User implementa UserDetails, por eso se puede retornar directamente
    }
}
```

### Paso 3 — Crear el JwtAuthenticationFilter

```java
package edu.eci.dosw.DOSW_Library.security;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

@Component
// OncePerRequestFilter garantiza que se ejecute UNA sola vez por petición
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtService jwtService,
                                    CustomUserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // 1. Leer el header Authorization
        String authHeader = request.getHeader("Authorization");

        // 2. Si no tiene token o no tiene formato "Bearer xxx", pasar sin autenticar
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 3. Extraer el token (sin "Bearer ")
        String token = authHeader.substring(7);

        // 4. Extraer el username del token
        String username = jwtService.extractUsername(token);

        // 5. Solo procesar si hay username y NO hay autenticación previa en el contexto
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // 6. Cargar el usuario desde la BD
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            // 7. Validar el token
            if (jwtService.isTokenValid(token, userDetails)) {

                // 8. Crear objeto de autenticación y guardarlo en el SecurityContext
                UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()   // ← Los roles del usuario
                    );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // 9. Continuar con la cadena de filtros
        filterChain.doFilter(request, response);
    }
}
```

### Paso 4 — Crear el SecurityConfig

```java
package edu.eci.dosw.DOSW_Library.security;

import org.springframework.context.annotation.*;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.*;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity   // ← Habilita el uso de @PreAuthorize en los controllers
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;
    private final CustomUserDetailsService userDetailsService;

    public SecurityConfig(JwtAuthenticationFilter jwtFilter,
                          CustomUserDetailsService userDetailsService) {
        this.jwtFilter = jwtFilter;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
            .csrf(csrf -> csrf.disable())          // Deshabilitar CSRF (no aplica a APIs REST)
            .cors(Customizer.withDefaults())        // Habilitar CORS

            .authorizeHttpRequests(auth -> auth
                // Endpoints públicos (sin token)
                .requestMatchers(
                    "/api/auth/**",
                    "/swagger-ui/**",
                    "/v3/api-docs/**",
                    "/swagger-ui.html"
                ).permitAll()

                // GET de libros: cualquier usuario autenticado
                .requestMatchers(HttpMethod.GET, "/api/books/**").hasAnyRole("USER", "LIBRARIAN")

                // Modificar libros: solo LIBRARIAN
                .requestMatchers("/api/books/**").hasRole("LIBRARIAN")

                // Usuarios: solo LIBRARIAN
                .requestMatchers("/api/users/**").hasRole("LIBRARIAN")

                // Préstamos: usuarios autenticados (con restricciones adicionales en @PreAuthorize)
                .requestMatchers("/api/loans/**").hasAnyRole("USER", "LIBRARIAN")

                // Todo lo demás requiere autenticación
                .anyRequest().authenticated()
            )

            // Sin sesiones en servidor (cada request es independiente)
            .sessionManagement(sm -> sm
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            .authenticationProvider(authenticationProvider())

            // Agregar nuestro filtro JWT ANTES del filtro estándar de usuario/contraseña
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)

            .build();
    }

    // Configura cómo se verifican las credenciales
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    // Para poder llamar a authenticationManager.authenticate() en AuthService
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
            throws Exception {
        return config.getAuthenticationManager();
    }

    // BCrypt para hashear contraseñas
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```

### Paso 5 — Crear el AuthService

```java
@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserService userService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(AuthenticationManager authenticationManager,
                       JwtService jwtService, UserService userService,
                       UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userService = userService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Registrar nuevo usuario
    public AuthResponseDTO register(UserRequestDTO request) {
        // Verificar que el username no esté en uso
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());  // UserService hashea la contraseña
        user.setRole(request.getRole() == null ? Role.USER : request.getRole());

        User created = userService.createUser(user);
        // Generar y retornar token directamente (el usuario queda autenticado)
        return new AuthResponseDTO(jwtService.generateToken(created));
    }

    // Login
    public AuthResponseDTO login(AuthRequestDTO request) {
        // authenticationManager verifica usuario y contraseña contra la BD
        // Si son incorrectos, lanza BadCredentialsException → 401 automático
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.getUsername(),
                request.getPassword()
            )
        );

        // Si llega aquí, las credenciales son correctas
        User user = userService.findByUsername(request.getUsername());
        return new AuthResponseDTO(jwtService.generateToken(user));
    }
}
```

> 💡 En `UserService.createUser()` asegúrate de hashear la contraseña:
> ```java
> user.setPassword(passwordEncoder.encode(user.getPassword()));
> ```

---

## 15. Flujo completo de prueba con Swagger

### Paso 1 — Arrancar la aplicación

```bash
./mvnw spring-boot:run
```

### Paso 2 — Abrir Swagger UI

```
http://localhost:8080/swagger-ui.html
```

### Paso 3 — Registrar un usuario

```json
POST /api/auth/register
{
  "name": "Carlos Admin",
  "email": "carlos@biblioteca.com",
  "username": "carlos",
  "password": "123456",
  "role": "LIBRARIAN"
}
```
**Respuesta:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJjYXJsb3MiLCJpYXQi..."
}
```

### Paso 4 — Autorizarte en Swagger

1. Copia el token de la respuesta
2. Haz clic en el botón **Authorize** 🔒 (arriba a la derecha en Swagger)
3. Escribe: `Bearer eyJhbGciOiJIUzI1NiJ9...`
4. Clic en **Authorize**

### Paso 5 — Crear un libro

```json
POST /api/books
{
  "title": "Clean Code",
  "author": "Robert C. Martin",
  "isbn": "978-0132350884",
  "totalStock": 5,
  "availableStock": 5
}
```

### Paso 6 — Crear un usuario normal y hacer un préstamo

```json
POST /api/auth/register
{
  "name": "Laura Usuario",
  "email": "laura@biblioteca.com",
  "username": "laura",
  "password": "123456",
  "role": "USER"
}
```

Luego autentícate con el token de Laura y crea un préstamo:

```json
POST /api/loans
{
  "bookId": 1
}
```

### Paso 7 — Verificar que el stock se redujo

```
GET /api/books/1
```

El `availableStock` debería haber bajado de 5 a 4.

### Paso 8 — Devolver el préstamo

```
POST /api/loans/1/return
```

El `availableStock` debería volver a 5.

---

## 📌 Resumen de lo que debes crear para agregar un nuevo recurso

Si el parcial pide agregar, por ejemplo, una entidad `Reservation`, el orden es:

```
1. core/model/Reservation.java              ← Modelo de dominio
2. persistence/entity/ReservationEntity.java ← Entidad JPA (@Entity)
3. persistence/dao/JpaReservationDao.java    ← Interfaz JpaRepository
4. persistence/mapper/ReservationPersistenceMapper.java ← Entity ↔ Dominio
5. repository/ReservationRepository.java    ← Interfaz de dominio
6. persistence/repository/ReservationRepositoryImpl.java ← Implementación
7. core/service/ReservationService.java     ← Lógica de negocio
8. controller/dto/ReservationRequestDTO.java ← DTO de entrada
9. controller/dto/ReservationResponseDTO.java ← DTO de salida
10. controller/mapper/ReservationMapper.java ← DTO ↔ Dominio (MapStruct)
11. controller/ReservationController.java   ← Endpoints HTTP
12. core/exception/ReservationNotFoundException.java ← Excepción
13. Agregar el manejo en GlobalExceptionHandler
```

---

*Proyecto: DOSW Library API — Escuela Colombiana de Ingeniería Julio Garavito*
