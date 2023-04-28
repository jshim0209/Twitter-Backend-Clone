package com.cooksys.assessment1Team3.services;

import java.util.List;

import com.cooksys.assessment1Team3.dtos.CredentialsDto;
import com.cooksys.assessment1Team3.dtos.TweetResponseDto;
import com.cooksys.assessment1Team3.dtos.UserRequestDto;
import com.cooksys.assessment1Team3.dtos.UserResponseDto;
import com.cooksys.assessment1Team3.entities.User;

public interface UserService {

	User getUser(String username);

	UserResponseDto getUserByUsername(String username);

	UserResponseDto modifyUser(String username, UserRequestDto body);

	UserResponseDto deleteUser(String username);

	List<UserResponseDto> getUsers();

	List<TweetResponseDto> getUserFeed(String username);

	List<UserResponseDto> getUserFollowing(String username);

	List<UserResponseDto> getUserFollowers(String username);

	List<TweetResponseDto> getMentions(String username);

	UserResponseDto createUser(UserRequestDto userRequest);

	List<UserResponseDto> getMentions(Long id);

	void followUser(String username, CredentialsDto credentialsDto);

	void unfollowUser(String username, CredentialsDto credentials);

}
