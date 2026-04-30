package com.miejemplo.questions;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.rest.abilities.CallAnApi;

public class CodigoRespuesta implements Question<Integer> {

    @Override
    public Integer answeredBy(Actor actor) {
        return CallAnApi.as(actor).getLastResponse().statusCode();
    }

    public static CodigoRespuesta obtenido() {
        return new CodigoRespuesta();
    }
}
