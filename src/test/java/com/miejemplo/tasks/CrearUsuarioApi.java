package com.miejemplo.tasks;

import com.miejemplo.models.Usuario;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.Tasks;
import net.serenitybdd.screenplay.rest.interactions.Post;

import static io.restassured.http.ContentType.JSON;

public class CrearUsuarioApi implements Task {

    private final Usuario usuario;

    public CrearUsuarioApi(Usuario usuario) {
        this.usuario = usuario;
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        actor.attemptsTo(
                Post.to("/user")
                        .with(request -> request
                                .contentType(JSON)
                                .body(usuario))
        );
    }

    public static CrearUsuarioApi conDatos(Usuario usuario) {
        return Tasks.instrumented(CrearUsuarioApi.class, usuario);
    }
}
