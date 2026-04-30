# Módulo 3 — Primer Pipeline con GitHub Actions

---

## Objetivo
Crear un archivo `.github/workflows/ci.yml` que ejecute automáticamente las pruebas
Serenity BDD en cada `push` o `pull_request` hacia `main`, en un servidor de GitHub,
sin intervención humana.

---

## Resultado obtenido
- Pipeline ejecutado exitosamente: `BUILD SUCCESSFUL`
- Tiempo de ejecución: **2m 17s**
- Todos los steps en verde ✅

---

## Estructura del Pipeline

```
push/PR a main
      ↓
  GitHub Actions (ubuntu-latest)
      ↓
  [Job: Run Serenity Tests]
   ├── Set up job              (2s)
   ├── Checkout repository     (1s)
   ├── Set up Java 17          (1s)
   ├── Grant execute permission to gradlew (0s)
   ├── Install Google Chrome   (12s)
   ├── Run Serenity Tests      (1m 52s)
   ├── Upload Serenity Report  (5s)
   └── Complete job            (0s)
```

---

## Archivo creado: `.github/workflows/ci.yml`

```yaml
name: Serenity BDD - CI Pipeline

on:
  push:
    branches: [ "main" ]
  pull_request:
    branches: [ "main" ]

jobs:
  test:
    name: Run Serenity Tests
    runs-on: ubuntu-latest

    steps:
      - name: Checkout repository
        uses: actions/checkout@v4

      - name: Set up Java 17
        uses: actions/setup-java@v4
        with:
          java-version: '17'
          distribution: 'temurin'

      - name: Grant execute permission to gradlew
        run: chmod +x gradlew

      - name: Install Google Chrome
        uses: browser-actions/setup-chrome@v1

      - name: Run Serenity Tests
        env:
          HEADLESS: "true"
        run: ./gradlew clean test aggregate

      - name: Upload Serenity Report
        if: always()
        uses: actions/upload-artifact@v4
        with:
          name: serenity-report
          path: target/site/serenity/
          retention-days: 7
```

---

## Explicación de cada bloque

| Sección | Qué hace |
|---|---|
| `on: push/pull_request` | Triggers — cuándo se ejecuta el pipeline |
| `runs-on: ubuntu-latest` | El agente es Linux (sin pantalla gráfica) |
| `actions/checkout@v4` | Descarga el código del repo |
| `actions/setup-java@v4` | Instala Java 17 (Temurin/Eclipse) |
| `chmod +x gradlew` | Da permisos de ejecución a Gradle en Linux |
| `browser-actions/setup-chrome@v1` | Instala Chrome en el agente |
| `HEADLESS: "true"` | Variable de entorno que activa modo headless (Módulo 2) |
| `./gradlew clean test aggregate` | Ejecuta pruebas y genera reporte Serenity |
| `upload-artifact@v4` | Guarda el reporte como archivo descargable |
| `if: always()` | El reporte se sube incluso si las pruebas fallan |

---

## Cómo descargar el reporte Serenity generado

1. Ir a: `https://github.com/lcorreas/serenity-bdd-cicd-demo/actions`
2. Clic en el run que quieres revisar
3. Hacer scroll hasta el final de la página → sección **Artifacts**
4. Clic en **serenity-report** → se descarga un ZIP
5. Descomprimir el ZIP
6. Abrir `index.html` en el navegador

> Los artefactos se eliminan automáticamente después de **7 días** (`retention-days: 7`)

---

## Conceptos aprendidos

| Concepto | Descripción |
|---|---|
| **Workflow** | El archivo YAML completo que define el pipeline |
| **Job** | Un conjunto de steps que corre en un agente |
| **Step** | Una acción individual dentro del job |
| **Action** | Bloque reutilizable (`uses: actions/...`) |
| **Artifact** | Archivo generado por el pipeline y guardado para descarga |
| **Trigger** | Evento que dispara el pipeline (push, PR, schedule) |
| `if: always()` | El step corre incluso si pasos anteriores fallaron |

---

## Checklist del Módulo 3

- [x] Archivo `.github/workflows/ci.yml` creado
- [x] Pipeline disparado automáticamente con el push
- [x] Java 17 instalado en el agente
- [x] Chrome instalado y corriendo en modo headless
- [x] `./gradlew clean test aggregate` exitoso en la nube
- [x] Reporte Serenity subido como artefacto descargable
- [x] Pipeline verde en **2m 17s** ✅
