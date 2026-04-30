package com.miejemplo.tasks;

import com.miejemplo.ui.PaginaInicio;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.Tasks;
import net.serenitybdd.screenplay.actions.Clear;
import net.serenitybdd.screenplay.actions.Enter;
import net.serenitybdd.screenplay.actions.SendKeys;
import org.openqa.selenium.Keys;

public class RealizarBusqueda implements Task {

    private final String texto;

    public RealizarBusqueda(String texto) {
        this.texto = texto;
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        actor.attemptsTo(
                Clear.field(PaginaInicio.CAJA_BUSQUEDA),
                Enter.theValue(texto).into(PaginaInicio.CAJA_BUSQUEDA),
                SendKeys.of(Keys.ENTER).into(PaginaInicio.CAJA_BUSQUEDA)
        );
    }

    public static RealizarBusqueda buscarTexto(String texto) {
        return Tasks.instrumented(RealizarBusqueda.class, texto);
    }
}
