package com.cooksys.assessment1Team3.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class UserAlreadyExistException extends RuntimeException{

    private static final long serialVersionUID = 2376474545723347347L;

    private String message;
}
