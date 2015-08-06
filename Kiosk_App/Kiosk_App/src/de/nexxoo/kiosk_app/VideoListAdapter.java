package de.nexxoo.kiosk_app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.nostra13.universalimageloader.core.ImageLoader;
import de.nexxoo.kiosk_app.entity.Video;
import de.nexxoo.kiosk_app.tools.Misc;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by b.yuan on 29.07.2015.
 */
public class VideoListAdapter extends ArrayAdapter<Video> {

	private LayoutInflater mInflater;
	private ImageLoader mImageLoader;
	private Context mContext;
	private List<Video> mVideoList;
	private int mLayoutId;

	public VideoListAdapter(Context context, int layoutId, List<Video> objects) {
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
		View v = convertView;
		if (v == null) {
			v = mInflater.inflate(mLayoutId, parent, false);
			v.setBackgroundColor(mContext.getResources().getColor(
					R.color.RealWhite));
			if (mLayoutId == R.layout.video_gridview_item) {
				// grid view
				item = new Item();
				item.videoName = (TextView) v
						.findViewById(R.id.video_gridview_item_name);
				item.videoName.setTypeface(Misc.getCustomFont(mContext,
						Misc.FONT_BOLD));
				item.videoCover = (ImageView) v.findViewById(R.id.video_gridview_item_cover);

			} else {
				// list layout
				item = new Item();
				item.videoName = (TextView) v
						.findViewById(R.id.video_listview_item_name);
				item.videoName.setTypeface(Misc.getCustomFont(mContext,
						Misc.FONT_BOLD));
				item.videoSize = (TextView) v
						.findViewById(R.id.video_listview_item_size);
				item.videoSize.setTypeface(Misc.getCustomFont(mContext,
						Misc.FONT_BOLD));
				item.videoCover = (ImageView) v.findViewById(R.id.video_listview_item_cover);

			}

			v.setTag(item);
		} else {
			item = (Item) v.getTag();
		}

		if (mVideoList != null) {
			item.videoName.setText("Video Tutorial");
//			Video video = mVideoList.get(position);
//			item.videoName.setText(video.getmName());
		}
		return v;
	}

	private static class Item {
		TextView videoName;
		TextView videoSize;
		ImageView videoCover;
	}
}
