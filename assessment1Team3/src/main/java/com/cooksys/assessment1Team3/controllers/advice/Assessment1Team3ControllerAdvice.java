package com.cooksys.assessment1Team3.controllers.advice;

import com.cooksys.assessment1Team3.dtos.ErrorDto;
import com.cooksys.assessment1Team3.exceptions.BadRequestException;
import com.cooksys.assessment1Team3.exceptions.NotAuthorizedException;
import com.cooksys.assessment1Team3.exceptions.NotFoundException;
import com.cooksys.assessment1Team3.exceptions.UserAlreadyExistException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice(basePackages = { "com.cooksys.assessment1Team3.controllers"})
@ResponseBody
public class Assessment1Team3ControllerAdvice {

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(BadRequestException.class)
	public ErrorDto handleBadRequestException(BadRequestException badRequestException) {
		return new ErrorDto(badRequestException.getMessage());
	}

	@ResponseStatus(HttpStatus.UNAUTHORIZED)
	@ExceptionHandler(NotAuthorizedException.class)
	public ErrorDto handleNotAuthorizedException(NotAuthorizedException notAuthorizedException) {
		return new ErrorDto(notAuthorizedException.getMessage());
	}

	@ResponseStatus(HttpStatus.NOT_FOUND)
	@ExceptionHandler(NotFoundException.class)
	public ErrorDto handleNotFoundException(NotFoundException notFoundException) {
		return new ErrorDto(notFoundException.getMessage());
	}

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(UserAlreadyExistException.class)
	public ErrorDto handleUserAlreadyExistException(UserAlreadyExistException userAlreadyExistException) {
		return new ErrorDto(userAlreadyExistException.getMessage());
	}


}
