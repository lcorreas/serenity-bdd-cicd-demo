# Módulo 2 — Preparar el Proyecto para CI/CD

---

## Objetivo
Adaptar el proyecto Serenity BDD para que pueda ejecutarse en un servidor de CI/CD,
donde no hay pantalla gráfica, y para que el pipeline pueda ejecutar subconjuntos
de pruebas según el contexto (PR, merge, regresión nocturna).

---

## 1. Modo Headless de Chrome via Variable de Entorno

**Archivo modificado:** `src/test/resources/serenity.conf`

**Problema:** En un servidor CI/CD no existe pantalla gráfica. Si Chrome intenta abrirse
en modo visual, el pipeline falla con un error de display.

**Solución:** Usar la sintaxis HOCON `${?VARIABLE}` para leer la variable de entorno `HEADLESS`.
El `?` significa "usa este valor solo si la variable existe". Si no existe, Serenity usa su
default (`false`).

```hocon
# Antes
headless.mode = false

# Después
headless.mode = ${?HEADLESS}
```

**Comportamiento por entorno:**

| Escenario | Variable | Resultado |
|---|---|---|
| Local normal | No existe | Chrome con ventana |
| Local simulando CI | `$env:HEADLESS="true"` | Chrome sin ventana |
| GitHub Actions | `HEADLESS: true` (en el YAML) | Chrome sin ventana |

---

## 2. Tags de Cucumber para Ejecución Selectiva

**Archivos modificados:** `src/test/resources/features/*.feature`

**Por qué importa en CI/CD:**
- En un **PR**: solo ejecutar `@smoke` (rápido, ~2 min) para no bloquear al dev
- En **merge a main**: ejecutar `@regression` completo
- En **pipeline nocturno**: ejecutar todo

**Estructura de tags aplicada:**

| Tag | Significado |
|---|---|
| `@smoke` | Prueba crítica, ejecución rápida |
| `@regression` | Suite completa de regresión |
| `@api` | Prueba de API (REST) |
| `@web` | Prueba de UI (Selenium) |

```gherkin
# crearusuario.feature
@smoke @regression @api
Feature: Registro exitoso de nuevos usuarios en la plataforma Petstore

  @smoke @regression @api
  Scenario Outline: Registro de un usuario con datos validos...
```

```gherkin
# consulta.feature
@smoke @regression @web
Característica: Búsqueda de una frase en un buscador web

  @smoke @regression @web
  Esquema del escenario: Búsqueda exitosa
```

---

## 3. Filtro de Tags en build.gradle

**Archivo modificado:** `build.gradle`

Permite pasar el tag desde la línea de comandos o desde el pipeline:

```groovy
test {
    useJUnitPlatform()
    systemProperty "cucumber.filter.tags", System.getProperty("cucumber.filter.tags", "")
}
```

**Cómo usarlo:**

```bash
# Ejecutar solo pruebas @smoke
./gradlew clean test aggregate -Dcucumber.filter.tags="@smoke"

# Ejecutar solo pruebas @api
./gradlew clean test aggregate -Dcucumber.filter.tags="@api"

# Ejecutar todo (sin filtro)
./gradlew clean test aggregate
```

---

## 4. .gitignore Actualizado

Se agregaron exclusiones clave para no subir archivos generados al repositorio:

```
build/       ← salida de Gradle (ya existía)
target/      ← reportes Serenity generados (agregado)
.idea/       ← configuración IntelliJ (agregado)
```

---

## Checklist del Módulo 2

- [x] Modo headless via variable de entorno `HEADLESS`
- [x] Tags `@smoke`, `@regression`, `@api`, `@web` en los features
- [x] Filtro de tags por `System.getProperty` en `build.gradle`
- [x] Pruebas ejecutadas localmente — `BUILD SUCCESSFUL`
- [x] `.gitignore` corregido — `target/` y `.idea/` excluidos
- [x] Cambios pusheados a GitHub
