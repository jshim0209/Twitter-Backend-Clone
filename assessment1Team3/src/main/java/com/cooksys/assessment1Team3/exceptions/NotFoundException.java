package com.cooksys.assessment1Team3.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class NotFoundException extends RuntimeException {

    private static final long serialVersionUID = 2376272345723346347L;

    private String message;

}
