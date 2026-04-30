# Plan de Estudio: Integración de Pruebas en Pipelines CI/CD
> Proyecto base: serenity-bdd-api-web-demo | Stack: Serenity BDD 5.3.2 + Cucumber + Gradle + Selenium + REST Assured

---

## Módulo 1 — Fundamentos (Teoría esencial)
**Estado:** ✅ En curso

**Temas:**
- Qué es CI/CD y por qué importa en QA
- Diferencia entre CI y CD
- Anatomía de un pipeline: trigger → stages → jobs → steps
- Plataformas comunes: GitHub Actions, GitLab CI, Jenkins, Azure DevOps

**Entregable:** Diagrama mental de cómo encaja QA en un pipeline.

---

## Módulo 2 — Preparar el proyecto para CI/CD
**Estado:** ⬜ Pendiente

**Temas:**
1. Activar modo headless en `serenity.conf` vía variable de entorno
2. Externalizar datos sensibles con variables de entorno
3. Usar tags de Cucumber para ejecutar subconjuntos de pruebas
4. Verificar que `./gradlew test aggregate` corra limpiamente

**Entregable:** El proyecto corre en modo headless y acepta configuración por entorno.

---

## Módulo 3 — Primer pipeline con GitHub Actions
**Estado:** ⬜ Pendiente

**Temas:**
1. Crear `.github/workflows/ci.yml`
2. Configurar triggers: push, pull_request, schedule (cron)
3. Instalar Java + correr `gradlew test aggregate` en el agente
4. Publicar reporte Serenity como artefacto descargable

**Entregable:** Pipeline funcional que se ejecuta en cada push.

---

## Módulo 4 — Publicar reporte Serenity en GitHub Pages
**Estado:** ⬜ Pendiente

**Temas:**
1. Tomar la carpeta de reportes y publicarla como sitio web
2. URL pública con resultados de cada ejecución

**Entregable:** URL pública con reporte Serenity actualizado automáticamente.

---

## Módulo 5 — Múltiples entornos y ejecución selectiva
**Estado:** ⬜ Pendiente

**Temas:**
1. Parámetros de pipeline para elegir entorno (dev, staging)
2. Tags @smoke en PR vs regresión completa en main
3. Notificaciones de resultado

**Entregable:** Pipeline que diferencia smoke de regresión completa.

---

## Módulo 6 — Jenkins (Complementario)
**Estado:** ⬜ Pendiente

**Temas:**
1. Instalar Jenkins con Docker localmente
2. Crear Jenkinsfile (pipeline as code)
3. Comparar GitHub Actions vs Jenkins

**Entregable:** Jenkinsfile equivalente al workflow de GitHub Actions.

---

## Ruta sugerida
```
Módulo 1 → Módulo 2 → Módulo 3 → Módulo 4 → Módulo 5 → Módulo 6 (opcional)
```
