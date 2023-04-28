package com.cooksys.assessment1Team3.services.impl;

import com.cooksys.assessment1Team3.dtos.CredentialsDto;
import com.cooksys.assessment1Team3.dtos.TweetResponseDto;
import com.cooksys.assessment1Team3.dtos.UserRequestDto;
import com.cooksys.assessment1Team3.dtos.UserResponseDto;
import com.cooksys.assessment1Team3.entities.Profile;
import com.cooksys.assessment1Team3.entities.Tweet;
import com.cooksys.assessment1Team3.entities.User;
import com.cooksys.assessment1Team3.exceptions.BadRequestException;
import com.cooksys.assessment1Team3.exceptions.NotAuthorizedException;
import com.cooksys.assessment1Team3.exceptions.NotFoundException;
import com.cooksys.assessment1Team3.exceptions.UserAlreadyExistException;
import com.cooksys.assessment1Team3.mappers.ProfileMapper;
import com.cooksys.assessment1Team3.mappers.TweetMapper;
import com.cooksys.assessment1Team3.mappers.UserMapper;
import com.cooksys.assessment1Team3.repositories.TweetRepository;
import com.cooksys.assessment1Team3.repositories.UserRepository;
import com.cooksys.assessment1Team3.services.TweetService;
import com.cooksys.assessment1Team3.services.UserService;
import com.cooksys.assessment1Team3.services.ValidateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;
	private final UserMapper userMapper;
	private final TweetMapper tweetMapper;
	private final TweetService tweetService;
	private final ValidateService validateService;
	private final ProfileMapper profileMapper;
	private final TweetRepository tweetRepository;

	private void validateUserRequest(UserRequestDto userRequestDto) {
		if (userRequestDto.getCredentials() == null ||
				userRequestDto.getProfile() == null ||
				userRequestDto.getCredentials().getUsername() == null ||
				userRequestDto.getCredentials().getPassword() == null ||
				userRequestDto.getProfile().getFirstName() == null ||
				userRequestDto.getProfile().getLastName() == null ||
				userRequestDto.getProfile().getEmail() == null ||
				userRequestDto.getProfile().getPhone() == null) {
			throw new BadRequestException("You must provide all the required fields for the request.");
		}
	}

	@Override
	public User getUser(String username) {
		Optional<User> optionalUser = userRepository.findByCredentialsUsernameAndDeletedFalse(username);

		if (optionalUser.isEmpty()) {
			throw new NotFoundException("User with username of " + username + " does not exist in our database.");
		}
		return optionalUser.get();
	}

	@Override
	public UserResponseDto getUserByUsername(String username) {
		return userMapper.entityToDto(getUser(username));
	}

	@Override
	public List<UserResponseDto> getUsers() {
		return userMapper.entitiesToDtos(userRepository.findAllByDeletedFalse());
	}

	@Override
	public List<TweetResponseDto> getUserFeed(String username) {
		User user = getUser(username);
		if (!validateService.validateUserExists(username) || user.isDeleted()) {
			throw new NotFoundException("There is no active user with name " + username);
		}

		List<Tweet> tweets = tweetRepository.findByAuthorAndDeletedFalse(user);

		for (User follow : user.getFollowing()) {
			tweets.addAll(tweetRepository.findByAuthorAndDeletedFalse(follow));
		}

		Collections.sort(tweets, Comparator.comparing(Tweet::getPosted).reversed());

		return tweetMapper.entitiesToResponseDtos(tweets);
	}

	@Override
	public List<UserResponseDto> getUserFollowing(String username) {
		User user = getUser(username);

		List<User> activeUserFollowing = user.getFollowing().stream().filter(user1 -> !user1.isDeleted())
				.collect(Collectors.toList());


		return userMapper.entitiesToDtos(activeUserFollowing);
	}

	@Override
	public List<UserResponseDto> getUserFollowers(String username) {
		User user = getUser(username);

		List<User> activeUserFollowers = user.getFollowers().stream().filter(user1 -> !user1.isDeleted())
				.collect(Collectors.toList());

		return userMapper.entitiesToDtos(activeUserFollowers);
	}

	@Override
	public List<TweetResponseDto> getMentions(String username) {

		User user = getUser(username);

		List<Tweet> tweets =
				tweetRepository
						.findByContentContainingAndDeletedFalse("@" + user.getCredentials().getUsername()).stream()
						.filter(tweet -> Objects.nonNull(tweet.getContent()))
						.sorted(Comparator.comparing(Tweet::getPosted).reversed())
						.collect(Collectors.toList());

		return tweetMapper.entitiesToResponseDtos(tweets);
	}

	@Override
	public UserResponseDto createUser(UserRequestDto userRequest) {
		validateUserRequest(userRequest);
		String username = userRequest.getCredentials().getUsername();
		Optional<User> user = userRepository.findByCredentialsUsername(username);
		if (user.isPresent()) {
			if (user.get().isDeleted()
					&& user.get().getCredentials().getPassword().equals(userRequest.getCredentials().getPassword())) {
				user.get().setDeleted(false);
				return userMapper.entityToDto(userRepository.saveAndFlush(user.get()));
			} else {
				throw new UserAlreadyExistException("Username: " + username + " is already taken!");
			}
		} else {
			return userMapper.entityToDto(userRepository.saveAndFlush(userMapper.requestDtoToEntity(userRequest)));
		}
	}

	@Override
	public UserResponseDto modifyUser(String username, UserRequestDto body) {

		if (body.getCredentials() == null ||
				body.getProfile() == null ||
				body.getCredentials().getUsername() == null ||
				body.getCredentials().getPassword() == null) {
			throw new BadRequestException("You must provide all the required fields for the request.");
		}

		Optional<User> optionalUser = userRepository.findByCredentialsUsername(body.getCredentials().getUsername());

		if (optionalUser.isEmpty()
				|| !optionalUser.get().getCredentials().getPassword().equals(body.getCredentials().getPassword())) {
			throw new NotAuthorizedException("Incorrect username or password.");
		}

		User user = getUser(username);
		// validate credential
		Profile mappedUserProfile = profileMapper.dtoToEntity(body.getProfile());
		if (mappedUserProfile.getEmail() != null) {
			user.getProfile().setEmail(mappedUserProfile.getEmail());
		}
		if (mappedUserProfile.getFirstName() != null) {
			user.getProfile().setFirstName(mappedUserProfile.getFirstName());
		}
		if (mappedUserProfile.getLastName() != null) {
			user.getProfile().setLastName(mappedUserProfile.getLastName());
		}
		if (mappedUserProfile.getPhone() != null) {
			user.getProfile().setPhone(mappedUserProfile.getPhone());
		}

		return userMapper.entityToDto(userRepository.saveAndFlush(user));
	}

	@Override
	public UserResponseDto deleteUser(String username) {
		if (!validateService.validateUserExists(username)) {
			throw new NotFoundException("User with username of " + username + " not found.");
		}
		User deletedUser = getUser(username);
		deletedUser.setDeleted(true);
		return userMapper.entityToDto(userRepository.saveAndFlush(deletedUser));
	}

	@Override
	public List<UserResponseDto> getMentions(Long id) {
		Tweet tweet = tweetService.getTweet(id);
		List<User> mentionedUsers = tweet.getMentionedUsers();
		mentionedUsers = mentionedUsers.stream().filter(u -> !u.isDeleted()).collect(Collectors.toList());
		return userMapper.entitiesToDtos(mentionedUsers);
	}

	@Override
	public void followUser(String username, CredentialsDto credentialsDto) {
		if (!validateService.validateUserExists(username)) {
			throw new NotFoundException("User with username of " + username + " not found.");
		}

		Optional<User> optionalUser = userRepository.findByCredentialsUsername(credentialsDto.getUsername());
		if (optionalUser.isEmpty()
				|| !optionalUser.get().getCredentials().getPassword().equals(credentialsDto.getPassword())) {
			throw new NotAuthorizedException("Incorrect username or password.");
		}

		User followed = getUser(username);
		User follower = optionalUser.get();

		List<User> followingList = follower.getFollowing();
		if (followingList.contains(followed)) {
			throw new BadRequestException("You are already following that user.");
		}
		followingList.add(followed);
		follower.setFollowing(followingList);
		userRepository.saveAndFlush(follower);

		List<User> followedList = followed.getFollowers();
		followedList.add(follower);
		followed.setFollowers(followedList);
		userRepository.saveAndFlush(followed);
	}

	@Override
	public void unfollowUser(String username, CredentialsDto credentials) {
		if (!validateService.validateUserExists(username)) {
			throw new NotFoundException("User with username of " + username + " not found.");
		}

		Optional<User> optionalUser = userRepository.findByCredentialsUsername(credentials.getUsername());
		if (optionalUser.isEmpty() || !optionalUser.get().getCredentials().getPassword().equals(credentials.getPassword())) {
			throw new NotAuthorizedException("Incorrect username or password.");
		}

		User followed = getUser(username);
		User follower = optionalUser.get();
		List<User> followingList = follower.getFollowing();

		if (!followingList.contains(followed)) {
			throw new BadRequestException("You can't unfollow user you don't subscribe!");
		}

		followingList.remove(followed);
		follower.setFollowing(followingList);
		userRepository.saveAndFlush(follower);

		List<User> followedList = followed.getFollowers();
		followedList.remove(follower);
		followed.setFollowers(followedList);
		userRepository.saveAndFlush(followed);

	}

}
