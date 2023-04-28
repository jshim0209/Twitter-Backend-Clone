package com.cooksys.assessment1Team3.mappers;

import com.cooksys.assessment1Team3.dtos.HashtagDto;
import com.cooksys.assessment1Team3.entities.Hashtag;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface HashtagMapper {
    List<HashtagDto> entitiesToDtos(List<Hashtag> dtos);

    HashtagDto entityToDto(Hashtag hashtag);

    Hashtag dtoToEntity(HashtagDto dto);
}
