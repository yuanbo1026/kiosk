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
import de.nexxoo.kiosk_app.entity.Manual;
import de.nexxoo.kiosk_app.tools.Misc;
import de.nexxoo.kiosk_app.tools.Nexxoo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by b.yuan on 17.08.2015.
 */
public class ManualGridViewAdapter extends ArrayAdapter<Manual> {

	private LayoutInflater mInflater;
	private Context mContext;
	private List<Manual> mManualList;
	private int mLayoutId;

	public ManualGridViewAdapter(Context context, int layoutId, List<Manual> objects) {
		super(context, layoutId, objects);
		mInflater = LayoutInflater.from(context);
		mContext = context;
		mManualList = new ArrayList<Manual>(objects);
		mLayoutId = layoutId;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		Item item;
		if (convertView  == null) {
			convertView = mInflater.inflate(mLayoutId, parent, false);
			convertView.setBackgroundColor(mContext.getResources().getColor(
					R.color.RealWhite));

			Log.e(Nexxoo.TAG,"GridViewAdapter Position: "+position);
			item = new Item();
			item.manualName = (TextView) convertView
					.findViewById(R.id.manual_gridview_item_name);
			item.manualName.setTypeface(Misc.getCustomFont(mContext,Misc.FONT_NORMAL));
			item.manualSize = (TextView) convertView.findViewById(R.id.manual_gridview_item_size);
			item.manualSize.setTypeface(Misc.getCustomFont(mContext, Misc.FONT_NORMAL));
			item.manualCover = (ImageView) convertView.findViewById(R.id.manual_gridview_item_cover);



			convertView.setTag(item);
		} else {
			item = (Item) convertView.getTag();
		}

		item.manualName.setText(mManualList.get(position).getName()+position);

		ImageLoader imageLoader = ImageLoader.getInstance(); // Get singleton instance
		imageLoader.displayImage(mManualList.get(position).getmPictureList().get
						(0).getmUrl(),
				item.manualCover,new ImageLoadingListener(){

					@Override
					public void onLoadingStarted(String imageUri, View view) {
//							Log.d(Nexxoo.TAG, "Image loading starts: " + imageUri);
					}

					@Override
					public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
						ImageView mImageView = (ImageView) view;
						mImageView.setImageResource(R.drawable.catalog_cover_small);
					}

					@Override
					public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
						Log.e(Nexxoo.TAG,"Image loading completes: "+imageUri);
					}

					@Override
					public void onLoadingCancelled(String imageUri, View view) {

					}
				});

		return convertView;
	}

	@Override
	public int getCount() {
		Log.e(Nexxoo.TAG,"GridviewAdapter get count:" +mManualList.size());
		return mManualList.size();
	}

	@Override
	public Manual getItem(int position) {
		return mManualList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	private static class Item {
		TextView manualName;
		TextView manualSize;
		ImageView manualCover;
	}

}