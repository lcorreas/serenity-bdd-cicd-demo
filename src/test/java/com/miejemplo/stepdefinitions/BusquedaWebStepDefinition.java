package com.miejemplo.stepdefinitions;

import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import net.serenitybdd.screenplay.actors.OnStage;

import static com.miejemplo.hooks.AbrirNavegador.abrirUrl;
import static com.miejemplo.questions.TituloPagina.actual;
import static com.miejemplo.tasks.RealizarBusqueda.buscarTexto;
import static com.miejemplo.util.Constantes.ACTOR;
import static com.miejemplo.util.Constantes.WEB_URL;
import static net.serenitybdd.screenplay.GivenWhenThen.seeThat;
import static net.serenitybdd.screenplay.actors.OnStage.theActorInTheSpotlight;
import static org.hamcrest.CoreMatchers.containsString;

public class BusquedaWebStepDefinition {
    @Dado("un usuario se encuentra en la pagina inicial de Yahoo")
    public void unUsuarioSeEncuentraEnLaPaginaInicialDeYahoo() {
        OnStage.theActorCalled(ACTOR).attemptsTo(
                abrirUrl(WEB_URL)
        );
    }

    @Cuando("ingresa una frase {string}")
    public void ingresaUnaFrase(String frase) {
        theActorInTheSpotlight().attemptsTo(
                buscarTexto(frase)
        );
    }

    @Entonces("el sistema debe mostrar en el titulo de la pagina la frase {string}")
    public void elSistemaDebeMostrarEnElTituloDeLaPaginaLaFrase(String frase) {
        theActorInTheSpotlight().should(
                seeThat(actual(),
                        containsString(frase))
        );
    }
}
