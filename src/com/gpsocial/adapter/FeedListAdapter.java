package com.gpsocial.adapter;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.gpsocial.R;
import com.gpsocial.data.FeedData;

public class FeedListAdapter extends ArrayAdapter<FeedData> {
	private Context context;
    private int layoutResourceId;   
    private List<FeedData> data = null;
    
    //TODO
    //MemoryCache memoryCache=new MemoryCache();

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
            holder.profilePicture = (ImageView)row.findViewById(R.id.profile_picture);
            holder.author = (TextView)row.findViewById(R.id.author);
            holder.message = (TextView)row.findViewById(R.id.message);
            holder.createdAt = (TextView)row.findViewById(R.id.createdAt);

            getProfileImg(holder.profilePicture, post.profile_img_url);
            
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
        
        return row;
	}
	
	public void setListData(List<FeedData> data) {
		this.data = data;
	}
	
	private class ProfilePictureParams {
		String url;
		ImageView dest;
	}

	private class UpdateProfilePictureAsyncTask extends AsyncTask<ProfilePictureParams, Void, Bitmap> {
		private ImageView view;
		
		@Override
		protected Bitmap doInBackground(ProfilePictureParams... params) {
			Bitmap bmp = null;
			view = params[0].dest;
			try {
				URL url = new URL(params[0].url);
				HttpURLConnection connection = (HttpURLConnection) url.openConnection();
				bmp = BitmapFactory.decodeStream(connection.getInputStream());
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return bmp;
		}
		
		@Override
        protected void onPostExecute(Bitmap result) {
			if (result != null) {
		        view.setImageBitmap(result);
			} else {
				view.setImageResource(R.drawable.default_avatar);
			}
			notifyDataSetChanged();
        }

	}
	private void getProfileImg(final ImageView avatar, final String img_url) {
		ProfilePictureParams params = new ProfilePictureParams();
		params.url = img_url;
		params.dest = avatar;
		new UpdateProfilePictureAsyncTask().execute(params);
	}
}
