package de.nexxoo.kiosk_app;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import de.nexxoo.kiosk_app.db.DatabaseHandler;
import de.nexxoo.kiosk_app.entity.Video;
import de.nexxoo.kiosk_app.tools.FileStorageHelper;
import de.nexxoo.kiosk_app.tools.Global;
import de.nexxoo.kiosk_app.tools.Misc;
import de.nexxoo.kiosk_app.tools.Nexxoo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by b.yuan on 17.08.2015.
 */
public class VideoGridViewAdapter extends ArrayAdapter<Video> {

	private LayoutInflater mInflater;
	private Context mContext;
	private List<Video> mVideoList;
	private int mLayoutId;
	private Boolean isVideoDownloaded;
	private FileStorageHelper fileHelper;
	private DatabaseHandler dbHandler;


	public VideoGridViewAdapter(Context context, int layoutId, List<Video> objects) {
		super(context, layoutId, objects);
		mInflater = LayoutInflater.from(context);
		mContext = context;
		mVideoList = new ArrayList<Video>(objects);
		mLayoutId = layoutId;
		fileHelper = new FileStorageHelper(context);
		dbHandler = new DatabaseHandler(context);
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		isVideoDownloaded = fileHelper.isContentDownloaded(mVideoList.get(position)
				.getFileName());
		Item item;
		if (convertView == null) {
			convertView = mInflater.inflate(mLayoutId, parent, false);
			convertView.setBackgroundColor(mContext.getResources().getColor(
					R.color.RealWhite));

			item = new Item();
			item.videoName = (TextView) convertView
					.findViewById(R.id.video_gridview_item_name);
			item.videoName.setTypeface(Misc.getCustomFont(mContext, Misc.FONT_NORMAL));
			item.videoSize = (TextView) convertView.findViewById(R.id.video_gridview_item_size);
			item.videoSize.setTypeface(Misc.getCustomFont(mContext, Misc.FONT_NORMAL));
			item.videoCover = (ImageView) convertView.findViewById(R.id.video_gridview_item_cover);
			item.play_button = (ImageView) convertView.findViewById(R.id
					.video_gridview_item_play_button);
			item.download_button = (ImageView) convertView.findViewById(R.id
					.video_gridview_item_download_button);

			convertView.setTag(item);
		} else {
			item = (Item) convertView.getTag();
		}

		item.videoName.setText(mVideoList.get(position).getName());
		/**
		 * get device actual size
		 */
		/*DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
		int width = metrics.widthPixels;
		int height = metrics.heightPixels;*/

		item.download_button.
				setImageResource(isVideoDownloaded ? R.drawable
						.ic_grid_trash : R.drawable.ic_grid_download);
		item.play_button.
				setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						Nexxoo.saveContentId(mContext, mVideoList.get(position)
								.getContentId());
						boolean isDownloaded = fileHelper.isContentDownloaded(mVideoList.get
								(position).getFileName());
						String url = mVideoList.get(position).getUrl();
						url.replace("www", "nexxoo:wenexxoo4kiosk!@www");
						Intent i = new Intent(mContext, VideoActivity.class);
						i.putExtra(mContext.getString(R.string
								.video_activity_intent_url_extra), url);
						String name = mVideoList.get(position).getFileName();
						i.putExtra("filename", name);
						i.putExtra("isVideoDownloaded", isDownloaded);
						mContext.startActivity(i);
						dbHandler.addContent(mVideoList.get(position).getContentId());
					}
				});

		item.download_button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Nexxoo.saveContentId(mContext,mVideoList.get(position).getContentId());
				if (isVideoDownloaded) {
					File video = new File(fileHelper.getFileAbsolutePath(mVideoList
							.get(position).getFileName()));
					video.delete();
					ImageView image = (ImageView) v;
					image.setImageResource(R.drawable.ic_grid_download);
				} else {
					DownloadAsyncTask task = new DownloadAsyncTask(mContext, mVideoList
							.get(position).getUrl(), mVideoList.get
							(position).getFileName(),(ImageView)v, Global
							.DOWNLOAD_TASK_TYPE_VIDEO);
					task.execute();
					dbHandler.addContent(mVideoList.get(position).getContentId());
				}
			}
		});

		if (!mVideoList.get(position).getmPictureList().isEmpty()) {
			ImageLoader imageLoader = ImageLoader.getInstance(); // Get singleton instance
			imageLoader.displayImage(mVideoList.get(position).getmPictureList().get
							(0).getmUrl(),
					item.videoCover, new ImageLoadingListener() {

						@Override
						public void onLoadingStarted(String imageUri, View view) {
						}

						@Override
						public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
							ImageView mImageView = (ImageView) view;
							mImageView.setImageResource(R.drawable.default_no_image);
						}

						@Override
						public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
						}

						@Override
						public void onLoadingCancelled(String imageUri, View view) {

						}
					});
		}else{
			item.videoCover.setImageResource(R.drawable.default_no_image);
		}


		return convertView;
	}


	@Override
	public int getCount() {
//		Log.e(Nexxoo.TAG,"GridviewAdapter get count:" +mvideoList.size());
		return mVideoList.size();
	}

	@Override
	public Video getItem(int position) {
		return mVideoList.get(position);
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
		ImageView download_button;
	}

}