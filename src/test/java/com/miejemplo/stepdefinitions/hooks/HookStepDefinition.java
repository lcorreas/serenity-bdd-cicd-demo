package com.miejemplo.stepdefinitions.hooks;

import io.cucumber.java.Before;
import net.serenitybdd.screenplay.actors.OnStage;
import net.serenitybdd.screenplay.actors.OnlineCast;

public class HookStepDefinition {

    @Before
    public void iniciarEscenario(){
        OnStage.setTheStage(new OnlineCast());
    }

    
}
