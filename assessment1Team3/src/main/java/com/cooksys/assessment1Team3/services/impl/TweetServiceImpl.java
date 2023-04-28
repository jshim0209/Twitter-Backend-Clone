package com.cooksys.assessment1Team3.services.impl;

import com.cooksys.assessment1Team3.dtos.*;

import com.cooksys.assessment1Team3.entities.Credentials;
import com.cooksys.assessment1Team3.entities.Hashtag;
import com.cooksys.assessment1Team3.entities.Tweet;
import com.cooksys.assessment1Team3.entities.User;
import com.cooksys.assessment1Team3.exceptions.BadRequestException;
import com.cooksys.assessment1Team3.exceptions.NotAuthorizedException;
import com.cooksys.assessment1Team3.exceptions.NotFoundException;
import com.cooksys.assessment1Team3.mappers.HashtagMapper;
import com.cooksys.assessment1Team3.mappers.TweetMapper;
import com.cooksys.assessment1Team3.mappers.UserMapper;
import com.cooksys.assessment1Team3.repositories.HashtagRepository;
import com.cooksys.assessment1Team3.repositories.TweetRepository;
import com.cooksys.assessment1Team3.repositories.UserRepository;
import com.cooksys.assessment1Team3.services.TweetService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TweetServiceImpl implements TweetService {

	private final TweetRepository tweetRepository;
	private final TweetMapper tweetMapper;
	private final UserRepository userRepository;
	private final UserMapper userMapper;
	private final HashtagMapper hashtagMapper;
	private final HashtagRepository hashtagRepository;

	private void validateTweetRequest(TweetRequestDto tweetRequestDto) {
		if (tweetRequestDto.getCredentials() == null || tweetRequestDto.getCredentials().getUsername() == null
				|| tweetRequestDto.getCredentials().getUsername().isEmpty()
				|| tweetRequestDto.getCredentials().getPassword() == null
				|| tweetRequestDto.getCredentials().getPassword().isEmpty() || tweetRequestDto.getContent() == null
				|| tweetRequestDto.getContent().isEmpty()) {
			throw new BadRequestException("You must provide all of the required field to process this request!");
		}
	}

	@Override
	public List<TweetResponseDto> getAllTweets() {

		List<Tweet> tweets = tweetRepository.findAllByDeletedFalse().stream()
				.sorted(Comparator.comparing(Tweet::getPosted).reversed()).collect(Collectors.toList());

		return tweetMapper.entitiesToResponseDtos(tweets);
	}

	@Override
	public TweetResponseDto createTweet(TweetRequestDto tweetRequestDto) {
		validateTweetRequest(tweetRequestDto);
		String username = tweetRequestDto.getCredentials().getUsername();
		Optional<User> optionalUser = userRepository.findByCredentialsUsernameAndDeletedFalse(username);
		if (optionalUser.isEmpty()) {
			throw new NotFoundException("User: " + username + " doesn't not exist or inactive.");
		}
		Tweet tweetToSave = new Tweet();
		tweetToSave.setAuthor(optionalUser.get());
		tweetToSave.setContent(tweetRequestDto.getContent());

		String regexPatternHashtag = "(#\\w+)";

		Pattern p = Pattern.compile(regexPatternHashtag);
		Matcher m = p.matcher(tweetToSave.getContent());

		List<Hashtag> hashtags = new ArrayList<>();

		while (m.find()) {
			Hashtag hashtag = new Hashtag();
			String string = m.group(1);
			string = string.replace("#", "");

			Optional<Hashtag> optionalHashtag = hashtagRepository.findByLabel(string);

			if (optionalHashtag.isEmpty()) {
				hashtag.setLabel(string);

				hashtagRepository.saveAndFlush(hashtag);

			} else {
				hashtag = optionalHashtag.get();
				hashtag.setLastUsed(Timestamp.valueOf(LocalDateTime.now()));

				hashtagRepository.saveAndFlush(hashtag);
			}

			hashtags.add(hashtag);
		}

		tweetToSave.setHashtags(hashtags);

		String regexPatternMention = "(@\\w+)";

		Pattern p2 = Pattern.compile(regexPatternMention);
		Matcher m2 = p2.matcher(tweetToSave.getContent());

		List<User> mentionedUsers = new ArrayList<>();
		while (m2.find()) {
			User mentionedUser = new User();
			String string = m2.group(1);
			string = string.replace("@", "");

			Optional<User> optionalUser2 = userRepository.findByCredentialsUsername(string);

			if (optionalUser2.isPresent()) {
				mentionedUser = optionalUser2.get();
				mentionedUsers.add(mentionedUser);
			}

		}
		tweetToSave.setMentionedUsers(mentionedUsers);

		return tweetMapper.entityToDto(tweetRepository.saveAndFlush(tweetToSave));
	}

	@Override
	public TweetResponseDto repostTweet(Long id, CredentialsDto credentialsDto) {
		Tweet tweet = new Tweet();
		Tweet usedTweet = tweetRepository.findById(id).get();
		if (usedTweet.isDeleted() || usedTweet == null) {
			throw new NotFoundException("We can't find a tweet with the id of " + id + " in our database.");
		}
		Optional<User> optionalUser = userRepository
				.findByCredentialsUsernameAndDeletedFalse(credentialsDto.getUsername());
		if (optionalUser.isEmpty()) {
			throw new BadRequestException("No user by that name.");
		}
		User user = optionalUser.get();
		if (!credentialsDto.getUsername().equals(user.getCredentials().getUsername())
				|| !credentialsDto.getPassword().equals(user.getCredentials().getPassword())) {
			throw new NotAuthorizedException("Incorrect username or password.");
		}
		tweet.setAuthor(user);
		tweet.setPosted(new Timestamp(System.currentTimeMillis()));
		tweet.setRepostOf(usedTweet);
		tweetRepository.saveAndFlush(tweet);
		return tweetMapper.entityToDto(tweet);
	}

	@Override
	public Tweet getTweet(Long id) {
		Optional<Tweet> optionalTweet = tweetRepository.findByIdAndDeletedFalse(id);

		if (optionalTweet.isEmpty()) {
			throw new NotFoundException("We can't find a tweet with the id of " + id + " in our database.");
		}
		return optionalTweet.get();
	}

	@Override
	public TweetResponseDto getTweetById(Long id) {

		return tweetMapper.entityToDto(getTweet(id));
	}

	@Override
	public void addLikeToTweet(Long id, CredentialsDto credentials) {
		Tweet tweetToBeLiked = getTweet(id);

		String username = credentials.getUsername();
		Optional<User> optionalUser = userRepository.findByCredentialsUsernameAndDeletedFalse(username);

		if (optionalUser.isEmpty()) {
			throw new NotFoundException("User with username of " + username + " was not found in our database.");
		}
		User user = optionalUser.get();

		if (!credentials.getUsername().equals(user.getCredentials().getUsername())
				|| !credentials.getPassword().equals(user.getCredentials().getPassword())) {
			throw new NotAuthorizedException("Incorrect username or password.");
		}

		List<User> likes = tweetToBeLiked.getLikes();
		if (!likes.contains(user)) {
			likes.add(user);
			user.getLikedTweets().add(tweetToBeLiked);
			tweetToBeLiked.setLikes(likes);

			tweetRepository.saveAndFlush(tweetToBeLiked);
			userRepository.saveAndFlush(user);
		}
	}

	@Override
	public List<UserResponseDto> getTweetLikesByTweetId(Long id) {
		Tweet tweet = getTweet(id);
		if (tweet.isDeleted() || tweet == null) {
			throw new NotFoundException("Tweet with id " + id + " was not found in our database.");
		}
		return userMapper.entitiesToDtos(tweet.getLikes());
	}

	@Override
	public List<HashtagDto> getTweetTagsByTweetId(Long id) {
		Tweet tweet = getTweet(id);
		if (tweet.isDeleted() || tweet == null) {
			throw new NotFoundException("Tweet with id " + id + " was not found in our database.");
		}

		return hashtagMapper.entitiesToDtos(tweet.getHashtags());
	}

	private List<Tweet> getRepliesRecursively(Tweet tweet) {
		List<Tweet> replies = tweet.getReplies();
		List<Tweet> results = new ArrayList<>();
		if (replies.isEmpty()) {
			return null;
		}
		replies.forEach(t -> {
			List<Tweet> temp = getRepliesRecursively(t);
			if (temp != null) {
				results.addAll(temp);
			}
		});
		return results;
	}
	@Override
	public ContextDto getTweetContextByTweetId(Long id) {

		Tweet tweet = getTweet(id);
		if (tweet.isDeleted() || tweet == null) {
			throw new NotFoundException("Tweet with id " + id + " was not found in our database.");
		}
		
		List<Tweet> afterList = new ArrayList<>();
		List<Tweet> currentReplies = tweet.getReplies();
		for (Tweet t : currentReplies) {
			List<Tweet> temp = getRepliesRecursively(t);
			if (temp != null) {
				afterList.addAll(temp);
			}
		}
		
		List<Tweet> activeAfterList = afterList.stream()
				.filter(tweet1 -> ! tweet1.isDeleted())
				.sorted(Comparator.comparing(Tweet::getPosted).reversed())
				.collect(Collectors.toList());	


		List<Tweet> beforeList = new ArrayList<>();

		Tweet currentTweet = tweet.getInReplyTo();
		while (currentTweet != null) {
			beforeList.add(currentTweet);
			currentTweet = currentTweet.getInReplyTo();
		}

		List<Tweet> activeBeforeList = beforeList.stream()
				.filter(tweet1 -> ! tweet1.isDeleted())
				.sorted(Comparator.comparing(Tweet::getPosted).reversed())
				.collect(Collectors.toList());
		
		ContextDto contextDto = new ContextDto();
		contextDto.setTarget(tweetMapper.entityToDto(tweet));
		contextDto.setAfter(tweetMapper.entitiesToResponseDtos(activeAfterList));
		contextDto.setBefore(tweetMapper.entitiesToResponseDtos(activeBeforeList));

		return contextDto;
	}

	public TweetResponseDto deleteTweet(Long id, CredentialsDto credentialsDto) {
		Tweet tweet = getTweet(id);
		Credentials authorCredentials = tweet.getAuthor().getCredentials();
		if (!authorCredentials.getUsername().equals(credentialsDto.getUsername())
				|| !authorCredentials.getPassword().equals(credentialsDto.getPassword())) {
			throw new NotAuthorizedException("You do not have proper credentials to delete this tweet.");
		}

		if (tweet.isDeleted() || tweet == null) {
			throw new NotFoundException("Tweet with id " + id + " was not found in our database.");
		}
		tweet.setDeleted(true);

		return tweetMapper.entityToDto(tweetRepository.saveAndFlush(tweet));
	}

	public List<TweetResponseDto> getUserTweets(String username) {
		Optional<User> optionalUser = userRepository.findByCredentialsUsername(username);
		if (optionalUser.isEmpty()) {
			throw new NotFoundException("User with username of " + username + " was not found in our database.");
		} else {
			User user = optionalUser.get();
			List<Tweet> userTweets = user.getTweets();
			userTweets = userTweets.stream().filter(t -> !t.isDeleted()).collect(Collectors.toList());
			Collections.sort(userTweets, Comparator.comparing(Tweet::getPosted).reversed());
			return tweetMapper.entitiesToResponseDtos(tweetRepository.saveAllAndFlush(userTweets));
		}
	}

	@Override
	public TweetResponseDto createReplyToTweet(Long id, TweetRequestDto tweetRequestDto) {
		Tweet originalTweet = getTweet(id);

		CredentialsDto credentialsDto = tweetRequestDto.getCredentials();
		Optional<User> optionalUser = userRepository.findByCredentialsUsername(credentialsDto.getUsername());
		if (optionalUser.isEmpty()
				|| !optionalUser.get().getCredentials().getPassword().equals(credentialsDto.getPassword())) {
			throw new NotAuthorizedException("Incorrect username or password.");
		}

		String content = tweetRequestDto.getContent();
		if (content.isBlank()) {
			throw new BadRequestException("Reply tweets must contain content.");
		}

		User author = optionalUser.get();
		Tweet replyTweet = new Tweet();
		replyTweet.setAuthor(author);
		replyTweet.setInReplyTo(originalTweet);
		replyTweet.setContent(content);

		return tweetMapper.entityToDto(tweetRepository.saveAndFlush(replyTweet));
	}

	@Override
	public List<TweetResponseDto> getRepliesToTweet(Long id) {
		Tweet originalTweet = getTweet(id);
		List<Tweet> replies = originalTweet.getReplies().stream().filter(t -> !t.isDeleted())
				.sorted(Comparator.comparing(Tweet::getPosted).reversed()).collect(Collectors.toList());

		return tweetMapper.entitiesToResponseDtos(replies);
	}

	@Override
	public List<TweetResponseDto> getRepostsOfTweet(Long id) {
		Tweet originalTweet = getTweet(id);
		List<Tweet> reposts = originalTweet.getReposts().stream().filter(t -> !t.isDeleted())
				.sorted(Comparator.comparing(Tweet::getPosted).reversed()).collect(Collectors.toList());
		return tweetMapper.entitiesToResponseDtos(reposts);
	}
}
