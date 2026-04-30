# Preguntas y Respuestas — CI/CD con Serenity BDD

Preparación para entrevistas y conversaciones técnicas sobre integración de
pruebas en pipelines de CI/CD.

---

## Preguntas conceptuales

**¿Qué es CI/CD y cómo se relaciona con QA?**

CI (Continuous Integration) es la práctica de integrar y validar automáticamente
cada cambio de código mediante compilación y pruebas automatizadas. CD
(Continuous Delivery/Deployment) extiende eso hasta dejar el software listo para
despliegue o desplegarlo directamente. Para QA, significa que las pruebas
automatizadas se ejecutan en cada cambio sin intervención manual, detectando
defectos de forma temprana y continua.

---

**¿Qué es Shift Left Testing y cómo lo implementaste?**

Shift Left Testing es la práctica de adelantar las pruebas en el ciclo de
desarrollo, en lugar de probar solo al final. Lo implementé configurando el
pipeline para que ejecute pruebas automáticamente en cada Pull Request. Antes de
que cualquier código entre a la rama principal, las pruebas de humo (`@smoke`)
ya han corrido y validado que nada está roto. El dev recibe el resultado en
minutos, no días.

---

**¿Qué diferencia hay entre un artifact y GitHub Pages en el contexto del reporte Serenity?**

Un artifact es un archivo comprimido (ZIP) que se genera en el pipeline y se puede
descargar manualmente desde la interfaz de GitHub. Tiene una vigencia limitada
(en nuestro caso 7 días). GitHub Pages en cambio publica el reporte como un sitio
web en una URL pública permanente que se actualiza automáticamente en cada
ejecución. Es mucho más práctico para compartir resultados con el equipo o con
stakeholders sin que tengan que descargar nada.

---

**¿Por qué Chrome necesita modo headless en CI/CD?**

Los servidores de CI/CD (como los agentes de GitHub Actions) son máquinas Linux
sin entorno gráfico, es decir, no tienen pantalla ni display. Chrome en modo
normal intenta abrir una ventana y falla porque no hay donde mostrarla. El modo
headless permite que Chrome funcione completamente en memoria, sin necesidad de
interfaz visual, ejecutando las mismas interacciones y validaciones pero sin
renderizar en pantalla.

---

**¿Cómo controlaste el modo headless sin cambiar el código para cada entorno?**

Usé la sintaxis HOCON `${?HEADLESS}` en el archivo `serenity.conf`. El símbolo
`?` hace que la propiedad solo tome valor si la variable de entorno `HEADLESS`
existe. Localmente no la defino, así Chrome abre con ventana como siempre. En el
pipeline de GitHub Actions defino `HEADLESS: "true"` como variable de entorno del
paso de ejecución. De esta forma el mismo código funciona en ambos contextos sin
ningún cambio.

---

**¿Qué son los tags de Cucumber y para qué los usas en CI/CD?**

Los tags son etiquetas que se añaden a los features o escenarios en Cucumber para
clasificarlos. En CI/CD los uso para ejecutar subconjuntos de pruebas según el
contexto. Por ejemplo, en un Pull Request solo ejecuto los escenarios con `@smoke`
(los más críticos y rápidos) para no bloquear al desarrollador. Cuando el código
llega a la rama principal ejecuto la suite `@regression` completa. También
classifiqué por tipo (`@api`, `@web`) para poder ejecutar solo pruebas de API o
solo pruebas web cuando se necesite.

---

**¿Qué es un trigger en GitHub Actions y cuáles configuraste?**

Un trigger es el evento que dispara la ejecución del pipeline. Configuré tres:
`push` a `main` (cuando se mergea código), `pull_request` hacia `main` (cuando se
abre o actualiza un PR) y `workflow_dispatch` (ejecución manual con parámetros
desde la interfaz de GitHub). Cada trigger tiene un comportamiento diferente: el
PR ejecuta solo smoke, el push a main ejecuta regresión completa y el manual
permite elegir entorno y tags libremente.

---

**¿Qué es `workflow_dispatch` y cuál es su utilidad práctica?**

Es un trigger especial que permite lanzar el pipeline manualmente desde la
interfaz de GitHub, sin necesidad de hacer un push. Lo configuré con parámetros
de entrada: el entorno (dev o staging) y los tags de Cucumber. Esto es muy útil
en escenarios reales como: ejecutar pruebas en staging antes de una demo,
lanzar solo las pruebas de API después de un cambio en el backend, o ejecutar
una regresión puntual sin esperar al ciclo de noche.

---

**¿Qué significa `if: always()` en un step del pipeline?**

Significa que ese step se ejecuta siempre, independientemente de si los steps
anteriores fallaron o no. Lo usé en el paso de subida del reporte Serenity como
artifact. Si las pruebas fallan, aún necesito ver el reporte para entender qué
falló. Sin `if: always()`, si el step de pruebas falla, el step de reporte
también se saltaría y perdería esa información crítica.

---

**¿Qué es `needs` en GitHub Actions?**

Es una dependencia entre jobs. El job `deploy` tiene `needs: test`, lo que
significa que solo empieza cuando el job `test` termina exitosamente. Esto garantiza
que no se publique un reporte en GitHub Pages si las pruebas fallaron o si el
reporte aún no se generó. Sin `needs`, ambos jobs correrían en paralelo y
`deploy` intentaría publicar archivos que aún no existen.

---

**¿Por qué el job de deploy no corre en Pull Requests?**

Porque tiene la condición `if: github.ref == 'refs/heads/main'`. En un PR, el
código aún no está en `main`, por lo que esa condición es falsa. No tiene sentido
sobreescribir el reporte público con el resultado de un PR que todavía no fue
aprobado. El reporte en GitHub Pages debe reflejar el estado del código que ya
pasó por revisión y está en la rama principal.

---

**¿Qué es Pipeline as Code y por qué es importante?**

Pipeline as Code es el concepto de definir la configuración del pipeline en un
archivo de texto versionado junto con el código fuente (en nuestro caso
`.github/workflows/ci.yml`). Las ventajas son: el pipeline tiene historial de
cambios en git igual que el código, cualquier modificación al pipeline pasa por
revisión en un PR, y si el proyecto se mueve a otro repositorio el pipeline viaja
con él. Es la forma profesional de manejar CI/CD, en contraste con configurar
pipelines desde interfaces gráficas donde los cambios no quedan rastreados.

---

## Preguntas situacionales

**Si un pipeline falla en PR, ¿qué haces?**

Primero abro la pestaña Actions en GitHub y entro al run fallido. Reviso el step
que falló y expando los logs para ver el error específico. Si el error es de
compilación, lo comunico al desarrollador. Si es una prueba que falló, descargo
el artifact del reporte Serenity para ver exactamente qué escenario falló, qué
esperaba y qué obtuvo. Si el fallo es intermitente (flaky test), lo documento como
issue y evalúo si es un problema de estabilidad de la prueba o del ambiente.

---

**¿Cómo garantizas que el pipeline no sea un cuello de botella para el equipo?**

Aplicando la estrategia de ejecución selectiva: en PRs solo corro `@smoke` (pruebas
rápidas que dan feedback en ~1 minuto), y la regresión completa la dejo para el
merge a main o para ejecuciones nocturnas. También clasifico las pruebas por tipo
(`@api`, `@web`) para poder ejecutar solo lo relevante cuando se hacen cambios
puntuales. El objetivo es que el pipeline dé feedback útil lo más rápido posible.

---

**¿Cómo manejas credenciales o datos sensibles en el pipeline?**

Los datos sensibles nunca se escriben en el archivo YAML directamente porque ese
archivo está en el repositorio y es público. En GitHub Actions se usan **Secrets**:
valores cifrados que se configuran en Settings > Secrets del repositorio y se
referencian en el workflow como `${{ secrets.NOMBRE_SECRET }}`. Por ejemplo, una
URL de base de datos de staging se guardaría como secret y se pasaría como
variable de entorno al step de pruebas.

---

**¿Qué diferencia hay entre GitHub Actions y Jenkins?**

GitHub Actions es una plataforma nativa de GitHub, con configuración en YAML,
sin infraestructura propia que mantener, y con miles de actions reutilizables en
el marketplace. Es ideal para proyectos en GitHub y equipos que quieren empezar
rápido. Jenkins es una herramienta open source que se instala en servidores
propios, usa Groovy para los Jenkinsfiles, y es altamente personalizable. Es muy
común en empresas grandes del sector bancario y financiero porque permite control
total sobre la infraestructura y es más maduro para ambientes enterprise con
requisitos de seguridad estrictos. Los conceptos son los mismos: pipeline,
stages, steps, triggers — solo cambia la sintaxis y el alojamiento.

---

**¿Cómo integrarías pruebas de base de datos MySQL en el pipeline?**

Hay dos enfoques. El primero es usar un servicio de base de datos en el propio
pipeline: GitHub Actions permite levantar contenedores Docker como servicios
(MySQL, PostgreSQL) dentro del job, ideal para pruebas de integración aisladas.
El segundo es conectarse a un ambiente de pruebas externo, pasando las
credenciales como Secrets y la URL como variable de entorno. Las pruebas que
validan datos en base de datos se etiquetarían con un tag específico (por ejemplo
`@db`) para poder incluirlas o excluirlas según el ambiente disponible.
