package de.nexxoo.kiosk_app;

import android.content.Context;
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
import de.nexxoo.kiosk_app.entity.Video;
import de.nexxoo.kiosk_app.tools.FileStorageHelper;
import de.nexxoo.kiosk_app.tools.Misc;
import de.nexxoo.kiosk_app.tools.Nexxoo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by b.yuan on 29.07.2015.
 */
public class SearchResultVideoListAdapter extends ArrayAdapter<Video> {

	private LayoutInflater mInflater;
	private ImageLoader mImageLoader;
	private Context mContext;
	private List<Video> mVideoList;
	private int mLayoutId;

	public SearchResultVideoListAdapter(Context context, int layoutId, List<Video> objects) {
		super(context, layoutId, objects);
		mInflater = LayoutInflater.from(context);
		mImageLoader = ImageLoader.getInstance();
		mContext = context;
		mVideoList = new ArrayList<Video>(objects);
		mLayoutId = layoutId;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		Item item;
		if (convertView  == null) {
			convertView = mInflater.inflate(mLayoutId, parent, false);
			convertView.setBackgroundColor(mContext.getResources().getColor(
					R.color.RealWhite));
			if (mLayoutId == R.layout.video_gridview_item) {
				// grid view
				item = new Item();
				item.videoCover = (ImageView) convertView.findViewById(R.id.video_gridview_item_cover);

			} else {
				// list layout
				item = new Item();
				item.videoName = (TextView) convertView
						.findViewById(R.id.video_listview_item_name);
				item.videoName.setTypeface(Misc.getCustomFont(mContext,
						Misc.FONT_NORMAL));
				item.videoSize = (TextView) convertView
						.findViewById(R.id.video_listview_item_size);
				item.videoSize.setTypeface(Misc.getCustomFont(mContext,
						Misc.FONT_NORMAL));
				item.videoCover = (ImageView) convertView.findViewById(R.id.video_listview_item_cover);

			}

			convertView.setTag(item);
		} else {
			item = (Item) convertView.getTag();
		}
		item.videoName.setText(mVideoList.get(position).getName());
		item.videoName.setText(mVideoList.get(position).getName());
		String deteilInformation = Nexxoo.splitToComponentTimes(mVideoList.get(position).getDuration())
				+Nexxoo.DURATION_DIVIDER+	Nexxoo
				.readableFileSize(mVideoList.get(position)
						.getSize());
		item.videoSize.setText(deteilInformation);
		if (!mVideoList.get(position).getmPictureList().isEmpty()) {
			mImageLoader.displayImage(mVideoList.get(position).getmPictureList().get
							(0).getmUrl(),
					item.videoCover,new ImageLoadingListener(){

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
	public int getViewTypeCount() {
		// menu type count
		return 2;
	}

	@Override
	public int getItemViewType(int position) {
		// current menu type
		Video video = mVideoList.get(position);
		FileStorageHelper helper = new FileStorageHelper(mContext);
		boolean isVideoDownloaded = helper.isContentDownloaded(video.getName());
		return isVideoDownloaded?1:0;
	}

	private static class Item {
		TextView videoName;
		TextView videoSize;
		ImageView videoCover;
	}
}
