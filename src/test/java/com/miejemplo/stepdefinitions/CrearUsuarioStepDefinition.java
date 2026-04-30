package com.miejemplo.stepdefinitions;

import com.miejemplo.hooks.ConfigurarApi;
import com.miejemplo.models.Usuario;
import com.miejemplo.questions.CodigoRespuesta;
import com.miejemplo.tasks.CrearUsuarioApi;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import net.serenitybdd.screenplay.actors.OnStage;

import static com.miejemplo.util.Constantes.ACTOR;
import static com.miejemplo.util.Constantes.URL_API;
import static net.serenitybdd.screenplay.GivenWhenThen.seeThat;
import static net.serenitybdd.screenplay.actors.OnStage.theActorInTheSpotlight;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.CoreMatchers.equalTo;

public class CrearUsuarioStepDefinition {

    private Usuario usuario;

    @Given("que el servicio de usuarios de Petstore se encuentra disponible")
    public void queElServicioDeUsuariosDePetstoreSeEncuentraDisponible() {
        OnStage.theActorCalled(ACTOR).attemptsTo(
                ConfigurarApi.conUrl(URL_API)
        );
    }

    @Given("se preparan los datos del nuevo usuario con {string}, {string} y {string}")
    public void sePreparanLosDatosDelNuevoUsuarioConY(String username, String firstname, String lastname) {
        usuario = new Usuario(username, firstname, lastname);

    }

    @When("se envia la solicitud de creacion del usuario al sistema")
    public void seEnviaLaSolicitudDeCreacionDelUsuarioAlSistema() {
        theActorInTheSpotlight().attemptsTo(
                CrearUsuarioApi.conDatos(usuario)
        );
    }

    @Then("el sistema confirma que el usuario fue registrado exitosamente")
    public void elSistemaConfirmaQueElUsuarioFueRegistradoExitosamente() {
        theActorInTheSpotlight().should(
                seeThat(CodigoRespuesta.obtenido(), equalTo(SC_OK))
        );
    }
}
