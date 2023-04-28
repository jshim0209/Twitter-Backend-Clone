package com.cooksys.assessment1Team3.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;
import java.util.List;

@Entity
@NoArgsConstructor
@Data
public class Tweet {

    @Id
    @GeneratedValue
    // Changed to Long data type 
    private Long id;

    @ManyToOne
    private User author;

    @CreationTimestamp
    private Timestamp posted;

    //Set default value of deleted to false
    private boolean deleted = false;

    private String content;

    //Removed Join
    @ManyToOne
    private Tweet inReplyTo;

    //Added relational mapping between singular Tweet and replies to it.
    @OneToMany(mappedBy="inReplyTo")
    private List<Tweet> replies;

    @ManyToOne
    private Tweet repostOf;

    //Added relational mapping between singular Tweet and reposts of it.
    @OneToMany(mappedBy="repostOf")
    private List<Tweet> reposts;

    //Added explicit joins for Tweets and Hashtags
    @ManyToMany(cascade=CascadeType.MERGE)
    @JoinTable(
            name="tweet_hashtags",
            joinColumns=@JoinColumn(name="tweet_id"),
            inverseJoinColumns=@JoinColumn(name="hashtag_id")
    )
    private List<Hashtag> hashtags;

    @ManyToMany(mappedBy="likedTweets")
    private List<User> likes;

    @ManyToMany
    @JoinTable(
            name="user_mentions",
            joinColumns=@JoinColumn(name="tweet_id"),
            inverseJoinColumns=@JoinColumn(name="user_id")
    )
    private List<User> mentionedUsers;

}
