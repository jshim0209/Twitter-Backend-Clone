package com.cooksys.assessment1Team3.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class BadRequestException extends RuntimeException {

	private static final long serialVersionUID = -5917453038608287183L;

	private String message;
}
