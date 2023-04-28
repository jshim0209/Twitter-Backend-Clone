package com.cooksys.assessment1Team3.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor
@Data
public class Credentials {

	@Column(nullable = false, unique = true)
	private String username;

	@Column(nullable = false)
	private String password;

}
