package com.gpsocial.adapter;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.gpsocial.R;
import com.gpsocial.data.FeedData;
import com.nostra13.universalimageloader.core.ImageLoader;

public class FeedListAdapter extends ArrayAdapter<FeedData> {
	private Context context;
    private int layoutResourceId;   
    private List<FeedData> data = null;
    
    
	public FeedListAdapter(Context context,
			int layoutResourceId, List<FeedData> data) {
		super(context, layoutResourceId, data);
		this.context = context;
		this.layoutResourceId = layoutResourceId;
		this.data = data;
	}
   
    static class FeedObjectHolder
    {
        ImageView profilePicture;
        TextView author;
        TextView message;
        TextView createdAt;
        TextView distance;
    }
    
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
        FeedObjectHolder holder = null;

        FeedData post = data.get(position);
        
        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
           
            holder = new FeedObjectHolder();
            holder.profilePicture = (ImageView) row.findViewById(R.id.profile_picture);
            holder.author = (TextView) row.findViewById(R.id.author);
            holder.message = (TextView) row.findViewById(R.id.message);
            holder.createdAt = (TextView) row.findViewById(R.id.createdAt);
            holder.distance = (TextView) row.findViewById(R.id.distance);

            row.setTag(holder);
        }
        else
        {
            holder = (FeedObjectHolder)row.getTag();
        }
        
        holder.author.setText(post.author);
        holder.message.setText(post.message);
        if (System.currentTimeMillis() - post.created_at < 1000 * 60 * 60 * 24) // one day
        	holder.createdAt.setText(DateUtils.getRelativeTimeSpanString(post.created_at));
        else
        	holder.createdAt.setText(
        			new SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy", Locale.CANADA)
        			.format(post.created_at));
        
        ImageLoader.getInstance().displayImage(post.profile_img_url, holder.profilePicture);
        if (post.distance == 0) {
        	holder.distance.setVisibility(View.GONE);
        }

		notifyDataSetChanged();
        
        return row;
	}
	
	public void setListData(List<FeedData> data) {
		this.data = data;
	}
}
