# Módulo 5 — Múltiples Entornos y Ejecución Selectiva por Tags

---

## Objetivo
Hacer el pipeline inteligente: que ejecute pruebas diferentes según el contexto
(PR, merge a main, ejecución manual) y que permita elegir el entorno y los tags
desde la interfaz de GitHub sin tocar código.

---

## 1. Ejecución Selectiva por Tags según el Trigger

### El problema que resuelve
Sin esto, el pipeline ejecuta **todas** las pruebas siempre:
- En un PR: un dev espera 5 minutos para saber si puede mergear → frustrante
- En regresión nocturna: solo necesitas smoke → ineficiente

### La solución: lógica condicional en el paso de ejecución

```yaml
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
    echo "Ejecutando pruebas con tags: $TAGS"
    ./gradlew clean test aggregate -Dcucumber.filter.tags="$TAGS"
```

### Comportamiento resultante

| Trigger | Tags ejecutados | Propósito |
|---|---|---|
| `pull_request` | `@smoke` | Verificación rápida antes del merge |
| `push` a `main` | `@regression` | Suite completa después del merge |
| `workflow_dispatch` | El que elijas | Ejecución manual controlada |

### ¿Por qué `@smoke` en PR y `@regression` en main?
```
Developer abre PR
    ↓
Pipeline ejecuta solo @smoke (~1 min)
    ↓
Si pasan → PR aprobable, merge rápido
    ↓
Merge a main dispara @regression completa (~3 min)
    ↓
Si falla → el equipo lo sabe antes de que salga a producción
```

---

## 2. Trigger Manual con Parámetros (`workflow_dispatch`)

Permite lanzar el pipeline manualmente desde la UI de GitHub
eligiendo el entorno y los tags sin tocar código.

### Configuración en el workflow

```yaml
workflow_dispatch:
  inputs:
    environment:
      description: 'Entorno de pruebas'
      required: true
      default: 'dev'
      type: choice
      options:
        - dev
        - staging
    tags:
      description: 'Tags de Cucumber a ejecutar (ej: @smoke, @regression, @api)'
      required: false
      default: '@regression'
```

### Cómo usarlo
1. Ir a `https://github.com/lcorreas/serenity-bdd-cicd-demo/actions`
2. Clic en el workflow **Serenity BDD - CI Pipeline**
3. Clic en **Run workflow**
4. Elegir entorno (`dev` o `staging`) y los tags deseados
5. Clic en **Run workflow**

### Casos de uso prácticos

| Escenario | Entorno | Tags |
|---|---|---|
| Verificar solo APIs en staging | staging | `@api` |
| Smoke en dev antes de demo | dev | `@smoke` |
| Regresión completa manual | staging | `@regression` |
| Solo pruebas web | dev | `@web` |

---

## 3. Variable `github.event_name`

Es una variable de contexto que GitHub Actions provee automáticamente.
Contiene el nombre del evento que disparó el pipeline.

| Valor | Cuándo aparece |
|---|---|
| `push` | Al hacer `git push` |
| `pull_request` | Al abrir o actualizar un PR |
| `workflow_dispatch` | Al lanzar manualmente desde la UI |
| `schedule` | Al disparar por cron |

---

## 4. Cómo agregar más entornos en el futuro

Si el proyecto tuviera URLs de entorno configuradas (por ejemplo en `serenity.conf`),
podrías pasarlas como variable de entorno:

```yaml
- name: Run Serenity Tests
  env:
    HEADLESS: "true"
    ENV: ${{ github.event.inputs.environment }}
  run: ./gradlew clean test aggregate -Dcucumber.filter.tags="$TAGS"
```

Y en `serenity.conf` leer esa variable:
```hocon
base.url = ${?BASE_URL}
```

Y en el pipeline definir la URL según el entorno:
```yaml
BASE_URL: ${{ github.event.inputs.environment == 'staging' && 'https://staging.api.com' || 'https://dev.api.com' }}
```

---

## Checklist del Módulo 5

- [x] Lógica condicional de tags según el tipo de trigger
- [x] `@smoke` en PR — ejecución rápida
- [x] `@regression` en push a main — suite completa
- [x] `workflow_dispatch` con parámetros de entorno y tags
- [x] Pipeline ejecutado y verificado en GitHub Actions ✅
