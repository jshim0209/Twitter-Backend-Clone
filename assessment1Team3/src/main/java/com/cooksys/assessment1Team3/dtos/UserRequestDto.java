package com.cooksys.assessment1Team3.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class UserRequestDto {
	private CredentialsDto credentials;
	private ProfileDto profile;
}
