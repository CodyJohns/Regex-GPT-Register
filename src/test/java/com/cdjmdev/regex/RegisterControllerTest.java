package com.cdjmdev.regex;

import com.cdjmdev.oracle.dao.AuthtokenDAO;
import com.cdjmdev.oracle.dao.DAOFactory;
import com.cdjmdev.oracle.dao.UserDAO;
import com.cdjmdev.oracle.model.Authtoken;
import com.cdjmdev.oracle.model.User;
import com.cdjmdev.regex.service.RegisterService;
import com.fnproject.fn.testing.*;
import com.google.gson.Gson;
import org.junit.Rule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

public class RegisterControllerTest {

    @Rule
    public final FnTestingRule testing = FnTestingRule.createDefault();

    @Test
    @Disabled
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

    private DAOFactory dFactory;
    private AuthtokenDAO aDAO;
    private UserDAO uDAO;
    private Authtoken token;

    @BeforeEach
    void setup() {
        dFactory = mock(DAOFactory.class);
        aDAO = mock(AuthtokenDAO.class);
        uDAO = mock(UserDAO.class);

        token = new Authtoken();
        token.id = "1234567890";
        token.user_id = "";

        Mockito.when(aDAO.createNew(Mockito.any())).thenReturn(token);

        Mockito.when(dFactory.getAuthtokenDAO()).thenReturn(aDAO);
        Mockito.when(dFactory.getUserDAO()).thenReturn(uDAO);
    }

    @Test
    @DisplayName("Test register new user successfully")
    void testRegisterSuccessful() {

        Mockito.when(uDAO.getByEmail(Mockito.any())).thenThrow(NullPointerException.class);

        RegisterService service = new RegisterService(dFactory);

        RegisterController.RegisterRequest request = new RegisterController.RegisterRequest();
        request.id = "test@test.com";
        request.password = "1234567890";


        assertDoesNotThrow(() -> {
            RegisterController.RegisterResponse response = service.register(request);

            assertEquals(token.id, response.authtoken);
        });

        verify(uDAO).getByEmail(Mockito.any());
        verify(aDAO).createNew(Mockito.any());
    }

    @Test
    @DisplayName("Test register fields empty")
    void testRegisterFieldsEmpty() {

        Mockito.when(uDAO.getByEmail(Mockito.any())).thenThrow(NullPointerException.class);

        RegisterService service = new RegisterService(dFactory);

        RegisterController.RegisterRequest request = new RegisterController.RegisterRequest();

        assertThrows(IllegalArgumentException.class, () -> {
            service.register(request);
        });

        request.id = "";
        request.password = "";

        assertThrows(IllegalArgumentException.class, () -> {
            service.register(request);
        });
    }

    @Test
    @DisplayName("Test register user already exists")
    void testRegisterUserAlreadyExists() {

        Mockito.when(uDAO.getByEmail(Mockito.any())).thenReturn(new User("", "", "", ""));

        RegisterService service = new RegisterService(dFactory);

        RegisterController.RegisterRequest request = new RegisterController.RegisterRequest();
        request.id = "test@test.com";
        request.password = "1234567890";


        assertDoesNotThrow(() -> {
            RegisterController.RegisterResponse response = service.register(request);

            assertNull(response.authtoken);
        });

        verify(uDAO).getByEmail(Mockito.any());
        verify(aDAO, times(0)).createNew(Mockito.any());
    }
}