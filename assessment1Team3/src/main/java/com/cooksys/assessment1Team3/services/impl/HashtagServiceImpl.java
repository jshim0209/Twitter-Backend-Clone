package com.cooksys.assessment1Team3.services.impl;

import com.cooksys.assessment1Team3.dtos.HashtagDto;
import com.cooksys.assessment1Team3.dtos.TweetResponseDto;
import com.cooksys.assessment1Team3.entities.Hashtag;
import com.cooksys.assessment1Team3.entities.Tweet;
import com.cooksys.assessment1Team3.exceptions.NotFoundException;
import com.cooksys.assessment1Team3.mappers.HashtagMapper;
import com.cooksys.assessment1Team3.mappers.TweetMapper;
import com.cooksys.assessment1Team3.repositories.HashtagRepository;
import com.cooksys.assessment1Team3.services.HashtagService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HashtagServiceImpl implements HashtagService {

	private final HashtagRepository hashtagRepository;
	private final HashtagMapper hashtagMapper;
	private final TweetMapper tweetMapper;

	@Override
	public List<HashtagDto> getAllHashtags() {
		return hashtagMapper.entitiesToDtos(hashtagRepository.findAll());
	}

	@Override
	public List<TweetResponseDto> getTweetsByHashtag(String label) {
		Optional<Hashtag> optionalTag = hashtagRepository.findByLabel(label);
		if (optionalTag.isEmpty()) {
			throw new NotFoundException("The hashtag " + label + " was not found.");
		}
		Hashtag tag = optionalTag.get();
		List<Tweet> tweetsList = tag.getTweets();
		tweetsList = tweetsList.stream().filter(t -> !t.isDeleted()).collect(Collectors.toList());
		Collections.sort(tweetsList, Comparator.comparing(Tweet::getPosted).reversed());
		return tweetMapper.entitiesToResponseDtos(tweetsList);
	}

}
