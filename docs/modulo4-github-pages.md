# Módulo 4 — Publicar el Reporte Serenity en GitHub Pages

---

## Objetivo
Publicar automáticamente el reporte Serenity en una URL pública después de cada
ejecución del pipeline, sin necesidad de descargar artefactos.

**URL del reporte:** https://lcorreas.github.io/serenity-bdd-cicd-demo/

---

## ¿Qué es GitHub Pages?
GitHub Pages es un servicio gratuito de GitHub que convierte archivos HTML/CSS/JS
de un repositorio en un sitio web accesible públicamente.

En nuestro caso: la carpeta `target/site/serenity/` que genera Serenity BDD
(con su `index.html`) se publica como sitio web automáticamente.

---

## Configuración requerida en GitHub

1. Ir a: `https://github.com/TU_USUARIO/serenity-bdd-cicd-demo/settings/pages`
2. En **Source** seleccionar: `GitHub Actions`
3. Guardar

---

## Cambios en `.github/workflows/ci.yml`

### 1. Permisos agregados al workflow
```yaml
permissions:
  contents: read
  pages: write       # permite escribir en GitHub Pages
  id-token: write    # permite autenticarse con el servicio de Pages
```

### 2. Pasos nuevos en el job `test`
```yaml
- name: Configure GitHub Pages
  if: github.ref == 'refs/heads/main'
  uses: actions/configure-pages@v5

- name: Upload report to GitHub Pages
  if: github.ref == 'refs/heads/main'
  uses: actions/upload-pages-artifact@v3
  with:
    path: target/site/serenity/
```

### 3. Nuevo job `deploy`
```yaml
deploy:
  name: Deploy to GitHub Pages
  runs-on: ubuntu-latest
  needs: test
  if: github.ref == 'refs/heads/main'

  environment:
    name: github-pages
    url: ${{ steps.deployment.outputs.page_url }}

  steps:
    - name: Deploy to GitHub Pages
      id: deployment
      uses: actions/deploy-pages@v4
```

---

## Flujo completo con GitHub Pages

```
push a main
    ↓
Job: Run Serenity Tests
    ├── Corre las pruebas
    ├── Genera target/site/serenity/
    └── Empaqueta el reporte para Pages
    ↓
Job: Deploy to GitHub Pages
    └── Publica en https://lcorreas.github.io/serenity-bdd-cicd-demo/
```

---

## ¿Por qué `if: github.ref == 'refs/heads/main'`?

Los pasos de publicación solo corren cuando el push es a `main`.

| Evento | ¿Publica en Pages? |
|---|---|
| Push a `main` | ✅ Sí — actualiza el reporte público |
| Pull Request | ❌ No — no tiene sentido sobreescribir el reporte oficial |

---

## ¿Cuándo se ejecutan las pruebas?

Esta es la parte más importante para entender CI/CD:

### Trigger: `push` a `main`
```yaml
on:
  push:
    branches: [ "main" ]
```
Cada vez que alguien hace `git push` con cambios hacia la rama `main`,
GitHub detecta el evento y lanza el pipeline automáticamente.

**Ejemplo real del proyecto:**
- Modificas un `.feature`, un step definition, o el `build.gradle`
- Ejecutas `git push`
- En segundos, GitHub Actions inicia el pipeline sin que hagas nada más

### Trigger: `pull_request` a `main`
```yaml
  pull_request:
    branches: [ "main" ]
```
Cuando alguien abre un Pull Request (PR) hacia `main`, el pipeline corre
**antes de permitir el merge**. Si las pruebas fallan, el PR queda bloqueado.

Este es el patrón más usado en equipos: nadie puede fusionar código roto.

### Trigger adicional que puedes agregar: `schedule` (cron)
```yaml
  schedule:
    - cron: '0 2 * * *'   # todos los días a las 2am
```
Ejecuta la suite completa de regresión automáticamente cada noche,
aunque nadie haya subido código ese día.

### Resumen visual

```
Desarrollador hace push
        ↓
GitHub detecta el evento (trigger)
        ↓
Crea una máquina virtual nueva (ubuntu-latest)
        ↓
Ejecuta todos los steps del workflow
        ↓
Reporta resultado (verde ✅ o rojo ❌)
        ↓
Si es push a main → publica el reporte en Pages
```

---

## Checklist del Módulo 4

- [x] GitHub Pages habilitado con source: GitHub Actions
- [x] Permisos `pages: write` e `id-token: write` en el workflow
- [x] Paso de configuración de Pages en job `test`
- [x] Paso de empaquetado del reporte en job `test`
- [x] Job `deploy` separado con `needs: test`
- [x] Reporte publicado y accesible en URL pública ✅
- [x] URL: https://lcorreas.github.io/serenity-bdd-cicd-demo/
