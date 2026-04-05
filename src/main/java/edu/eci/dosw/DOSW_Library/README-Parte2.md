# 📚 DOSW Library API — Guía Completa Parte 2
### Guía de estudio para parcial práctico — Todo explicado desde cero

---

## 📋 Tabla de Contenido

1. [¿Qué construimos?](#1-qué-construimos)
2. [Arquitectura del proyecto](#2-arquitectura-del-proyecto)
3. [Capa de Persistencia Relacional (JPA + PostgreSQL)](#3-capa-de-persistencia-relacional-jpa--postgresql)
4. [Seguridad con JWT](#4-seguridad-con-jwt)
5. [Autorización por Roles](#5-autorización-por-roles)
6. [DTOs, Mappers y Validaciones](#6-dtos-mappers-y-validaciones)
7. [Manejo de Errores](#7-manejo-de-errores)
8. [Reglas de Negocio Importantes](#8-reglas-de-negocio-importantes)
9. [Cómo correr el proyecto](#9-cómo-correr-el-proyecto)
10. [Flujo completo de una petición](#10-flujo-completo-de-una-petición)
11. [Cheatsheet de anotaciones](#11-cheatsheet-de-anotaciones)

---

## 1. ¿Qué construimos?

Una **API REST** para gestionar una biblioteca. Permite:

- 📖 Gestionar libros (crear, leer, actualizar, eliminar)
- 👤 Gestionar usuarios (registrar, consultar)
- 📋 Gestionar préstamos (solicitar, devolver)
- 🔐 Autenticar usuarios con usuario y contraseña
- 🛡️ Autorizar operaciones según el rol del usuario

**Dos tipos de usuario:**

| Rol | Puede hacer |
|-----|-------------|
| `LIBRARIAN` (Bibliotecario) | Todo: gestionar libros, usuarios y préstamos |
| `USER` (Usuario normal) | Ver libros, solicitar préstamos, devolver sus propios libros |

---

## 2. Arquitectura del proyecto

```
src/main/java/.../
├── controller/          ← Recibe las peticiones HTTP (endpoints)
│   ├── dto/             ← Objetos de entrada/salida (lo que el usuario envía/recibe)
│   └── mapper/          ← Convierte DTO ↔ Modelo de dominio
│
├── core/                ← Lógica del negocio (las reglas)
│   ├── model/           ← Objetos del dominio: Book, User, Loan
│   ├── service/         ← Servicios con la lógica principal
│   ├── exception/       ← Excepciones personalizadas
│   ├── validator/       ← Validaciones de negocio
│   └── strategy/        ← Patrones de política de préstamos
│
├── persistence/         ← Comunicación con la base de datos
│   ├── entity/          ← Clases mapeadas a tablas SQL
│   ├── dao/             ← Interfaces JPA (CRUD automático)
│   ├── mapper/          ← Convierte Entity ↔ Modelo de dominio
│   └── repository/      ← Implementaciones concretas del repositorio
│
├── repository/          ← Interfaces genéricas de repositorio (dominio)
│
└── security/            ← Todo lo relacionado con JWT y Spring Security
```

### ¿Por qué tantas capas?

**Porque cada capa tiene una sola responsabilidad:**

```
HTTP Request
     ↓
 Controller    → Solo recibe y responde HTTP
     ↓
  Service      → Solo aplica reglas de negocio
     ↓
 Repository    → Solo habla con la base de datos
     ↓
 Base de datos
```

Si mañana cambias de PostgreSQL a MongoDB, solo tocas la capa `persistence/`.
El resto del código NO se entera.

---

## 3. Capa de Persistencia Relacional (JPA + PostgreSQL)

### 3.1 ¿Qué es JPA?

JPA (Java Persistence API) es una herramienta que **convierte objetos Java a filas de una tabla SQL automáticamente**. No tienes que escribir `INSERT INTO`, `SELECT * FROM`, etc. — JPA lo hace por ti.

### 3.2 Dependencias en pom.xml

```xml
<!-- JPA + Hibernate (implementación de JPA) -->
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
```

### 3.3 Configuración de conexión (application.yaml)

```yaml
spring:
  datasource:
    url: ${DB_URL:jdbc:postgresql://localhost:5432/librarydb}
    username: ${DB_USERNAME:postgres}
    password: ${DB_PASSWORD:postgres}
    driver-class-name: org.postgresql.Driver
  jpa:
    hibernate:
      ddl-auto: update   # Crea/actualiza las tablas automáticamente
    show-sql: true       # Muestra las queries SQL en consola (útil para debug)
```

> 💡 La sintaxis `${VARIABLE:valor_default}` significa: "usa la variable de entorno,
> y si no existe, usa el valor por defecto". Esto es importante para poder cambiar
> la base de datos en producción sin tocar el código.

### 3.4 Entidades (Entity)

Una **Entity** es una clase Java que representa una tabla en la base de datos. Cada instancia de la clase = una fila de la tabla.

```java
@Entity                        // ← Le dice a JPA que esto es una tabla
@Table(name = "books")         // ← El nombre de la tabla en la BD
public class BookEntity {

    @Id                                              // ← Esta es la llave primaria
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // ← Auto-incremental (1, 2, 3...)
    private Long id;

    @Column(nullable = false)   // ← NOT NULL en la base de datos
    private String title;

    @Column(nullable = false, unique = true)  // ← NOT NULL + UNIQUE
    private String isbn;

    @Column(nullable = false)
    private int totalStock;

    @Column(nullable = false)
    private int availableStock;
    
    // getters y setters...
}
```

**Tabla que genera JPA en PostgreSQL:**
```sql
CREATE TABLE books (
    id            BIGSERIAL PRIMARY KEY,
    title         VARCHAR NOT NULL,
    isbn          VARCHAR NOT NULL UNIQUE,
    total_stock   INT NOT NULL,
    available_stock INT NOT NULL
);
```

**LoanEntity — con relaciones entre tablas:**

```java
@Entity
@Table(name = "loans")
public class LoanEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "book_id")   // ← Crea columna book_id (FK)
    private BookEntity book;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")   // ← Crea columna user_id (FK)
    private UserEntity user;

    private LocalDate loanDate;
    private LocalDate returnDate;
    private boolean returned;
}
```

> 💡 `@ManyToOne` = "Muchos préstamos pueden tener el mismo libro".
> `FetchType.LAZY` = no cargar el libro completo hasta que lo necesites (mejor rendimiento).

### 3.5 DAOs (Interfaces JPA)

Un **DAO** (Data Access Object) es una interfaz que te da operaciones CRUD gratis sin escribir código:

```java
public interface JpaBookDao extends JpaRepository<BookEntity, Long> {
    // JpaRepository ya te da: save(), findById(), findAll(), deleteById(), etc.
    
    // Puedes agregar métodos personalizados con nombres especiales:
    Optional<BookEntity> findByIsbn(String isbn);  // ← JPA genera el SQL automáticamente
}
```

**Lo que obtienes gratis al extender `JpaRepository`:**

| Método | SQL equivalente |
|--------|----------------|
| `save(entity)` | `INSERT` o `UPDATE` |
| `findById(id)` | `SELECT * FROM table WHERE id = ?` |
| `findAll()` | `SELECT * FROM table` |
| `deleteById(id)` | `DELETE FROM table WHERE id = ?` |
| `count()` | `SELECT COUNT(*) FROM table` |

### 3.6 Interfaces de Repositorio de Dominio

Estas interfaces son la "promesa" de lo que puede hacer el repositorio, **sin importar si usa JPA, MongoDB u otra cosa**:

```java
// En el paquete repository/ (no persistence/)
public interface BookRepository {
    List<Book> findAll();
    Optional<Book> findById(Long id);
    Book save(Book book);
    void delete(Book book);
    Optional<Book> findByIsbn(String isbn);
}
```

> 💡 Nota que trabaja con `Book` (modelo de dominio), NO con `BookEntity`.
> El servicio no sabe nada de JPA. Eso es lo que hace la arquitectura limpia.

### 3.7 Mappers de Persistencia

Convierten entre la entidad JPA y el modelo de dominio:

```java
@Component
public class BookPersistenceMapper {

    // Entidad → Modelo de dominio
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

    // Modelo de dominio → Entidad
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

### 3.8 Implementación concreta del Repositorio

Aquí conectamos el DAO de JPA con la interfaz del dominio:

```java
@Repository
public class BookRepositoryImpl implements BookRepository {

    private final JpaBookDao dao;
    private final BookPersistenceMapper mapper;

    public BookRepositoryImpl(JpaBookDao dao, BookPersistenceMapper mapper) {
        this.dao = dao;
        this.mapper = mapper;
    }

    @Override
    public List<Book> findAll() {
        return dao.findAll()
                  .stream()
                  .map(mapper::toDomain)   // ← Convierte cada entidad a modelo
                  .toList();
    }

    @Override
    public Optional<Book> findById(Long id) {
        return dao.findById(id).map(mapper::toDomain);
    }

    @Override
    public Book save(Book book) {
        BookEntity entity = mapper.toEntity(book);
        BookEntity saved = dao.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public void delete(Book book) {
        dao.delete(mapper.toEntity(book));
    }
}
```

---

## 4. Seguridad con JWT

### 4.1 ¿Qué es JWT?

**JWT (JSON Web Token)** es un "carnet digital" que el servidor le da al usuario cuando inicia sesión. Ese carnet:
- Contiene información del usuario (id, rol)
- Está firmado digitalmente (nadie puede modificarlo sin que se detecte)
- Tiene fecha de expiración

```
┌─────────────────────────────────────────────────────────┐
│                        JWT Token                        │
├──────────────┬──────────────────┬───────────────────────┤
│   HEADER     │     PAYLOAD      │      SIGNATURE        │
│  (algoritmo) │  (datos usuario) │  (firma digital)      │
│  HS256       │  id: 1           │  HMACSHA256(          │
│              │  role: USER      │    header+payload,    │
│              │  exp: 86400000   │    secretKey          │
│              │                  │  )                    │
└──────────────┴──────────────────┴───────────────────────┘
```

### 4.2 Dependencias JWT en pom.xml

```xml
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
```

### 4.3 JwtService — Generar y validar tokens

```java
@Service
public class JwtService {

    @Value("${security.jwt.secret}")
    private String secretKey;

    @Value("${security.jwt.expiration-millis}")
    private long expirationMillis;

    // Genera un token para el usuario
    public String generateToken(User user) {
        return Jwts.builder()
                .subject(user.getUsername())
                .claim("id", user.getId())         // ← Guarda el ID en el token
                .claim("role", user.getRole())     // ← Guarda el rol en el token
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationMillis))
                .signWith(getSigningKey())          // ← Firma digital
                .compact();
    }

    // Valida si un token es válido
    public boolean isTokenValid(String token, UserDetails userDetails) {
        String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    // Extrae el nombre de usuario del token
    public String extractUsername(String token) {
        return extractClaims(token).getSubject();
    }

    private boolean isTokenExpired(String token) {
        return extractClaims(token).getExpiration().before(new Date());
    }

    private Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())   // ← Verifica la firma
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
    }
}
```

### 4.4 JwtAuthenticationFilter — Interceptar cada request

Este filtro se ejecuta **antes de cada petición HTTP**. Revisa si viene un token válido y, si es así, carga el usuario en el contexto de seguridad.

```java
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    // Constructor...

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // 1. Extraer el header "Authorization"
        String authHeader = request.getHeader("Authorization");

        // 2. Si no hay token o no empieza con "Bearer ", continuar sin autenticar
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 3. Extraer el token (quitar "Bearer ")
        String token = authHeader.substring(7);

        // 4. Extraer el username del token
        String username = jwtService.extractUsername(token);

        // 5. Si hay username y el contexto no tiene autenticación previa
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            // 6. Validar el token
            if (jwtService.isTokenValid(token, userDetails)) {
                // 7. Crear la autenticación y guardarla en el contexto
                UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }

        // 8. Continuar con la cadena de filtros
        filterChain.doFilter(request, response);
    }
}
```

**Flujo visual:**

```
Request llega
     ↓
¿Tiene header "Authorization: Bearer <token>"?
     ↓ NO → continuar sin autenticar (Spring Security rechazará si el endpoint lo requiere)
     ↓ SÍ
Extraer y validar token
     ↓ Inválido → continuar sin autenticar (401 automático)
     ↓ Válido
Cargar usuario en SecurityContext
     ↓
Continuar → llega al Controller
```

### 4.5 CustomUserDetailsService

Spring Security necesita una forma de cargar el usuario por nombre. Esto lo hace `UserDetailsService`:

```java
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }
}
```

> 💡 `User` implementa `UserDetails` (de Spring Security), por eso puede retornarse directamente.

### 4.6 SecurityConfig — Configuración central de seguridad

```java
@Configuration
@EnableMethodSecurity   // ← Habilita @PreAuthorize en los controllers
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomUserDetailsService customUserDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
            // Deshabilitar CSRF (no aplica para APIs REST con JWT)
            .csrf(csrf -> csrf.disable())

            // Configurar CORS (permitir peticiones de otros dominios)
            .cors(Customizer.withDefaults())

            // Reglas de acceso por URL
            .authorizeHttpRequests(auth -> auth
                // Endpoints públicos (no necesitan token)
                .requestMatchers("/api/auth/**", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                // GET de libros: cualquier usuario autenticado
                .requestMatchers(HttpMethod.GET, "/api/books/**").hasAnyRole("USER", "LIBRARIAN")
                // Modificar libros: solo LIBRARIAN
                .requestMatchers("/api/books/**").hasRole("LIBRARIAN")
                // Gestión de usuarios: solo LIBRARIAN
                .requestMatchers("/api/users/**").hasRole("LIBRARIAN")
                // Préstamos: cualquier usuario autenticado (con restricciones por @PreAuthorize)
                .requestMatchers("/api/loans/**").hasAnyRole("USER", "LIBRARIAN")
                // Todo lo demás: autenticado
                .anyRequest().authenticated()
            )

            // Sin sesiones en el servidor (stateless)
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            // Registrar nuestro filtro JWT ANTES del filtro de usuario/contraseña
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)

            .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();  // ← Hashea contraseñas con BCrypt
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
```

### 4.7 AuthController — Endpoint de Login

```java
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody AuthRequestDTO request) {
        String token = authService.login(request.getUsername(), request.getPassword());
        return ResponseEntity.ok(new AuthResponseDTO(token));
    }
}
```

**AuthService:**
```java
@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    public String login(String username, String password) {
        // 1. Verificar credenciales (lanza excepción si son incorrectas)
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(username, password)
        );

        // 2. Cargar el usuario desde la BD
        User user = (User) userDetailsService.loadUserByUsername(username);

        // 3. Generar y retornar el token
        return jwtService.generateToken(user);
    }
}
```

**Ejemplo de uso con curl:**

```bash
# Login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "admin", "password": "1234"}'

# Respuesta:
# {"token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbiIsImlkIjoxLCJyb2xlIjoiTElCUkFSSUFOIn0..."}

# Usar el token en otra petición
curl -X GET http://localhost:8080/api/books \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9..."
```

---

## 5. Autorización por Roles

### 5.1 Enum de Roles

```java
public enum Role {
    USER,       // Usuario normal
    LIBRARIAN   // Bibliotecario (administrador)
}
```

### 5.2 @PreAuthorize en Controllers

`@PreAuthorize` evalúa una expresión **antes** de ejecutar el método. Si la condición no se cumple, lanza 403 Forbidden.

```java
@RestController
@RequestMapping("/api/books")
public class BookController {

    // ✅ USER y LIBRARIAN pueden ver libros
    @GetMapping
    @PreAuthorize("hasAnyRole('USER','LIBRARIAN')")
    public List<BookResponseDTO> getAllBooks() { ... }

    // ✅ Solo LIBRARIAN puede crear libros
    @PostMapping
    @PreAuthorize("hasRole('LIBRARIAN')")
    public BookResponseDTO createBook(@Valid @RequestBody BookRequestDTO request) { ... }

    // ✅ Solo LIBRARIAN puede eliminar libros
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('LIBRARIAN')")
    public void deleteBook(@PathVariable Long id) { ... }
}
```

```java
@RestController
@RequestMapping("/api/loans")
public class LoanController {

    // Solo LIBRARIAN ve todos los préstamos
    @GetMapping
    @PreAuthorize("hasRole('LIBRARIAN')")
    public List<LoanResponseDTO> getAllLoans() { ... }

    // Cada usuario ve SUS préstamos — @AuthenticationPrincipal inyecta el usuario actual
    @GetMapping("/my-loans")
    @PreAuthorize("hasAnyRole('USER','LIBRARIAN')")
    public List<LoanResponseDTO> getMyLoans(@AuthenticationPrincipal User user) {
        return loanService.getLoansForUser(user.getId())  // ← Solo los suyos
                          .stream().map(loanMapper::toResponse).toList();
    }

    // Crear préstamo — el usuario del token es el dueño del préstamo
    @PostMapping
    @PreAuthorize("hasAnyRole('USER','LIBRARIAN')")
    public LoanResponseDTO createLoan(@AuthenticationPrincipal User user,
                                       @Valid @RequestBody LoanRequestDTO request) {
        Loan loan = loanService.createLoan(user.getId(), request.getBookId());
        return loanMapper.toResponse(loan);
    }
}
```

### 5.3 Respuestas HTTP según error de seguridad

| Situación | Código HTTP | Cuándo ocurre |
|-----------|------------|---------------|
| Sin token | **401 Unauthorized** | Petición a endpoint protegido sin `Authorization` header |
| Token inválido/expirado | **401 Unauthorized** | El token está mal firmado o ya expiró |
| Token válido pero rol incorrecto | **403 Forbidden** | Un USER intentando `POST /api/books` |

---

## 6. DTOs, Mappers y Validaciones

### 6.1 ¿Qué es un DTO?

Un **DTO (Data Transfer Object)** es un objeto que define exactamente qué campos se reciben o se envían en la API. Evita exponer directamente los modelos internos.

```
Usuario envía JSON → BookRequestDTO → BookService → Book (dominio) → BookEntity (BD)
                                                          ↑
BD → BookEntity → Book (dominio) → BookResponseDTO → JSON que recibe el usuario
```

**BookRequestDTO — Lo que el usuario envía al crear un libro:**

```java
public class BookRequestDTO {

    @NotBlank(message = "Title is required")   // ← Validación: no puede estar vacío
    private String title;

    @NotBlank(message = "Author is required")
    private String author;

    @NotBlank(message = "ISBN is required")
    private String isbn;

    @Min(value = 1, message = "Total stock must be at least 1")  // ← Mínimo 1
    private int totalStock;

    @Min(value = 0, message = "Available stock cannot be negative")
    private int availableStock;

    // getters y setters...
}
```

**BookResponseDTO — Lo que el usuario recibe:**

```java
public class BookResponseDTO {
    private Long id;
    private String title;
    private String author;
    private String isbn;
    private int totalStock;
    private int availableStock;
    // getters y setters...
}
```

### 6.2 Mappers con MapStruct

**MapStruct** genera automáticamente el código de conversión entre objetos. Solo defines la interfaz y MapStruct hace el resto en tiempo de compilación.

```java
@Mapper(componentModel = "spring")   // ← Spring lo inyecta como @Bean
public interface BookMapper {

    // Convierte DTO de request → Modelo de dominio
    Book toEntity(BookRequestDTO dto);

    // Convierte Modelo de dominio → DTO de response
    BookResponseDTO toResponse(Book book);
}
```

> 💡 MapStruct busca campos con el mismo nombre y los mapea automáticamente.
> Si los nombres son distintos, usas `@Mapping(source = "x", target = "y")`.

**Uso en el Controller:**

```java
@PostMapping
@PreAuthorize("hasRole('LIBRARIAN')")
public BookResponseDTO createBook(@Valid @RequestBody BookRequestDTO request) {
    Book book = bookMapper.toEntity(request);        // DTO → Dominio
    Book created = bookService.createBook(book);     // Lógica de negocio
    return bookMapper.toResponse(created);           // Dominio → DTO de respuesta
}
```

### 6.3 @Valid — Activar validaciones

Para que las anotaciones `@NotBlank`, `@Min`, etc. funcionen, debes poner `@Valid` antes del `@RequestBody`:

```java
@PostMapping
public BookResponseDTO createBook(@Valid @RequestBody BookRequestDTO request) {
    //                             ↑ Sin esto, las validaciones no se ejecutan
}
```

Si la validación falla, Spring lanza `MethodArgumentNotValidException` que el `GlobalExceptionHandler` captura.

---

## 7. Manejo de Errores

### 7.1 Excepciones Personalizadas

```java
// Cuando no se encuentra un libro
public class BookNotFoundException extends RuntimeException {
    public BookNotFoundException(String message) {
        super(message);
    }
}

// Cuando el libro no tiene stock disponible
public class BookNotAvailableException extends RuntimeException {
    public BookNotAvailableException(String message) {
        super(message);
    }
}

// Cuando un usuario intenta hacer algo que no le corresponde
public class UnauthorizedOperationException extends RuntimeException {
    public UnauthorizedOperationException(String message) {
        super(message);
    }
}
```

### 7.2 GlobalExceptionHandler

Centraliza el manejo de todos los errores y devuelve respuestas JSON limpias:

```java
@RestControllerAdvice   // ← Aplica a todos los controllers
public class GlobalExceptionHandler {

    // Libro no encontrado → 404
    @ExceptionHandler(BookNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleBookNotFound(BookNotFoundException ex) {
        return Map.of("error", ex.getMessage());
    }

    // Libro sin stock → 422
    @ExceptionHandler(BookNotAvailableException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public Map<String, String> handleBookNotAvailable(BookNotAvailableException ex) {
        return Map.of("error", ex.getMessage());
    }

    // Validaciones fallidas (@Valid) → 400
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
            errors.put(error.getField(), error.getDefaultMessage())
        );
        return errors;
    }

    // Operación no autorizada → 403
    @ExceptionHandler(UnauthorizedOperationException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Map<String, String> handleUnauthorized(UnauthorizedOperationException ex) {
        return Map.of("error", ex.getMessage());
    }
}
```

**Ejemplo de respuesta de error:**
```json
// POST /api/books con campos vacíos
{
  "title": "Title is required",
  "author": "Author is required"
}

// GET /api/books/999 (no existe)
{
  "error": "Book not found with id: 999"
}
```

---

## 8. Reglas de Negocio Importantes

### 8.1 Control de Stock de Libros

```java
@Service
public class BookService {

    // Decrementar stock al crear préstamo
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
        // No puede superar el stock total
        if (book.getAvailableStock() < book.getTotalStock()) {
            book.setAvailableStock(book.getAvailableStock() + 1);
        }
        bookRepository.save(book);
    }
}
```

### 8.2 Crear un Préstamo

```java
@Transactional
public Loan createLoan(Long userId, Long bookId) {
    User user = userService.getUserById(userId);
    Book book = bookService.getBookById(bookId);

    // Regla 1: El libro debe tener stock disponible
    if (book.getAvailableStock() <= 0) {
        throw new BookNotAvailableException("Book has no available stock");
    }

    // Regla 2: El usuario no puede superar el límite de préstamos activos
    LoanPolicyStrategy policy = loanPolicyContext.getPolicy(user.getRole());
    long activeLoans = loanRepository.countByUserIdAndReturnedFalse(userId);
    if (activeLoans >= policy.maxConcurrentLoans()) {
        throw new LoanLimitExceededException("User exceeded active loan limit");
    }

    // Reducir stock
    bookService.decrementAvailableCopies(bookId);

    // Crear el préstamo
    Loan loan = new Loan();
    loan.setBook(book);
    loan.setUser(user);
    loan.setLoanDate(LocalDate.now());
    loan.setReturnDate(LocalDate.now().plusDays(policy.loanDays()));
    loan.setReturned(false);

    return loanRepository.save(loan);
}
```

### 8.3 Devolver un Préstamo

```java
@Transactional
public Loan returnLoan(Long loanId, Long requesterUserId, Role requesterRole) {
    Loan loan = getLoanById(loanId);

    // Regla 1: Solo el dueño o un LIBRARIAN puede devolver
    boolean isOwner = loan.getUser().getId().equals(requesterUserId);
    boolean isLibrarian = requesterRole == Role.LIBRARIAN;
    if (!isOwner && !isLibrarian) {
        throw new UnauthorizedOperationException("You cannot return loans from other users");
    }

    // Regla 2: No se puede devolver un préstamo ya devuelto
    if (loan.isReturned()) {
        throw new IllegalStateException("Loan is already returned");
    }

    loan.setReturned(true);
    loan.setReturnDate(LocalDate.now());
    bookService.incrementAvailableCopies(loan.getBook().getId());  // ← Recuperar stock

    return loanRepository.save(loan);
}
```

### 8.4 Política de Préstamos por Rol (Strategy Pattern)

Diferentes roles tienen diferentes límites:

```java
// Interfaz de la estrategia
public interface LoanPolicyStrategy {
    int maxConcurrentLoans();   // Cuántos libros puede tener al mismo tiempo
    int loanDays();             // Por cuántos días
}

// Estrategia para USER normal
@Component
public class StandardLoanPolicyStrategy implements LoanPolicyStrategy {
    @Override
    public int maxConcurrentLoans() { return 2; }
    @Override
    public int loanDays() { return 7; }
}

// Estrategia para LIBRARIAN o VIP
@Component
public class PremiumLoanPolicyStrategy implements LoanPolicyStrategy {
    @Override
    public int maxConcurrentLoans() { return 5; }
    @Override
    public int loanDays() { return 14; }
}

// Contexto que decide qué estrategia usar
@Component
public class LoanPolicyContext {
    private final StandardLoanPolicyStrategy standard;
    private final PremiumLoanPolicyStrategy premium;

    public LoanPolicyStrategy getPolicy(Role role) {
        return role == Role.LIBRARIAN ? premium : standard;
    }
}
```

---

## 9. Cómo correr el proyecto

### 9.1 Pre-requisitos

- Java 17+
- Maven
- PostgreSQL corriendo localmente (o Docker)

### 9.2 Opción A: Con Docker Compose

```bash
# Arrancar base de datos PostgreSQL
docker-compose up -d

# Correr la aplicación
./mvnw spring-boot:run
```

### 9.3 Opción B: Variables de entorno manuales

```bash
export DB_URL=jdbc:postgresql://localhost:5432/librarydb
export DB_USERNAME=postgres
export DB_PASSWORD=postgres
export JWT_SECRET=bXlTdXBlclNlY3JldEtleUZvckpXVEF1dGhlbnRpY2F0aW9u

./mvnw spring-boot:run
```

### 9.4 Swagger UI (documentación interactiva)

Una vez corriendo, abre en el navegador:

```
http://localhost:8080/swagger-ui.html
```

Aquí puedes probar todos los endpoints directamente.

---

## 10. Flujo completo de una petición

**Escenario:** Un USER solicita un préstamo

```
1. Usuario envía:
   POST /api/loans
   Authorization: Bearer <jwt_token>
   Body: {"bookId": 5}

2. JwtAuthenticationFilter:
   → Extrae el token del header
   → Valida firma y expiración
   → Carga el usuario en SecurityContext

3. SecurityConfig verifica:
   → ¿Está autenticado? Sí ✅
   → ¿Tiene rol USER o LIBRARIAN? Sí ✅
   → Permite pasar al controller

4. LoanController.createLoan():
   → @PreAuthorize("hasAnyRole('USER','LIBRARIAN')") ✅
   → @AuthenticationPrincipal User user → obtiene el usuario logueado
   → @Valid @RequestBody LoanRequestDTO → valida que bookId no sea null
   → Llama a loanService.createLoan(user.getId(), request.getBookId())

5. LoanService.createLoan():
   → Busca el usuario por ID
   → Busca el libro por ID → ¿existe? ¿tiene stock? ¿no superó límite?
   → Llama a bookService.decrementAvailableCopies(bookId)
   → Crea el Loan y lo guarda

6. LoanRepositoryImpl.save():
   → Convierte Loan → LoanEntity (mapper)
   → JpaLoanDao.save(entity) → INSERT en PostgreSQL
   → Convierte LoanEntity → Loan (mapper)
   → Retorna Loan guardado

7. LoanController retorna:
   → loanMapper.toResponse(loan) → LoanResponseDTO
   → HTTP 201 Created + JSON con los datos del préstamo
```

---

## 11. Cheatsheet de Anotaciones

### JPA

| Anotación | Dónde va | Para qué |
|-----------|----------|----------|
| `@Entity` | Clase | Marca la clase como tabla en BD |
| `@Table(name="x")` | Clase | Nombre de la tabla |
| `@Id` | Campo | Llave primaria |
| `@GeneratedValue` | Campo | Auto-incremental |
| `@Column(nullable=false)` | Campo | Restricción NOT NULL |
| `@Column(unique=true)` | Campo | Restricción UNIQUE |
| `@ManyToOne` | Campo | Relación muchos-a-uno |
| `@JoinColumn(name="x")` | Campo | Nombre de la columna FK |
| `@Enumerated(EnumType.STRING)` | Campo | Guarda el enum como texto |
| `@Transactional` | Método | Todo o nada (rollback si falla) |

### Spring Security

| Anotación | Dónde va | Para qué |
|-----------|----------|----------|
| `@EnableMethodSecurity` | Clase Config | Habilita @PreAuthorize |
| `@PreAuthorize("hasRole('X')")` | Método controller | Requiere ese rol |
| `@PreAuthorize("hasAnyRole('X','Y')")` | Método controller | Requiere uno de esos roles |
| `@AuthenticationPrincipal` | Parámetro | Inyecta el usuario autenticado |
| `@Configuration` | Clase | Clase de configuración Spring |

### Validaciones

| Anotación | Para qué |
|-----------|----------|
| `@NotBlank` | String no vacío ni null |
| `@NotNull` | No puede ser null |
| `@Min(value=N)` | Número >= N |
| `@Max(value=N)` | Número <= N |
| `@Email` | Formato de email válido |
| `@Size(min=N, max=M)` | Longitud de String |
| `@Valid` | Activa las validaciones en @RequestBody |

### Mappers MapStruct

| Anotación | Para qué |
|-----------|----------|
| `@Mapper(componentModel="spring")` | Crea un Bean de Spring |
| `@Mapping(source="x", target="y")` | Mapear campos con diferente nombre |
| `@Mapping(target="x", ignore=true)` | Ignorar un campo en el mapeo |

---

> 📌 **Tip para el parcial:** Si te preguntan "¿por qué el servicio no depende de JPA directamente?",
> la respuesta es **separación de responsabilidades y facilidad para cambiar la implementación de
> persistencia sin tocar la lógica de negocio**. Eso es arquitectura limpia.

> 📌 **Tip para el parcial:** Si te preguntan cómo funciona la seguridad, el flujo es:
> **Request → JwtFilter → SecurityContext → @PreAuthorize → Controller**

---

*Proyecto: DOSW Library API — Escuela Colombiana de Ingeniería Julio Garavito*