package com.cdjmdev.regex.service;

import com.cdjmdev.oracle.dao.DAOFactory;
import com.cdjmdev.oracle.model.Authtoken;
import com.cdjmdev.oracle.model.User;
import com.cdjmdev.regex.RegisterController;

public class RegisterService {
	private DAOFactory factory;

	public RegisterService(DAOFactory factory) {
		this.factory = factory;
	}

	public RegisterController.RegisterResponse register(RegisterController.RegisterRequest request) {

		if(request.id == null || request.id.isBlank() || request.password == null || request.password.isBlank())
			throw new IllegalArgumentException("Fields cannot be blank");

		RegisterController.RegisterResponse response = new RegisterController.RegisterResponse();

		User user;

		try {
			factory.getUserDAO().getByEmail(request.id);

			response.message = "User already exists with that email. " +
				"If you previously logged in using Google, please do so again using that method of login.";
			response.status = 409;
			return response;
		} catch(NullPointerException e) {
			user = new User("", "", request.password, request.id);
		}

		factory.getUserDAO().createNew(user);

		Authtoken token = factory.getAuthtokenDAO().createNew(user);

		response.authtoken = token.id;
		response.message = "User registered successfully.";

		return response;
	}
}
