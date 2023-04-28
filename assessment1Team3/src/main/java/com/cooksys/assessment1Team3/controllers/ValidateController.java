package com.cooksys.assessment1Team3.controllers;

import com.cooksys.assessment1Team3.services.ValidateService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/validate")
public class ValidateController {

	private final ValidateService validateService;

	@GetMapping("/username/exists/@{username}")
	public boolean validateUserExists(@PathVariable String username) {
		return validateService.validateUserExists(username);
	}

	@GetMapping("/username/available/@{username}")
	public boolean validateUserAvailable(@PathVariable String username) {
		return validateService.validateUserAvailable(username);
	}

	@GetMapping("/tag/exists/{label}")
	public boolean validateTagExists(@PathVariable String label) {
		return validateService.validateTagExists(label);
	}
}
