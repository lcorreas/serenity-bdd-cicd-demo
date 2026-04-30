# Resumen Completo: CI/CD con Serenity BDD y GitHub Actions

---

## ¿Qué construimos?

Un pipeline de integración continua que ejecuta automáticamente las pruebas
Serenity BDD (web con Selenium y API con REST Assured) cada vez que se sube
código a GitHub, y publica el reporte en una URL pública.

**Repositorio:** https://github.com/lcorreas/serenity-bdd-cicd-demo
**Reporte en vivo:** https://lcorreas.github.io/serenity-bdd-cicd-demo/

---

## Arquitectura del pipeline

```
Developer → git push → GitHub detecta evento
                              ↓
                    GitHub Actions se activa
                              ↓
                 Ubuntu Linux (servidor temporal)
                              ↓
              ┌───────────────────────────────┐
              │  Job: Run Serenity Tests      │
              │  1. Checkout del código       │
              │  2. Instalar Java 17          │
              │  3. Instalar Chrome           │
              │  4. Ejecutar pruebas          │
              │     (HEADLESS=true)           │
              │  5. Subir artefacto           │
              │  6. Empaquetar para Pages     │
              └───────────────────────────────┘
                              ↓
              ┌───────────────────────────────┐
              │  Job: Deploy to GitHub Pages  │
              │  → Publica reporte en URL     │
              └───────────────────────────────┘
```

---

## Stack tecnológico del proyecto

| Componente | Tecnología | Versión |
|---|---|---|
| Framework de pruebas | Serenity BDD | 5.3.2 |
| Lenguaje de escenarios | Cucumber (Gherkin) | 7.34.2 |
| Pruebas web | Selenium + Chrome | headless en CI |
| Pruebas API | REST Assured (serenity-rest-assured) | 5.3.2 |
| Build tool | Gradle | 9.0.0 |
| Lenguaje | Java | 17 |
| CI/CD | GitHub Actions | - |
| Reporte público | GitHub Pages | - |

---

## Cambios realizados al proyecto

### 1. `serenity.conf` — Modo headless por variable de entorno
**Problema:** Chrome necesita pantalla gráfica. Los servidores CI no tienen pantalla.
**Solución:** La propiedad `headless.mode` lee la variable de entorno `HEADLESS`.

```hocon
# Antes
headless.mode = false

# Después
headless.mode = ${?HEADLESS}
```

La sintaxis `${?VARIABLE}` es HOCON: "usa este valor solo si la variable existe".
- Local sin variable → Chrome con ventana (comportamiento normal)
- Pipeline con `HEADLESS=true` → Chrome sin pantalla

---

### 2. Features de Cucumber — Tags para ejecución selectiva
**Problema:** Sin tags, el pipeline ejecuta todo siempre, sin poder filtrar.
**Solución:** Agregar tags con propósito claro a cada escenario.

```gherkin
@smoke @regression @api
Feature: Registro exitoso de nuevos usuarios en la plataforma Petstore

  @smoke @regression @api
  Scenario Outline: Registro de un usuario con datos validos...
```

```gherkin
@smoke @regression @web
Característica: Búsqueda de una frase en un buscador web

  @smoke @regression @web
  Esquema del escenario: Búsqueda exitosa
```

| Tag | Significado |
|---|---|
| `@smoke` | Prueba crítica, ejecución rápida (~1 min) |
| `@regression` | Suite completa de regresión |
| `@api` | Prueba de API REST |
| `@web` | Prueba de interfaz web (Selenium) |

---

### 3. `build.gradle` — Soporte de filtro de tags desde línea de comandos

```groovy
test {
    useJUnitPlatform()
    systemProperty "cucumber.filter.tags", System.getProperty("cucumber.filter.tags", "")
}
```

Permite ejecutar con filtro:
```bash
./gradlew clean test aggregate -Dcucumber.filter.tags="@smoke"
./gradlew clean test aggregate -Dcucumber.filter.tags="@api"
```

---

### 4. `.gitignore` — Exclusiones correctas

Se agregaron:
```
target/    ← reportes Serenity generados (Maven/Gradle)
.idea/     ← configuración del IDE IntelliJ
```

**Regla de oro:** nunca versionar archivos generados automáticamente.
El pipeline los genera de nuevo en cada ejecución.

---

### 5. `.github/workflows/ci.yml` — El pipeline completo

```yaml
name: Serenity BDD - CI Pipeline

on:
  push:
    branches: [ "main" ]       # al mergear código
  pull_request:
    branches: [ "main" ]       # al abrir un PR
  workflow_dispatch:            # ejecución manual con parámetros
    inputs:
      environment:
        description: 'Entorno de pruebas'
        default: 'dev'
        type: choice
        options: [dev, staging]
      tags:
        description: 'Tags de Cucumber'
        default: '@regression'

permissions:
  contents: read
  pages: write
  id-token: write

jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v4
        with: { java-version: '17', distribution: 'temurin' }
      - run: chmod +x gradlew
      - uses: browser-actions/setup-chrome@v1
      - name: Run Serenity Tests
        env:
          HEADLESS: "true"
        run: |
          if [ "${{ github.event_name }}" = "pull_request" ]; then
            TAGS="@smoke"
          elif [ "${{ github.event_name }}" = "workflow_dispatch" ]; then
            TAGS="${{ github.event.inputs.tags }}"
          else
            TAGS="@regression"
          fi
          ./gradlew clean test aggregate -Dcucumber.filter.tags="$TAGS"
      - uses: actions/upload-artifact@v4
        if: always()
        with: { name: serenity-report, path: target/site/serenity/ }
      - uses: actions/configure-pages@v5
        if: github.ref == 'refs/heads/main'
      - uses: actions/upload-pages-artifact@v3
        if: github.ref == 'refs/heads/main'
        with: { path: target/site/serenity/ }

  deploy:
    needs: test
    if: github.ref == 'refs/heads/main'
    environment: { name: github-pages, url: ${{ steps.deployment.outputs.page_url }} }
    steps:
      - uses: actions/deploy-pages@v4
        id: deployment
```

---

## Lógica de ejecución por trigger

| Trigger | Tags ejecutados | Publica en Pages | Cuándo ocurre |
|---|---|---|---|
| `push` a `main` | `@regression` | ✅ Sí | Al hacer merge |
| `pull_request` | `@smoke` | ❌ No | Al abrir un PR |
| `workflow_dispatch` | El que elijas | ✅ Si va a main | Manual desde UI |

---

## Conceptos clave para dominar en entrevistas

### CI vs CD
- **CI (Continuous Integration):** automatizar la compilación y las pruebas en cada cambio de código
- **CD (Continuous Delivery):** el código validado queda listo para despliegue
- **CD (Continuous Deployment):** el despliegue también es automático

### Shift Left Testing
Mover las pruebas lo más temprano posible en el ciclo de desarrollo.
En lugar de probar al final del sprint, se prueba en cada commit.
El pipeline implementa Shift Left porque valida cada PR antes de que entre a `main`.

### Pipeline as Code
El pipeline está definido en un archivo YAML versionado en el repositorio
(`.github/workflows/ci.yml`). Ventajas:
- Historial de cambios del pipeline en git
- Revisión de cambios en PR como cualquier otro código
- Portable: si cambias de repositorio, llevas el pipeline contigo

### Artifacts vs GitHub Pages
| | Artifact | GitHub Pages |
|---|---|---|
| Acceso | Descarga ZIP | URL pública en navegador |
| Vigencia | 7 días (configurable) | Permanente hasta el próximo push |
| Uso | Revisión puntual | Compartir con el equipo |

### Variables de entorno en CI/CD
- Se definen en el paso con `env:`
- Permiten configurar el comportamiento sin tocar el código
- En GitHub también se pueden guardar como **Secrets** para valores sensibles (contraseñas, tokens)

---

## Flujo de trabajo diario con CI/CD (como QA)

```
1. Dev sube código en una rama
2. Dev abre un PR hacia main
3. Pipeline ejecuta @smoke automáticamente
4. Si falla → QA y Dev lo ven inmediatamente
5. Si pasa → PR puede mergearse
6. Al mergear → pipeline ejecuta @regression completa
7. Reporte actualizado en GitHub Pages
8. Cada noche (con schedule) → regresión automática en staging
```

---

## Comandos útiles para recordar

```bash
# Ejecutar todas las pruebas localmente
./gradlew clean test aggregate

# Ejecutar solo @smoke
./gradlew clean test aggregate -Dcucumber.filter.tags="@smoke"

# Ejecutar solo @api
./gradlew clean test aggregate -Dcucumber.filter.tags="@api"

# Simular modo headless localmente (PowerShell)
$env:HEADLESS="true"; .\gradlew clean test aggregate

# Ver estado del repo
git --no-pager status --short
git --no-pager log --oneline -5
```
