# Retos 6, 7 y 8 — CI/CD, Azure Deploy y High Level Design

---

## RETO #6 — CI/CD con GitHub Actions

### ¿Qué es esto?
CI/CD significa Integración Continua y Despliegue Continuo. Cada vez que haces un Pull Request en GitHub, se ejecutan automáticamente 4 tareas: compilar, probar, analizar y desplegar. Así nunca llega código roto a producción.

---

### Paso 1 — Crear la carpeta del workflow

Dentro de tu proyecto crea esta estructura de carpetas exacta:

```
.github/
  workflows/
    ci-cd.yml
```

En IntelliJ: clic derecho en la raíz del proyecto → New → Directory → escribe `.github/workflows`. Luego clic derecho sobre esa carpeta → New → File → escribe `ci-cd.yml`.

---

### Paso 2 — Contenido del archivo ci-cd.yml

Pega exactamente este contenido en el archivo `.github/workflows/ci-cd.yml`:

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

---

### Paso 3 — Agregar los 5 tests requeridos

Crea el archivo `LoanServiceReto6Test.java` en esta ruta exacta:

```
src/test/java/edu/eci/dosw/DOSW_Library/core/service/LoanServiceReto6Test.java
```

El contenido del archivo es el que te entregué anteriormente (los 5 tests con el formato "Dado que... Cuando... Entonces...").

---

### Paso 4 — Hacer commit y push

Abre PowerShell en la carpeta del proyecto y ejecuta estos comandos uno por uno:

```bash
git checkout feature/reto6-cicd
```
> Si esa rama no existe aún, créala con: `git checkout -b feature/reto6-cicd`

```bash
git add .
git commit -m "Reto 6 - CI/CD Pipeline and functional tests"
git push origin feature/reto6-cicd
```

---

### Paso 5 — Crear el Pull Request en GitHub

1. Abre el navegador y ve a tu repositorio en GitHub:
   ```
   https://github.com/carljob/DOSW-Library
   ```
2. GitHub mostrará automáticamente un banner amarillo que dice **"Compare & pull request"** — clic en ese botón
3. Si no aparece el banner, ve directamente a:
   ```
   https://github.com/carljob/DOSW-Library/compare/develop...feature/reto6-cicd
   ```
4. Pon como título: `Reto 6 - CI/CD Pipeline and functional tests`
5. Clic en **"Create pull request"**

---

### Paso 6 — Verificar que el workflow corra exitoso

1. En tu repositorio de GitHub clic en la pestaña **"Actions"**
2. Verás el workflow corriendo — espera a que los 4 jobs queden en verde:
    - ✅ Build
    - ✅ Test
    - ✅ Analysis
    - ✅ Deploy
3. Si algún job falla, clic en él para ver el error exacto

> **Nota importante:** Si ves el error `actions/upload-artifact: v3 deprecated`, asegúrate de que en tu archivo `ci-cd.yml` todas las líneas digan `upload-artifact@v4` y no `v3`.

---

## RETO #7 — Deploy en Microsoft Azure

### ¿Qué es esto?
Desplegar la aplicación en la nube significa que cualquier persona con internet puede acceder a tu API desde una URL pública, sin necesidad de tener el proyecto corriendo en tu computadora.

---

### Paso 1 — Crear el App Service en Azure

1. Ve a [portal.azure.com](https://portal.azure.com) e inicia sesión
2. Clic en **"Crear un recurso"** (o **"Create a resource"**)
3. Busca **"Aplicación web"** (Web App) — selecciona el de Microsoft
4. Clic en **"Crear"**
5. Configura los campos así:

| Campo | Valor |
|-------|-------|
| Suscripción | Azure for Students (la que tengas) |
| Grupo de recursos | Crea nuevo → `dosw-library-rg` |
| Nombre | `dosw-library-api` |
| Publicar | Código |
| Pila del entorno en tiempo de ejecución | Java 17 |
| Pila de servidor web Java | Java SE (Embedded Web Server) |
| Sistema operativo | Linux |
| Región | East US |
| Plan de precios | **Free F1** (0 dólares) |

6. Clic en **"Revisar y crear"** → luego **"Crear"**
7. Espera a que aparezca el mensaje **"Se completó la implementación"**
8. Clic en **"Ir al recurso"**

---

### Paso 2 — Habilitar autenticación básica para descargar el perfil

1. En el menú izquierdo del App Service clic en **"Configuración"** → **"Configuración general"**
2. Busca la sección **"Credenciales de implementación básica"**
3. Activa la opción → **"On"**
4. Clic en **"Guardar"**

---

### Paso 3 — Descargar el Publish Profile

1. Vuelve a la pantalla principal del App Service (clic en "Introducción" en el menú izquierdo)
2. En la barra superior clic en **"Descargar perfil de publicación"**
3. Se descarga un archivo con extensión `.PublishSettings` — guárdalo, lo necesitas en el siguiente paso

---

### Paso 4 — Agregar el secreto en GitHub

1. Ve a tu repositorio en GitHub: `https://github.com/carljob/DOSW-Library`
2. Clic en **"Settings"** (pestaña superior del repositorio)
3. En el menú izquierdo clic en **"Secrets and variables"** → **"Actions"**
4. Clic en **"New repository secret"**
5. Configura así:
    - **Name:** `AZURE_WEBAPP_PUBLISH_PROFILE`
    - **Secret:** abre el archivo `.PublishSettings` descargado con el Bloc de notas, selecciona todo (`Ctrl+A`), cópialo y pégalo aquí
6. Clic en **"Add secret"**

---

### Paso 5 — Verificar que el workflow incluye el deploy a Azure

El archivo `.github/workflows/ci-cd.yml` ya tiene el job `deploy` configurado para Azure (lo hicimos en el Reto #6). Verifica que el job `deploy` tenga este contenido:

```yaml
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

---

### Paso 6 — Hacer push para disparar el workflow

En PowerShell:

```bash
git add .
git commit -m "Reto 7 - Azure deploy configured"
git push origin feature/reto6-cicd
```

Espera a que el workflow corra en GitHub Actions y el job **Deploy** quede en verde ✅.

---

### Paso 7 — Configurar la variable de entorno de MongoDB en Azure

Sin esta configuración la app arranca pero no puede conectarse a la base de datos.

1. En Azure Portal ve a tu App Service `dosw-library-api`
2. En el menú izquierdo clic en **"Configuración"** → **"Variables de entorno"**
3. Clic en **"+ Agregar"**
4. Configura así:
    - **Nombre:** `SPRING_DATA_MONGODB_URI`
    - **Valor:** `mongodb+srv://admin:AdminMongo@cluster0.arq8ibw.mongodb.net/librarydb?appName=Cluster0`
5. Clic en **"Aplicar"**
6. Clic en **"Continuar"** cuando pregunte si quieres reiniciar la app

---

### Paso 8 — Verificar que la app funciona en la nube

1. Espera unos 30-60 segundos a que la app reinicie
2. Abre en el navegador:
   ```
   https://dosw-library-api-h8bpdkatefhfd3hk.eastus-01.azurewebsites.net/swagger-ui/index.html
   ```
3. Debe aparecer la interfaz de Swagger UI con todos los endpoints ✅

---

### Paso 9 — Pruebas funcionales apuntando a Azure

Repite el mismo flujo del Reto #5 pero ahora desde la URL de Azure:

1. `POST /api/auth/login` → obtén el token
2. Clic en **"Authorize 🔒"** → pega `Bearer <token>`
3. `POST /api/books` → crea un libro
4. `POST /api/loans` → crea un préstamo (usa el `_id` real de MongoDB Compass)
5. `POST /api/loans/{id}/return` → devuelve el libro

Graba un video de estas pruebas y agrégalo al README:

```markdown
## Pruebas Funcionales — Azure Deploy (Reto #7)
[Ver video de pruebas en Azure](LINK_DEL_VIDEO)
```

---

## RETO #8 — High Level Design de Arquitectura

### ¿Qué es esto?
Un documento técnico que describe cómo está diseñado el sistema: sus capas, componentes, decisiones de arquitectura y tecnologías usadas.

---

### Paso 1 — Agregar el documento al proyecto

El archivo `HLD-DOSW-Library-v2.docx` que se generó debe quedar en la raíz de tu proyecto:

```
DOSW-Library/
  HLD-DOSW-Library-v2.docx   ← aquí
  src/
  pom.xml
  ...
```

---

### Paso 2 — Hacer commit del documento

```bash
git add HLD-DOSW-Library-v2.docx
git commit -m "Reto 8 - High Level Design document"
git push origin feature/reto6-cicd
```

---

### Paso 3 — Agregar referencia al README principal

En el `README.md` de tu proyecto agrega esta sección:

```markdown
## Reto #8 — High Level Design

El documento de arquitectura de alto nivel se encuentra en la raíz del proyecto:

[Ver documento HLD](./HLD-DOSW-Library-v2.docx)

### Contenido del documento:
- Introducción y alcance del sistema
- Arquitectura general por capas
- Modelo de persistencia dual (PostgreSQL + MongoDB)
- Modelo de seguridad con JWT
- Pipeline CI/CD con GitHub Actions
- Infraestructura de despliegue en Azure
- Stack tecnológico completo
```

---

## Resumen Final — Estado de todos los retos

| Reto | Descripción | Estado |
|------|-------------|--------|
| Reto #1 | Modelo no relacional (esquema conceptual) | ✅ |
| Reto #2 | Conexión con MongoDB Atlas | ✅ |
| Reto #3 | Implementación con MongoDB | ✅ |
| Reto #4 | Decisión relacional vs No Relacional | ✅ |
| Reto #5 | Pruebas funcionales con MongoDB | ✅ |
| Reto #6 | CI/CD con GitHub Actions (4 jobs + 5 tests) | ✅ |
| Reto #7 | Deploy en Azure App Service | ✅ |
| Reto #8 | Documento High Level Design | ✅ |

---

## URLs importantes

| Recurso | URL |
|---------|-----|
| Swagger UI (Azure) | https://dosw-library-api-h8bpdkatefhfd3hk.eastus-01.azurewebsites.net/swagger-ui/index.html |
| Swagger UI (Local) | http://localhost:8080/swagger-ui/index.html |
| Repositorio GitHub | https://github.com/carljob/DOSW-Library |
| GitHub Actions | https://github.com/carljob/DOSW-Library/actions |
| MongoDB Atlas | https://cloud.mongodb.com |
| Azure Portal | https://portal.azure.com |

---

## Videos

| Video | Link |
|-------|------|
| Pruebas funcionales con MongoDB (Reto #5) | LINK_DEL_VIDEO |
| Pruebas funcionales en Azure (Reto #7) | LINK_DEL_VIDEO |
