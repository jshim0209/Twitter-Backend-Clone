package com.cooksys.assessment1Team3.mappers;

import com.cooksys.assessment1Team3.dtos.ProfileDto;
import com.cooksys.assessment1Team3.entities.Profile;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProfileMapper {
    Profile dtoToEntity(ProfileDto profileDto);
}
