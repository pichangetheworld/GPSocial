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
	public String profile_img_url;	// url to get profile pic (?)
	// public double longitude;
	// public double latitude;
	
	public FeedData() {}
	
	public FeedData(TwitterData data) {
		feed_source = Source.TWITTER;
		author = data.user.screen_name;
		message = data.text;
		try {
			created_at = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", Locale.CANADA)
				.parse(data.created_at).getTime(); // Twitter uses PST-DST
		} catch (ParseException e) {
			e.printStackTrace();
		}
		profile_img_url = data.user.profile_image_url;
	}
};
