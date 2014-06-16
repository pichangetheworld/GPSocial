package com.gpsocial.data;

public class FeedData {
	public enum Source {
		FACEBOOK,
		TWITTER
	};
	
	public Source feed_source;  // FB, Twitter, G+
	public String author;	// user	
	public String message;	// text
	public long created_at;	// timestamp
};
