package com.example.twitter;

import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.twitter.ITweet;
import com.example.twitter.TwitterClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TwitterClientTest {

	@Mock
    private ITweet tweet;

	@Test
	void ensureThatTwitterClientCallsGetMessageOnTweet() {
		TwitterClient twitterClient = new TwitterClient();

		when(tweet.getMessage()).thenReturn("Using mockito is great");

		twitterClient.sendTweet(tweet);
		verify(tweet, atLeastOnce()).getMessage();

	}

}