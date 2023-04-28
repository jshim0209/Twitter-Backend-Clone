package com.cooksys.assessment1Team3.services;

public interface ValidateService {

	boolean validateUserExists(String username);

	boolean validateUserAvailable(String username);

	boolean validateTagExists(String label);
}
