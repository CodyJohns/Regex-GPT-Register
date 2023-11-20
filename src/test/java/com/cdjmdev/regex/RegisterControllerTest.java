package com.cdjmdev.regex;

import com.fnproject.fn.testing.*;
import com.google.gson.Gson;
import org.junit.*;

import static org.junit.Assert.*;

public class RegisterControllerTest {

    @Rule
    public final FnTestingRule testing = FnTestingRule.createDefault();

    @Test
    public void shouldReturnGreeting() {
        Gson gson = new Gson();

        RegisterController.RegisterRequest request = new RegisterController.RegisterRequest();
        request.id = "test@test.com";
        request.password = "1234567890";

        testing.givenEvent().withBody(gson.toJson(request)).enqueue();
        testing.thenRun(RegisterController.class, "handleRequest");

        FnResult result = testing.getOnlyResult();
        String response = result.getBodyAsString();
        RegisterController.RegisterResponse responseObj = gson.fromJson(response, RegisterController.RegisterResponse.class);
        assertNotNull(responseObj);
        System.out.println(response);
    }

}