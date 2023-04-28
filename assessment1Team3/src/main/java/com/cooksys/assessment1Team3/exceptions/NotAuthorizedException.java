package com.cooksys.assessment1Team3.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class NotAuthorizedException extends RuntimeException{

	private static final long serialVersionUID = 7834465387690995370L;

	private String message;

}
