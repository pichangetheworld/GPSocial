package com.gpsocial.data;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class FeedData {
	public enum Source {
		FACEBOOK,
		TWITTER
	};
	
	public Source feed_source;  // FB, Twitter, G+
	public String author;	// user	
	public String message;	// text
	public long created_at;	// timestamp
	public String profile_pic_url;	// url to get profile pic (?)
	
	public FeedData() {}
	
	public FeedData(TwitterData data) {
		feed_source = Source.TWITTER;
		author = data.user.screen_name;
		message = data.text;
		try {
			created_at = new SimpleDateFormat("EEE MMM dd HH:mm:ss", Locale.CANADA)
				.parse(data.created_at).getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		profile_pic_url = data.user.profile_image_url;
	}
};
