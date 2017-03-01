package org.cardiacatlas.xpacs.cucumber.stepdefs;

import org.cardiacatlas.xpacs.XpacswebApp;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.ResultActions;

import org.springframework.boot.test.context.SpringBootTest;

@WebAppConfiguration
@SpringBootTest
@ContextConfiguration(classes = XpacswebApp.class)
public abstract class StepDefs {

    protected ResultActions actions;

}
