# Módulo 1 — Fundamentos de CI/CD para QA

---

## 1. ¿Qué es CI/CD?

**CI = Continuous Integration (Integración Continua)**
Cada vez que un desarrollador sube código al repositorio, se ejecuta automáticamente un proceso que compila, analiza y prueba ese código. El objetivo: detectar errores **lo antes posible**.

**CD = Continuous Delivery / Continuous Deployment**
- **Delivery:** el código validado queda listo para despliegue con un clic humano.
- **Deployment:** el despliegue también es automático, sin intervención humana.

```
Developer push → CI (build + test) → CD (deploy to staging) → CD (deploy to prod)
```

---

## 2. ¿Por qué le importa esto a un QA?

En el mundo tradicional, QA prueba **al final**. En CI/CD, QA está en **cada iteración**:

| Sin CI/CD | Con CI/CD |
|---|---|
| Pruebas manuales al final del sprint | Pruebas automáticas en cada commit |
| Bugs detectados tarde (caro) | Bugs detectados al instante (barato) |
| "Funciona en mi máquina" | Entorno estandarizado siempre |
| Reporte PDF adjunto por correo | Reporte publicado en URL automáticamente |

Esto se llama **Shift Left Testing** — uno de los requisitos de la vacante.

---

## 3. Anatomía de un Pipeline

Un pipeline tiene esta estructura jerárquica:

```
PIPELINE
 └── STAGE (ej: "Build", "Test", "Deploy")
      └── JOB (ej: "Run API Tests")
           └── STEP (ej: "Execute ./gradlew test aggregate")
```

**Triggers** — qué dispara el pipeline:
- `push` → cada vez que alguien sube código
- `pull_request` → cuando se abre un PR (ideal para smoke tests)
- `schedule` → cron job (ej: regresión completa cada noche a las 2am)
- `manual` → el QA lo dispara cuando quiere

---

## 4. Plataformas CI/CD más usadas

| Plataforma | Dónde se usa | Archivo de config |
|---|---|---|
| **GitHub Actions** | Proyectos en GitHub | `.github/workflows/ci.yml` |
| **GitLab CI** | Proyectos en GitLab | `.gitlab-ci.yml` |
| **Jenkins** | Empresas grandes (bancos) | `Jenkinsfile` |
| **Azure DevOps** | Ecosistema Microsoft | `azure-pipelines.yml` |
| **CircleCI / Bitbucket** | Startups | config específica |

> En el sector bancario/financiero, **Jenkins** es el más común. GitHub Actions es el más fácil para
> aprender los conceptos y luego se trasladan a Jenkins fácilmente.

---

## 5. ¿Dónde encaja tu proyecto Serenity?

```
Developer sube código
        ↓
  GitHub Actions se activa
        ↓
  Instala Java 17
        ↓
  Ejecuta: ./gradlew clean test aggregate
        ↓
  Si falla → ❌ notifica, bloquea el merge
  Si pasa  → ✅ publica reporte Serenity
        ↓
  Deploy (si aplica)
```

Tu proyecto ya tiene todo lo necesario: Gradle, los runners y los features. Solo falta el archivo YAML del pipeline.

---

## Checklist del Módulo 1

- [x] Entender qué es CI y qué es CD
- [x] Entender la anatomía de un pipeline
- [x] Conocer las plataformas más usadas
- [x] Entender dónde encaja Serenity BDD en el pipeline
- [x] Java 17 instalado localmente
- [ ] Proyecto subido a GitHub
