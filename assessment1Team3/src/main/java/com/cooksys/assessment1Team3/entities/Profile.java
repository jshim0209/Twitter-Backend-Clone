package com.cooksys.assessment1Team3.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor
@Data
public class Profile {
    private String firstName;
    private String lastName;
    private String phone;

    // Make email non-null
    @Column(nullable = false)
    private String email;
}
