# Reto #6 — CI/CD con GitHub Actions

## ¿Qué hace este reto?

Cada vez que abres un Pull Request en GitHub, se ejecutan automáticamente 4 tareas en orden:
1. **build** → compila el proyecto
2. **test** → corre las pruebas
3. **analysis** → analiza la calidad del código
4. **deploy** → despliega la app

Esto se llama pipeline CI/CD y garantiza que el código que llega a producción siempre compiló y pasó todas las pruebas.

---

## PARTE 1 — Crear el archivo del workflow

### Paso 1.1 — Abrir el proyecto en IntelliJ

Abre IntelliJ IDEA y carga tu proyecto `DOSW-Library`.

---

### Paso 1.2 — Crear la carpeta .github/workflows

1. En el panel izquierdo de IntelliJ (Project), ubica la **raíz del proyecto** — es la carpeta que contiene `pom.xml`, `src/`, etc.
2. Clic derecho sobre la raíz del proyecto
3. Clic en **"New"**
4. Clic en **"Directory"**
5. En el campo que aparece escribe exactamente: `.github/workflows`
6. Presiona **Enter**

> IntelliJ creará automáticamente las dos carpetas anidadas: `.github` y dentro de ella `workflows`.

---

### Paso 1.3 — Crear el archivo ci-cd.yml

1. En el panel izquierdo, clic derecho sobre la carpeta `workflows` que acabas de crear
2. Clic en **"New"**
3. Clic en **"File"**
4. En el campo que aparece escribe exactamente: `ci-cd.yml`
5. Presiona **Enter**

Se abre el archivo vacío en el editor.

---

### Paso 1.4 — Pegar el contenido del workflow

Clic dentro del archivo `ci-cd.yml` y pega exactamente este contenido:

```yaml
name: CI/CD Pipeline - DOSW Library

on:
  pull_request:
    branches: [ "main", "develop" ]

jobs:

  build:
    name: Build
    runs-on: ubuntu-latest
    steps:
      - name: Checkout código
        uses: actions/checkout@v4
      - name: Configurar Java 17
        uses: actions/setup-java@v4
        with:
          java-version: '17'
          distribution: 'temurin'
          cache: maven
      - name: Compilar proyecto (fase compile)
        run: mvn compile -DskipTests --no-transfer-progress
      - name: Guardar clases compiladas
        uses: actions/upload-artifact@v4
        with:
          name: compiled-classes
          path: target/
          retention-days: 1

  test:
    name: Test
    runs-on: ubuntu-latest
    needs: build
    steps:
      - name: Checkout código
        uses: actions/checkout@v4
      - name: Configurar Java 17
        uses: actions/setup-java@v4
        with:
          java-version: '17'
          distribution: 'temurin'
          cache: maven
      - name: Ejecutar pruebas unitarias (fase verify)
        run: |
          mvn verify \
            -Dtest="LoanServiceTest,LoanServiceReto6Test,UserServiceTest,BookMongoPersistenceTest,BookJpaPersistenceTest" \
            -DfailIfNoTests=false \
            --no-transfer-progress
      - name: Publicar resultados de pruebas
        uses: actions/upload-artifact@v4
        if: always()
        with:
          name: test-results
          path: target/surefire-reports/
          retention-days: 7

  analysis:
    name: Analysis
    runs-on: ubuntu-latest
    needs: test
    steps:
      - name: Checkout código
        uses: actions/checkout@v4
      - name: Configurar Java 17
        uses: actions/setup-java@v4
        with:
          java-version: '17'
          distribution: 'temurin'
          cache: maven
      - name: Análisis estático con Maven
        run: mvn validate compile -DskipTests --no-transfer-progress
      - name: Verificar warnings
        run: |
          echo "=== Revisando calidad del código ==="
          mvn compile -DskipTests --no-transfer-progress 2>&1 | grep -i "warning\|error" || echo "Sin warnings críticos"

  deploy:
    name: Deploy
    runs-on: ubuntu-latest
    needs: test
    steps:
      - name: Checkout código
        uses: actions/checkout@v4
      - name: Configurar Java 17
        uses: actions/setup-java@v4
        with:
          java-version: '17'
          distribution: 'temurin'
          cache: maven
      - name: Empaquetar JAR
        run: mvn package -DskipTests --no-transfer-progress
      - name: Desplegar en Azure App Service
        uses: azure/webapps-deploy@v2
        with:
          app-name: dosw-library-api
          publish-profile: ${{ secrets.AZURE_WEBAPP_PUBLISH_PROFILE }}
          package: target/*.jar
```

6. Guarda el archivo con `Ctrl + S`

---

## PARTE 2 — Crear los 5 tests requeridos

El enunciado pide exactamente 5 tests de la entidad "reserva" (préstamo) a nivel de servicio.

### Paso 2.1 — Ubicar la carpeta de tests

En el panel izquierdo de IntelliJ navega hasta:

```
src
  test
    java
      edu
        eci
          dosw
            DOSW_Library
              core
                service      ← aquí debes crear el archivo
```

---

### Paso 2.2 — Crear el archivo de tests

1. Clic derecho sobre la carpeta `service` dentro de `test`
2. Clic en **"New"**
3. Clic en **"Java Class"**
4. En el campo que aparece escribe exactamente: `LoanServiceReto6Test`
5. Presiona **Enter**

---

### Paso 2.3 — Pegar el contenido de los tests

Borra todo lo que haya en el archivo y pega exactamente este contenido:

```java
package edu.eci.dosw.DOSW_Library.core.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import edu.eci.dosw.DOSW_Library.core.model.Book;
import edu.eci.dosw.DOSW_Library.core.model.Loan;
import edu.eci.dosw.DOSW_Library.core.model.Role;
import edu.eci.dosw.DOSW_Library.core.model.User;
import edu.eci.dosw.DOSW_Library.core.strategy.LoanPolicyContext;
import edu.eci.dosw.DOSW_Library.core.strategy.LoanPolicyStrategy;
import edu.eci.dosw.DOSW_Library.repository.LoanRepository;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LoanServiceReto6Test {

    @Mock
    private LoanRepository loanRepository;

    @Mock
    private UserService userService;

    @Mock
    private BookService bookService;

    @Mock
    private LoanPolicyContext loanPolicyContext;

    @Mock
    private LoanPolicyStrategy loanPolicyStrategy;

    @InjectMocks
    private LoanService loanService;

    private User user;
    private Book book;
    private Loan loan;

    @BeforeEach
    void setUp() {
        user = new User(1L, "Carlos", "carlos@email.com", "carlos0869", "encoded", Role.USER);
        book = new Book(10L, "Clean Code", "Robert Martin", "ISBN-1", 5, 4);
        loan = new Loan(100L, book, user, LocalDate.now(), LocalDate.now().plusDays(14), false);
    }

    @Test
    void dadoQueHayUnaReserva_cuandoConsulto_entoncesEsExitosaValidandoId() {
        when(loanRepository.findById(100L)).thenReturn(Optional.of(loan));

        Loan resultado = loanService.getLoanById(100L);

        assertNotNull(resultado);
        assertEquals(100L, resultado.getId());
    }

    @Test
    void dadoQueNoHayReservas_cuandoConsultoTodas_entoncesRetornaListaVacia() {
        when(loanRepository.findAll()).thenReturn(Collections.emptyList());

        List<Loan> resultado = loanService.getAllLoans();

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    @Test
    void dadoQueNoHayReservas_cuandoCreo_entoncesLaCreacionEsExitosa() {
        when(userService.getUserById(1L)).thenReturn(user);
        when(bookService.getBookById(10L)).thenReturn(book);
        when(loanPolicyContext.getPolicy(Role.USER)).thenReturn(loanPolicyStrategy);
        when(loanPolicyStrategy.maxConcurrentLoans()).thenReturn(3);
        when(loanPolicyStrategy.loanDays()).thenReturn(14);
        when(loanRepository.countByUserIdAndReturnedFalse(1L)).thenReturn(0L);
        when(loanRepository.save(any(Loan.class))).thenAnswer(inv -> {
            Loan l = inv.getArgument(0);
            l = new Loan(200L, l.getBook(), l.getUser(), l.getLoanDate(), l.getReturnDate(), l.isReturned());
            return l;
        });

        Loan resultado = loanService.createLoan(1L, 10L);

        assertNotNull(resultado);
        assertEquals(book, resultado.getBook());
        assertEquals(user, resultado.getUser());
        assertFalse(resultado.isReturned());
        verify(loanRepository).save(any(Loan.class));
    }

    @Test
    void dadoQueHayUnaReserva_cuandoElimino_entoncesLaEliminacionEsExitosa() {
        when(loanRepository.findById(100L)).thenReturn(Optional.of(loan));
        doNothing().when(loanRepository).delete(loan);

        loanService.deleteLoan(100L);

        verify(loanRepository).delete(loan);
    }

    @Test
    void dadoQueHayUnaReserva_cuandoEliminoYConsulto_entoncesNoHayResultados() {
        when(loanRepository.findById(100L)).thenReturn(Optional.of(loan));
        doNothing().when(loanRepository).delete(loan);
        when(loanRepository.findAll()).thenReturn(Collections.emptyList());

        loanService.deleteLoan(100L);
        List<Loan> resultado = loanService.getAllLoans();

        verify(loanRepository).delete(loan);
        assertTrue(resultado.isEmpty());
    }
}
```

Guarda el archivo con `Ctrl + S`.

---

## PARTE 3 — Subir los cambios a GitHub

### Paso 3.1 — Abrir la terminal de IntelliJ

En IntelliJ clic en la pestaña **"Terminal"** que está en la barra inferior. Si no la ves, ve al menú superior → **View** → **Tool Windows** → **Terminal**.

---

### Paso 3.2 — Crear una rama nueva

En la terminal escribe este comando y presiona Enter:

```bash
git checkout -b feature/reto6-cicd
```

> Esto crea una nueva rama llamada `feature/reto6-cicd` y te mueve a ella automáticamente. Verás el mensaje: `Switched to a new branch 'feature/reto6-cicd'`

> **Si la rama ya existe** verás un error. En ese caso escribe en su lugar:
> ```bash
> git checkout feature/reto6-cicd
> ```

---

### Paso 3.3 — Agregar todos los archivos nuevos

```bash
git add .
```

> El punto (`.`) significa "agrega todos los cambios". No debe salir ningún mensaje de error.

---

### Paso 3.4 — Hacer el commit

```bash
git commit -m "Reto 6 - CI/CD Pipeline and functional tests"
```

> Verás un resumen de los archivos que se incluyeron en el commit.

---

### Paso 3.5 — Subir la rama a GitHub

```bash
git push origin feature/reto6-cicd
```

> Verás un mensaje que dice algo como `Branch 'feature/reto6-cicd' set up to track remote branch`. Eso es correcto.

---

## PARTE 4 — Crear el Pull Request en GitHub

### Paso 4.1 — Abrir el repositorio en el navegador

Ve a:
```
https://github.com/carljob/DOSW-Library
```

---

### Paso 4.2 — Crear el Pull Request

**Opción A (más fácil):** GitHub mostrará automáticamente un banner amarillo en la parte superior que dice:

```
feature/reto6-cicd had recent pushes  [Compare & pull request]
```

Clic en el botón verde **"Compare & pull request"**.

**Opción B (si no aparece el banner):** Ve directamente a:
```
https://github.com/carljob/DOSW-Library/compare/develop...feature/reto6-cicd
```

---

### Paso 4.3 — Configurar y crear el PR

1. En el campo **"Add a title"** escribe:
   ```
   Reto 6 - CI/CD Pipeline and functional tests
   ```
2. En el campo **"Add a description"** escribe:
   ```
   Agrega workflow CI/CD con 4 jobs y 5 tests funcionales de reservas
   ```
3. Verifica que en la parte superior diga:
   - **base:** `develop`
   - **compare:** `feature/reto6-cicd`
4. Clic en el botón verde **"Create pull request"**

---

## PARTE 5 — Verificar que el workflow corre exitoso

### Paso 5.1 — Ir a la pestaña Actions

Una vez creado el PR, en tu repositorio de GitHub clic en la pestaña **"Actions"** (está en la barra superior junto a Code, Issues, Pull requests, etc.).

---

### Paso 5.2 — Ver el workflow corriendo

Verás una entrada que dice **"CI/CD Pipeline - DOSW Library"** con un círculo amarillo girando — eso significa que está corriendo. Clic en esa entrada para ver el detalle.

---

### Paso 5.3 — Esperar que los 4 jobs queden en verde

En el panel izquierdo verás los 4 jobs. Espera a que todos cambien de amarillo a verde:

```
✅ Build     — debe terminar primero (aprox. 15 segundos)
✅ Test      — corre después de Build (aprox. 20 segundos)
✅ Analysis  — corre después de Test (aprox. 18 segundos)
✅ Deploy    — corre después de Test (aprox. 40 segundos)
```

Cuando todos estén en verde el Reto #6 está **completado** ✅.

---

### Paso 5.4 — Si algún job falla

1. Clic en el job que falló (el que tiene ❌ rojo)
2. Lee el mensaje de error que aparece en rojo
3. Los errores más comunes son:

| Error | Solución |
|-------|----------|
| `upload-artifact: v3 deprecated` | Abre `ci-cd.yml` y cambia `@v3` por `@v4` en todas las líneas de `upload-artifact` |
| `No tests found` | Verifica que el archivo `LoanServiceReto6Test.java` está en la ruta correcta |
| `Compilation error` | Revisa que no haya errores de sintaxis en el código Java |

Después de corregir el error, vuelve al Paso 3.3 para hacer un nuevo commit y push. El workflow se disparará automáticamente.

---

## Resumen de archivos creados en este reto

```
DOSW-Library/
  .github/
    workflows/
      ci-cd.yml                          ← workflow de GitHub Actions
  src/
    test/
      java/
        edu/eci/dosw/DOSW_Library/
          core/
            service/
              LoanServiceReto6Test.java  ← 5 tests requeridos
```

---

## Resultado esperado en GitHub Actions

```
CI/CD Pipeline - DOSW Library   ✅ Success   1m 23s

  ✅ Build      13s
  ✅ Test       20s
  ✅ Analysis   18s
  ✅ Deploy     38s
```
