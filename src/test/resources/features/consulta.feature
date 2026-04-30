#language: es

@smoke @regression @web
Característica: Búsqueda de una frase en un buscador web

  @smoke @regression @web
  Esquema del escenario: Búsqueda exitosa
    Dado un usuario se encuentra en la pagina inicial de Yahoo
    Cuando ingresa una frase "<frase>"
    Entonces el sistema debe mostrar en el titulo de la pagina la frase "<frase>"

    Ejemplos:
      |     frase     |
      |Mi primer robot|
