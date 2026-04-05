# 📐 Reto #1 — Metamodelo NoSQL (MongoDB)
### Guía completa: cómo diseñar y leer un diagrama de modelo no relacional

---

## 📋 Tabla de Contenido

1. [¿Qué es un metamodelo NoSQL?](#1-qué-es-un-metamodelo-nosql)
2. [Diferencia entre SQL y NoSQL](#2-diferencia-entre-sql-y-nosql)
3. [Cómo leer la notación del diagrama](#3-cómo-leer-la-notación-del-diagrama)
4. [Embebido vs Referenciado](#4-embebido-vs-referenciado)
5. [El diagrama del sistema de Biblioteca](#5-el-diagrama-del-sistema-de-biblioteca)
6. [Explicación de cada colección](#6-explicación-de-cada-colección)
7. [Cómo se ven los datos en MongoDB](#7-cómo-se-ven-los-datos-en-mongodb)
8. [Cómo hacer el diagrama en Lucidchart](#8-cómo-hacer-el-diagrama-en-lucidchart)

---

## 1. ¿Qué es un metamodelo NoSQL?

Un **metamodelo NoSQL** es el diseño conceptual que define:

- Qué **colecciones** (equivalente a tablas) va a tener la base de datos
- Qué **campos** tiene cada documento dentro de esas colecciones
- Cómo se **relacionan** los documentos entre sí
- Si los datos relacionados van **dentro del mismo documento** (embebido) o en **documentos separados** (referenciado)

> 💡 Es como el diagrama ER de una base de datos relacional, pero adaptado a la forma en que MongoDB almacena datos.

---

## 2. Diferencia entre SQL y NoSQL

| Concepto | PostgreSQL (SQL) | MongoDB (NoSQL) |
|----------|-----------------|-----------------|
| Estructura | Tablas con columnas fijas | Colecciones con documentos JSON flexibles |
| Fila | Registro con columnas | Documento JSON |
| Relación | JOIN entre tablas (FK) | Referencia por ObjectId o datos embebidos |
| ID | Integer autoincremental | ObjectId (cadena hexadecimal de 24 caracteres) |
| Esquema | Rígido (columnas definidas) | Flexible (cada documento puede tener campos distintos) |

**Ejemplo visual:**

```
PostgreSQL:                          MongoDB:
┌──────────────────┐                 {
│ books            │                   "_id": ObjectId("664a1b..."),
│──────────────────│                   "title": "Clean Code",
│ id  | title | .. │                   "author": "Robert C. Martin",
│  1  | Clean │ .. │                   "categories": ["Programación"],
│  2  | DDD   │ .. │                   "metadata": {
└──────────────────┘                     "pages": 431,
                                         "language": "Inglés"
                                       }
                                     }
```

---

## 3. Cómo leer la notación del diagrama

El diagrama usa **notación Crow's Foot** (pata de cuervo). Cada símbolo en los extremos de las líneas indica cuántos elementos pueden estar relacionados.

### 3.1 Los símbolos de cardinalidad

```
Símbolo          Significado
────||────       Exactamente uno (obligatorio)
────|O────       Cero o uno (opcional)
────||<──        Uno o muchos (obligatorio)
────O<───        Cero o muchos (opcional)
```

### 3.2 Cómo leer una línea completa

La línea se lee desde **ambos extremos hacia el centro**:

```
USUARIO  ──||────O<──  PRESTAMO

Se lee así:
→ Un USUARIO puede tener CERO O MUCHOS préstamos
→ Un PRESTAMO pertenece a EXACTAMENTE UN usuario
```

```
PRESTAMO  ──||────O<──  HISTORIALPRESTAMO  (embebido)

Se lee así:
→ Un PRESTAMO puede tener CERO O MUCHOS entradas de historial
→ Cada entrada de historial pertenece a EXACTAMENTE UN préstamo
```

### 3.3 Tipos de línea

| Tipo de línea | Significado | Cuándo usarla |
|--------------|-------------|---------------|
| **Línea sólida** `────` | Referencia por ObjectId | Los documentos viven en colecciones separadas. Solo se guarda el ID del otro documento |
| **Línea punteada** `- - -` | Documento embebido | El subdocumento vive DENTRO del documento padre. No tiene colección propia |

### 3.4 Ejemplos del diagrama

```
PRESTAMO ──────────── USUARIO        ← Línea sólida
                                       PRESTAMO solo guarda el usuarioId (ObjectId)
                                       USUARIO vive en su propia colección

LIBRO - - - - - - - - METADATA       ← Línea punteada
                                       METADATA vive DENTRO del documento LIBRO
                                       No existe una colección separada de metadata

PRESTAMO - - - - - - - HISTORIALPRESTAMO  ← Línea punteada + "historial[] embedded"
                                             El [] indica que es una LISTA de entradas
                                             Todas las entradas viven dentro del préstamo
```

---

## 4. Embebido vs Referenciado

Esta es **la decisión más importante** al diseñar un modelo NoSQL.

### ¿Cuándo embeber?

Embebes un documento cuando:
- Los datos **siempre se consultan juntos** (no tiene sentido pedir metadata sin el libro)
- Los datos **no se reutilizan** en otros documentos
- La relación es **de pertenencia total** (el historial solo existe dentro de ese préstamo)

```
✅ EMBEBIDOS en este proyecto:
   LIBRO → METADATA         (las páginas/idioma siempre van con el libro)
   LIBRO → DISPONIBILIDAD   (el stock siempre se consulta con el libro)
   LIBRO → categories[]     (las categorías son parte del libro)
   PRESTAMO → HISTORIAL[]   (el historial pertenece solo a ese préstamo)
```

### ¿Cuándo referenciar?

Referencias cuando:
- Los datos **se consultan por separado** con frecuencia
- El mismo documento **es usado por muchos otros** (un usuario tiene muchos préstamos)
- Embeber causaría **duplicación masiva** de datos

```
✅ REFERENCIADOS en este proyecto:
   PRESTAMO → usuarioId    (el usuario existe independientemente del préstamo)
   PRESTAMO → libroId      (el libro existe independientemente del préstamo)
```

### Comparación visual

```
EMBEBIDO — Todo en un solo documento:
{
  "_id": "abc123",
  "title": "Clean Code",
  "metadata": {           ← metadata DENTRO del libro
    "pages": 431,
    "language": "Inglés"
  }
}

REFERENCIADO — Documentos separados:
// Documento en colección "loans"
{
  "_id": "loan001",
  "libroId": "abc123",    ← solo el ID, no el libro completo
  "usuarioId": "user42"   ← solo el ID, no el usuario completo
}

// Documento en colección "books"
{
  "_id": "abc123",        ← el libro vive aparte
  "title": "Clean Code"
}
```

---

## 5. El diagrama del sistema de Biblioteca

![Diagrama NoSQL - Library System](./diagrama-nosql.png)

> El diagrama muestra las 3 colecciones principales del sistema y sus relaciones.

---

## 6. Explicación de cada colección

### 📖 Colección: LIBRO

| Campo | Tipo | Descripción |
|-------|------|-------------|
| `id` | ObjectId PK | Identificador único generado por MongoDB |
| `titulo` | String | Título del libro |
| `autor` | String | Autor del libro |
| `isbn` | String | Código ISBN único |
| `categorias` | String[] | Lista de categorías (ej: ["Programación", "Ciencias"]) |
| `tipoPublicacion` | String | Tipo: REVISTA, EBOOK, CARTILLA |
| `fechaPublicacion` | Date | Fecha en que fue publicado |
| `fechaAgregado` | Date | Fecha en que fue agregado al catálogo |
| `metadata` | **Embebido** | Objeto con paginas, idioma, empresa |
| `disponibilidad` | **Embebido** | Objeto con status, totalCopias, copiasDisponibles, copiasPrestadas |

**Subdocumento METADATA (embebido en LIBRO):**

| Campo | Tipo | Descripción |
|-------|------|-------------|
| `paginas` | Int | Número de páginas |
| `idioma` | String | Idioma del libro |
| `empresa` | String | Editorial / empresa que lo publicó |

**Subdocumento DISPONIBILIDAD (embebido en LIBRO):**

| Campo | Tipo | Descripción |
|-------|------|-------------|
| `status` | String | Estado: AVAILABLE, UNAVAILABLE |
| `totalCopias` | Int | Total de ejemplares existentes |
| `copiasDisponibles` | Int | Ejemplares disponibles para préstamo |
| `copiasPrestadas` | Int | Ejemplares actualmente prestados |

---

### 👤 Colección: USUARIO

| Campo | Tipo | Descripción |
|-------|------|-------------|
| `id` | ObjectId PK | Identificador único |
| `nombre` | String | Nombre completo |
| `email` | String | Correo electrónico único |
| `username` | String | Nombre de usuario único |
| `password` | String | Contraseña hasheada (BCrypt) |
| `rol` | String | Rol: USER, LIBRARIAN |
| `tipoMembresia` | String | Tipo: VIP, PLATINUM, STANDARD |
| `fechaAgregado` | Date | Fecha de registro en la biblioteca |

---

### 📋 Colección: PRESTAMO

| Campo | Tipo | Descripción |
|-------|------|-------------|
| `id` | ObjectId PK | Identificador único |
| `usuarioId` | ObjectId FK | **Referencia** al documento en colección usuarios |
| `libroId` | ObjectId FK | **Referencia** al documento en colección libros |
| `fechaPrestamo` | Date | Fecha en que se realizó el préstamo |
| `fechaDevolucion` | Date | Fecha límite o real de devolución |
| `devuelto` | Boolean | true si ya fue devuelto |
| `fechaAgregado` | Date | Fecha de creación del registro |
| `historial` | **Embebido[]** | Lista de entradas de historial del préstamo |

**Subdocumento HISTORIALPRESTAMO (embebido en PRESTAMO como lista):**

| Campo | Tipo | Descripción |
|-------|------|-------------|
| `status` | String | Estado en ese momento: CREATED, EXTENDED, RETURNED |
| `fechaEjecucion` | Date | Fecha en que ocurrió ese cambio de estado |

---

## 7. Cómo se ven los datos en MongoDB

Así se vería un documento real en cada colección:

### Documento en colección `books`:
```json
{
  "_id": "664a1b2c3d4e5f6a7b8c9d0e",
  "titulo": "Clean Code",
  "autor": "Robert C. Martin",
  "isbn": "978-0132350884",
  "categorias": ["Programación", "Ingeniería de Software"],
  "tipoPublicacion": "EBOOK",
  "fechaPublicacion": "2008-08-01",
  "fechaAgregado": "2024-01-15",
  "metadata": {
    "paginas": 431,
    "idioma": "Inglés",
    "empresa": "Prentice Hall"
  },
  "disponibilidad": {
    "status": "AVAILABLE",
    "totalCopias": 5,
    "copiasDisponibles": 3,
    "copiasPrestadas": 2
  }
}
```

### Documento en colección `users`:
```json
{
  "_id": "664a1b2c3d4e5f6a7b8c9d1f",
  "nombre": "Carlos García",
  "email": "carlos@biblioteca.com",
  "username": "carlos",
  "password": "$2a$10$hashedPassword...",
  "rol": "USER",
  "tipoMembresia": "STANDARD",
  "fechaAgregado": "2024-03-10"
}
```

### Documento en colección `loans`:
```json
{
  "_id": "664a1b2c3d4e5f6a7b8c9d2g",
  "usuarioId": "664a1b2c3d4e5f6a7b8c9d1f",
  "libroId": "664a1b2c3d4e5f6a7b8c9d0e",
  "fechaPrestamo": "2024-04-01",
  "fechaDevolucion": "2024-04-08",
  "devuelto": false,
  "fechaAgregado": "2024-04-01",
  "historial": [
    {
      "status": "CREATED",
      "fechaEjecucion": "2024-04-01"
    },
    {
      "status": "EXTENDED",
      "fechaEjecucion": "2024-04-07"
    }
  ]
}
```

---

## 8. Cómo hacer el diagrama en Lucidchart

### Paso 1 — Crear el documento

1. Ir a [lucidchart.com](https://lucidchart.com)
2. Crear un nuevo documento
3. En el panel izquierdo buscar **"Entity Relationship"** en las formas disponibles

### Paso 2 — Crear una entidad (colección)

1. Arrastrar una forma de tipo **Entity** al canvas
2. Hacer doble clic para editar el nombre (ej: `LIBRO`)
3. Agregar filas: cada fila es un campo con nombre y tipo
4. Para marcar PK: escribir `PK` después del nombre del campo
5. Para marcar FK: escribir `FK` después del nombre del campo

### Paso 3 — Crear relaciones referenciadas (línea sólida)

1. Pasar el mouse sobre el borde de una entidad hasta que aparezca una flecha azul
2. Arrastrar esa flecha hasta la entidad destino
3. La línea sólida representa una **referencia por ObjectId**
4. Hacer doble clic en la línea para agregar la etiqueta (ej: `"usuarioId"`)
5. Cambiar los símbolos de cardinalidad en los extremos según la relación

### Paso 4 — Crear documentos embebidos (línea punteada)

1. Crear la entidad embebida (ej: `METADATA`)
2. Conectarla igual que en el paso 3
3. Seleccionar la línea → en el panel derecho cambiar el estilo a **"Dashed"** (punteado)
4. Agregar la etiqueta `"embedded"` a la línea

### Paso 5 — Cambiar los símbolos de cardinalidad

Al seleccionar una línea, en el panel derecho aparece la opción para cambiar el símbolo de cada extremo. Los más usados:

| Símbolo en Lucidchart | Notación | Significado |
|----------------------|----------|-------------|
| `One and only one` | `──||──` | Exactamente uno |
| `Zero or one` | `──|O──` | Cero o uno |
| `One or many` | `──||<──` | Uno o muchos |
| `Zero or many` | `──O<──` | Cero o muchos |

### Paso 6 — Exportar

1. `File → Export` → seleccionar **PNG** o **PDF**
2. Guardar la imagen en la carpeta del proyecto
3. Referenciarla en el README con:
```markdown
![Diagrama NoSQL](./diagrama-nosql.png)
```

---

## 📌 Resumen para el parcial

> **¿Por qué NoSQL para este sistema?**
> El catálogo de libros está creciendo y los datos no son todos iguales (distintos tipos de publicación, metadata variable). MongoDB permite esquemas flexibles y escala horizontalmente sin fricciones.

> **¿Por qué METADATA y DISPONIBILIDAD son embebidos?**
> Porque siempre se consultan junto con el libro. Nunca se necesita "dame todas las metadatas sin sus libros". Embeber evita hacer un JOIN (lookup) extra.

> **¿Por qué usuarioId y libroId son referencias en PRESTAMO?**
> Porque un usuario puede tener muchos préstamos. Si embebiéramos el usuario completo en cada préstamo, cualquier cambio en el nombre del usuario tendría que actualizarse en todos sus préstamos — inconsistencia garantizada.

> **¿Qué es el ObjectId?**
> Es el identificador único que MongoDB genera automáticamente para cada documento. Se ve así: `664a1b2c3d4e5f6a7b8c9d0e` (24 caracteres hexadecimales). Equivale al `id BIGSERIAL` de PostgreSQL.

---

*Proyecto: DOSW Library API — Escuela Colombiana de Ingeniería Julio Garavito*
