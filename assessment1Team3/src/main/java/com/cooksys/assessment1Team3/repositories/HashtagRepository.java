package com.cooksys.assessment1Team3.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cooksys.assessment1Team3.entities.Hashtag;

@Repository
public interface HashtagRepository extends JpaRepository<Hashtag, Long> {

    Optional<Hashtag> findByLabel(String Label);

    Optional<Hashtag> findHashtagByLabelContaining(String s);
}
