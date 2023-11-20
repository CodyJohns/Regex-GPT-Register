package com.cdjmdev.regex;

import com.cdjmdev.oracle.dao.DAOFactory;
import com.cdjmdev.oracle.dao.OracleDAOFactory;
import com.cdjmdev.regex.service.RegisterService;

public class RegisterController {

    private DAOFactory factory;
    private RegisterService service;

    public RegisterController() {
        factory = new OracleDAOFactory();
        service = new RegisterService(factory);
    }

    public static class RegisterRequest {
        public String id;
        public String password;
    }

    public static class RegisterResponse {
        public String authtoken;
        public String message;
        public int status = 200;
    }

    public RegisterResponse handleRequest(RegisterRequest request) {
        try {
            return service.register(request.id, request.password);
        } catch (Exception e) {
            RegisterResponse response = new RegisterResponse();
            response.status = 500;
            response.message = e.getMessage();

            return response;
        }
    }

}