package com.cooksys.assessment1Team3.mappers;

import com.cooksys.assessment1Team3.dtos.CredentialsDto;
import com.cooksys.assessment1Team3.entities.Credentials;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CredentialsMapper {
    CredentialsDto entityToDto(Credentials credentials);

    Credentials dtoToEntity(CredentialsDto dto);
}
