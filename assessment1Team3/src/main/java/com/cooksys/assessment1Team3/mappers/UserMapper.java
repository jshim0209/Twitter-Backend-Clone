package com.cooksys.assessment1Team3.mappers;

import com.cooksys.assessment1Team3.dtos.UserRequestDto;
import com.cooksys.assessment1Team3.dtos.UserResponseDto;
import com.cooksys.assessment1Team3.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = {ProfileMapper.class, CredentialsMapper.class})
public interface UserMapper {
    @Mapping(target = "username", source = "credentials.username")
    UserResponseDto entityToDto(User user);

    User responseDtoToEntity(UserResponseDto userResponseDto);

    User requestDtoToEntity(UserRequestDto userRequestDto);

    List<UserResponseDto> entitiesToDtos(List<User> users);
}