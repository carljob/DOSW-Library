# DOSW-Library

API REST para gestionar usuarios, libros y prestamos con almacenamiento en memoria.

## Patrones usados

- `Strategy` para politica de prestamos por tipo de usuario:
  - `STANDARD`: maximo 2 prestamos, 7 dias.
  - `PREMIUM`: maximo 5 prestamos, 14 dias.

## Endpoints

- `GET/POST /api/users`
- `GET/PUT/DELETE /api/users/{id}`
- `GET/POST /api/books`
- `GET/PUT/DELETE /api/books/{id}`
- `GET/POST /api/loans`
- `GET/PUT/DELETE /api/loans/{id}`
- `PUT /api/loans/{id}/return`

## Ejecutar

```powershell
.\mvnw.cmd spring-boot:run
```

## Probar

```powershell
.\mvnw.cmd test
```
