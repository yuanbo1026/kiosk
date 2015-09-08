package de.nexxoo.kiosk_app;

import android.content.Context;
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
import de.nexxoo.kiosk_app.entity.BaseEntity;
import de.nexxoo.kiosk_app.tools.FileStorageHelper;
import de.nexxoo.kiosk_app.tools.Misc;
import de.nexxoo.kiosk_app.tools.Nexxoo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by b.yuan on 28.07.2015.
 */
public class HistoryListAdapter extends ArrayAdapter<BaseEntity> {

	private LayoutInflater mInflater;
	private Context mContext;
	private List<BaseEntity> mBaseEntityList;
	private int mLayoutId;
	private FileStorageHelper helper;


	public HistoryListAdapter(Context context, int layoutId, List<BaseEntity> objects) {
		super(context, layoutId, objects);
		mInflater = LayoutInflater.from(context);
		mContext = context;
		mBaseEntityList = new ArrayList<BaseEntity>(objects);
		mLayoutId = layoutId;
		helper = new FileStorageHelper(context);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		Item item = null;
		View v = convertView;
		if (v == null) {
			v = mInflater.inflate(mLayoutId, parent, false);
			v.setBackgroundColor(mContext.getResources().getColor(
					R.color.RealWhite));
			Log.d(Nexxoo.TAG, "ListView Adapter Position: " + position);
			item = new Item();
			item.name = (TextView) v
					.findViewById(R.id.history_listview_item_name);
			item.name.setTypeface(Misc.getCustomFont(mContext,
					Misc.FONT_NORMAL));
			item.size = (TextView) v.findViewById(R.id.history_listview_item_size);
			item.size.setTypeface(Misc.getCustomFont(mContext,
					Misc.FONT_NORMAL));
			item.cover = (ImageView) v.findViewById(R.id.history_listview_item_cover);

			item.name.setText(mBaseEntityList.get(position).getName());
			if (!mBaseEntityList.get(position).getmPictureList().isEmpty()) {
				ImageLoader imageLoader = ImageLoader.getInstance(); // Get singleton instance
				imageLoader.displayImage(mBaseEntityList.get(position).getmPictureList().get
								(0).getmUrl(),
						item.cover, new ImageLoadingListener() {

							@Override
							public void onLoadingStarted(String imageUri, View view) {
//								Log.d(Nexxoo.TAG, "Image loading starts: " + imageUri);
							}

							@Override
							public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
								ImageView mImageView = (ImageView) view;
								mImageView.setImageResource(R.drawable.default_no_image);
							}

							@Override
							public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
//								Log.d(Nexxoo.TAG,"Image loading completes: "+imageUri);
							}

							@Override
							public void onLoadingCancelled(String imageUri, View view) {

							}
						});
			} else {
				item.cover.setImageResource(R.drawable.default_no_image);
			}


			v.setTag(item);
		} else {
			item = (Item) v.getTag();
		}

		return v;
	}

	@Override
	public int getItemViewType(int position) {
		int contentTypeId = mBaseEntityList.get(position).getContentTypeId();
		return contentTypeId;
	}

	@Override
	public int getCount() {
		return mBaseEntityList.size();
	}

	@Override
	public BaseEntity getItem(int position) {
		return mBaseEntityList.get(position);
	}

	private static class Item {
		TextView name;
		TextView size;
		ImageView cover;
	}

}
