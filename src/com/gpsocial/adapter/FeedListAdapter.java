package com.gpsocial.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.gpsocial.R;
import com.gpsocial.data.FeedData;

public class FeedListAdapter extends ArrayAdapter<FeedData> {
	private static Context context;
    private static int layoutResourceId;   
    private static FeedData data[] = null;

	public FeedListAdapter(Context context,
			int layoutResourceId, FeedData[] objects) {
		super(context, layoutResourceId, objects);
		this.context = context;
		this.layoutResourceId = layoutResourceId;
		this.data = objects;
	}
   
    static class FeedObjectHolder
    {
        ImageView profilePicture;
        TextView username;
        TextView message;
    }

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
        FeedObjectHolder holder = null;
       
        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
           
            holder = new FeedObjectHolder();
            holder.profilePicture = (ImageView)row.findViewById(R.id.profile_picture);
            holder.username = (TextView)row.findViewById(R.id.author);
            holder.message = (TextView)row.findViewById(R.id.feed_content);
           
            row.setTag(holder);
        }
        else
        {
            holder = (FeedObjectHolder)row.getTag();
        }
       
        FeedData post = data[position];
        holder.profilePicture.setImageResource(R.drawable.default_avatar);
        holder.username.setText(post.author);
       
        return row;
	}
}
