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
import de.nexxoo.kiosk_app.tools.FileStorageHelper;
import de.nexxoo.kiosk_app.tools.Misc;
import de.nexxoo.kiosk_app.tools.Nexxoo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by b.yuan on 28.07.2015.
 */
public class SearchResultPDFListAdapter extends ArrayAdapter<Manual> {

	private LayoutInflater mInflater;
	private Context mContext;
	private List<Manual> mManualList;
	private int mLayoutId;
	private FileStorageHelper helper;


	public SearchResultPDFListAdapter(Context context, int layoutId, List<Manual> objects) {
		super(context, layoutId, objects);
		mInflater = LayoutInflater.from(context);
		mContext = context;
		mManualList = new ArrayList<Manual>(objects);
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
				item.manualName = (TextView) v
						.findViewById(R.id.manual_listview_item_name);
				item.manualName.setTypeface(Misc.getCustomFont(mContext,
						Misc.FONT_NORMAL));
				item.manualSize = (TextView) v.findViewById(R.id.manual_listview_item_size);
				item.manualSize.setTypeface(Misc.getCustomFont(mContext,
						Misc.FONT_NORMAL));
				item.manualCover = (ImageView) v.findViewById(R.id.manual_listview_item_cover);

			item.manualName.setText(mManualList.get(position).getName());
			String deteilInformation = mManualList.get(position).getPages()
					+Nexxoo.PAGES+	Nexxoo
					.readableFileSize(mManualList.get(position)
							.getSize());
			item.manualSize.setText(deteilInformation);
			if (!mManualList.get(position).getmPictureList().isEmpty()) {
				ImageLoader imageLoader = ImageLoader.getInstance(); // Get singleton instance
				imageLoader.displayImage(mManualList.get(position).getmPictureList().get
								(0).getmUrl(),
						item.manualCover,new ImageLoadingListener(){

							@Override
							public void onLoadingStarted(String imageUri, View view) {
								Log.d(Nexxoo.TAG, "Image loading starts: " + imageUri);
							}

							@Override
							public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
								ImageView mImageView = (ImageView) view;
								mImageView.setImageResource(R.drawable.default_no_image);
							}

							@Override
							public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
								Log.d(Nexxoo.TAG,"Image loading completes: "+imageUri);
							}

							@Override
							public void onLoadingCancelled(String imageUri, View view) {

							}
						});
			}else{
				item.manualCover.setImageResource(R.drawable.default_no_image);
			}
			v.setTag(item);
		} else {
			item = (Item) v.getTag();
		}
		return v;
	}

	@Override
	public int getItemViewType(int position) {
		String fileName = mManualList.get(position).getFileName();
		boolean isContentDownloaded = helper.isContentDownloaded(fileName);
		return isContentDownloaded?1:0;
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
