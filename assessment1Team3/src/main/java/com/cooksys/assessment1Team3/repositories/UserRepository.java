package com.cooksys.assessment1Team3.repositories;

import com.cooksys.assessment1Team3.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

	List<User> findAllByDeletedFalse();

	Optional<User> findByCredentialsUsernameAndDeletedFalse(String username);

	Optional<User> findByCredentialsUsername(String username);
}

