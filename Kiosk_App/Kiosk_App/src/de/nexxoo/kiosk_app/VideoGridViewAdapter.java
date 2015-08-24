package de.nexxoo.kiosk_app;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import de.nexxoo.kiosk_app.entity.Video;
import de.nexxoo.kiosk_app.tools.Misc;
import de.nexxoo.kiosk_app.tools.Nexxoo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by b.yuan on 17.08.2015.
 */
public class VideoGridViewAdapter extends ArrayAdapter<Video> {

	private LayoutInflater mInflater;
	private Context mContext;
	private List<Video> mvideoList;
	private int mLayoutId;


	public VideoGridViewAdapter(Context context, int layoutId, List<Video> objects) {
		super(context, layoutId, objects);
		mInflater = LayoutInflater.from(context);
		mContext = context;
		mvideoList = new ArrayList<Video>(objects);
		mLayoutId = layoutId;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final int currentPosition = position;
		Item item;
		if (convertView == null) {
			convertView = mInflater.inflate(mLayoutId, parent, false);
			convertView.setBackgroundColor(mContext.getResources().getColor(
					R.color.RealWhite));

			Log.e(Nexxoo.TAG, "GridViewAdapter Position: " + position);
			item = new Item();
			item.videoName = (TextView) convertView
					.findViewById(R.id.video_gridview_item_name);
			item.videoName.setTypeface(Misc.getCustomFont(mContext, Misc.FONT_NORMAL));
			item.videoSize = (TextView) convertView.findViewById(R.id.video_gridview_item_size);
			item.videoSize.setTypeface(Misc.getCustomFont(mContext, Misc.FONT_NORMAL));
			item.videoCover = (ImageView) convertView.findViewById(R.id.video_gridview_item_cover);
			item.play_button = (ImageView) convertView.findViewById(R.id
					.video_gridview_item_play_button);

			convertView.setTag(item);
		} else {
			item = (Item) convertView.getTag();
		}

		item.videoName.setText(mvideoList.get(position).getName() + position);
		/**
		 * get device actual size
		 */
		/*DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
		int width = metrics.widthPixels;
		int height = metrics.heightPixels;*/

		item.play_button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String url = mvideoList.get(currentPosition).getUrl();
				Log.e(Nexxoo.TAG, "Video URL : " + url);
				url.replace("www", "nexxoo:wenexxoo4kiosk!@www");
				Intent i = new Intent(mContext, VideoActivity.class);
				i.putExtra(mContext.getString(R.string
						.video_activity_intent_url_extra), url);
				mContext.startActivity(i);

			}
		});

		ImageLoader imageLoader = ImageLoader.getInstance(); // Get singleton instance
		imageLoader.displayImage(mvideoList.get(position).getmPictureList().get
						(0).getmUrl(),
				item.videoCover, new ImageLoadingListener() {

					@Override
					public void onLoadingStarted(String imageUri, View view) {
					}

					@Override
					public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
						ImageView mImageView = (ImageView) view;
						mImageView.setImageResource(R.drawable.video_cover_small);
					}

					@Override
					public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
//						Log.e(Nexxoo.TAG, "Image loading completes: " + imageUri);
					}

					@Override
					public void onLoadingCancelled(String imageUri, View view) {

					}
				});

		return convertView;
	}

	@Override
	public int getCount() {
//		Log.e(Nexxoo.TAG,"GridviewAdapter get count:" +mvideoList.size());
		return mvideoList.size();
	}

	@Override
	public Video getItem(int position) {
		return mvideoList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	private static class Item {
		TextView videoName;
		TextView videoSize;
		ImageView videoCover;
		ImageView play_button;
	}

}