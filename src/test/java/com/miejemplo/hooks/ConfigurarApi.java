package com.miejemplo.hooks;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.Tasks;
import net.serenitybdd.screenplay.rest.abilities.CallAnApi;

public class ConfigurarApi implements Task {

    private final String urlApi;

    public ConfigurarApi(String urlApi) {
        this.urlApi = urlApi;
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        actor.whoCan(CallAnApi.at(urlApi));
    }

    public static ConfigurarApi conUrl(String urlApi) {
        return Tasks.instrumented(ConfigurarApi.class, urlApi);
    }
}
