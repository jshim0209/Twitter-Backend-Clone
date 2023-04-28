package com.cooksys.assessment1Team3.services;

import com.cooksys.assessment1Team3.dtos.HashtagDto;
import com.cooksys.assessment1Team3.dtos.TweetResponseDto;

import java.util.List;

public interface HashtagService {

	List<HashtagDto> getAllHashtags();

	List<TweetResponseDto> getTweetsByHashtag(String label);

}
