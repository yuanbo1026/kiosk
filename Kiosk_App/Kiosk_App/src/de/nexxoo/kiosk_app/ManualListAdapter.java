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
import de.nexxoo.kiosk_app.entity.Manual;
import de.nexxoo.kiosk_app.tools.Misc;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by b.yuan on 28.07.2015.
 */
public class ManualListAdapter extends ArrayAdapter<Manual> {

	private LayoutInflater mInflater;
	private Context mContext;
	private List<Manual> mManualList;
	private int mLayoutId;
	//	private List<Cover> mPictureList = new ArrayList<Cover>();
	private List<Bitmap> mBitmaps = new ArrayList<Bitmap>();
	private Bitmap bitmap;


	public ManualListAdapter(Context context, int layoutId, List<Manual> objects) {
		super(context, layoutId, objects);
		mInflater = LayoutInflater.from(context);
		mContext = context;
		mManualList = new ArrayList<Manual>(objects);
		mLayoutId = layoutId;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		Item item = null;
		View v = convertView;
		if (v == null) {
			v = mInflater.inflate(mLayoutId, parent, false);
			v.setBackgroundColor(mContext.getResources().getColor(
					R.color.RealWhite));
			if (mLayoutId == R.layout.manual_gridview_item) {
				// grid view
				item = new Item();
				item.manualName = (TextView) v
						.findViewById(R.id.manual_gridview_item_name);
				item.manualName.setTypeface(Misc.getCustomFont(mContext,
						Misc.FONT_BOLD));
				item.manualSize = (TextView) v.findViewById(R.id.manual_gridview_item_size);
				item.manualSize.setTypeface(Misc.getCustomFont(mContext,
						Misc.FONT_NORMAL));
				item.manualCover = (ImageView) v.findViewById(R.id.manual_gridview_item_cover);

			} else {
				// list view

				item = new Item();
				item.manualName = (TextView) v
						.findViewById(R.id.manual_listview_item_name);
				item.manualName.setTypeface(Misc.getCustomFont(mContext,
						Misc.FONT_BOLD));
				item.manualSize = (TextView) v.findViewById(R.id.manual_listview_item_size);
				item.manualSize.setTypeface(Misc.getCustomFont(mContext,
						Misc.FONT_NORMAL));
				item.manualCover = (ImageView) v.findViewById(R.id.manual_listview_item_cover);

			}

			item.manualName.setText(mManualList.get(position).getName());

			ImageLoader imageLoader = ImageLoader.getInstance(); // Get singleton instance
			imageLoader.displayImage(mManualList.get(position).getmPictureList().get
							(0).getmUrl(),
					item.manualCover,new ImageLoadingListener(){

						@Override
						public void onLoadingStarted(String imageUri, View view) {

						}

						@Override
						public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
							ImageView mImageView = (ImageView) view;
							mImageView.setImageResource(R.drawable.catalog_cover_small);
						}

						@Override
						public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {

						}

						@Override
						public void onLoadingCancelled(String imageUri, View view) {

						}
					});

			v.setTag(item);
		} else {
			item = (Item) v.getTag();
		}

		return v;
	}

	@Override
	public int getCount() {
		return mManualList.size();
	}

	@Override
	public Manual getItem(int position) {
		return mManualList.get(position);
	}

	private static class Item {
		TextView manualName;
		TextView manualSize;
		ImageView manualCover;
	}

}
