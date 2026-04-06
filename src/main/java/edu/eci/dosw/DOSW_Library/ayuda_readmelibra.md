# Reto #1 — Meta-modelo conceptual NoSQL

## Decisiones embed vs referencia

### Book (colección `books`)
Documento principal. Contiene campos extendidos **embebidos**:
- `metadata` (páginas, idioma, empresa) → **embebido** porque es inseparable del libro.
- `availability` (status, totalCopias, copiasDisponibles, copiasPrestadas) → **embebido** porque cambia junto con el libro.
- `categorias` → **array primitivo** (strings simples, no entidad propia).

### User (colección `users`)
Documento principal con campos extendidos directos:
- `tipoMembresia` (VIP, Platinum, Standard) → campo simple.
- `fechaAgregado` → campo simple.
- **NO referencia** Book ni Loan directamente.

### Loan (colección `loans`)
Referencia a Book y User por ID (no embed), porque:
- Un mismo libro puede tener múltiples préstamos activos.
- Un usuario puede tener múltiples préstamos.
- Embed causaría duplicación masiva de datos.

Contiene **historial embebido**:
- `historial: [ { status, fechaEjecucion } ]` → **array embebido** porque el historial es propio del préstamo, no se consulta de forma independiente.

## Diagrama de colecciones

```
┌─────────────────────────────────────────────────────┐
│  books                                              │
│  ─────────────────────────────────────────────────  │
│  _id: ObjectId                                      │
│  titulo: String                                     │
│  autor: String                                      │
│  isbn: String (unique)                              │
│  categorias: [String]                               │
│  tipoPublicacion: String                            │
│  fechaPublicacion: Date                             │
│  fechaAgregado: Date                                │
│  totalStock: Int                                    │
│  availableStock: Int                                │
│  metadata: {                      ← EMBEBIDO        │
│    paginas: Int                                     │
│    idioma: String                                   │
│    empresa: String                                  │
│  }                                                  │
│  availability: {                  ← EMBEBIDO        │
│    status: String                                   │
│    totalCopias: Int                                 │
│    copiasDisponibles: Int                           │
│    copiasPrestadas: Int                             │
│  }                                                  │
└─────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────┐
│  users                                              │
│  ─────────────────────────────────────────────────  │
│  _id: ObjectId                                      │
│  nombre: String                                     │
│  email: String (unique)                             │
│  username: String (unique)                          │
│  password: String (hashed)                          │
│  rol: String (USER | LIBRARIAN)                     │
│  tipoMembresia: String (VIP|Platinum|Standard)      │
│  fechaAgregado: Date                                │
└─────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────┐
│  loans                                              │
│  ─────────────────────────────────────────────────  │
│  _id: ObjectId                                      │
│  libroId: String        ──────────► books._id       │
│  usuarioId: String      ──────────► users._id       │
│  fechaPrestamo: Date                                │
│  fechaDevolucion: Date                              │
│  devuelto: Boolean                                  │
│  fechaAgregado: Date                                │
│  historial: [           ← EMBEBIDO (array)          │
│    {                                                │
│      status: String                                 │
│      fechaEjecucion: Date                           │
│    }                                                │
│  ]                                                  │
└─────────────────────────────────────────────────────┘
```

## Relaciones entre documentos
- `loans.libroId` → referencia por ID a `books._id` (**referenced**)
- `loans.usuarioId` → referencia por ID a `users._id` (**referenced**)
- `loans.historial` → array de objetos embebidos (**embedded**)
- `books.metadata` → objeto embebido (**embedded**)
- `books.availability` → objeto embebido (**embedded**)
