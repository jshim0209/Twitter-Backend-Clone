package com.cooksys.assessment1Team3.repositories;

import com.cooksys.assessment1Team3.entities.Tweet;
import com.cooksys.assessment1Team3.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TweetRepository extends JpaRepository<Tweet, Long> {
    List<Tweet> findAllByDeletedFalse();

    Optional<Tweet> findByIdAndDeletedFalse(Long id);

    List<Tweet> findByContentContainingAndDeletedFalse(String string);

    List<Tweet> findByAuthorAndDeletedFalse(User user);
}
