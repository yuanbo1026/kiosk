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
import de.nexxoo.kiosk_app.entity.Catalog;
import de.nexxoo.kiosk_app.tools.Misc;
import de.nexxoo.kiosk_app.tools.Nexxoo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by b.yuan on 29.07.2015.
 */
public class CatalogListAdapter extends ArrayAdapter<Catalog> {

	private LayoutInflater mInflater;
	private Context mContext;
	private List<Catalog> mCatalogList;
	private int mLayoutId;


	public CatalogListAdapter(Context context, int layoutId, List<Catalog> objects) {
		super(context, layoutId, objects);
		mInflater = LayoutInflater.from(context);
		mContext = context;
		mCatalogList = new ArrayList<Catalog>(objects);
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
			Log.d(Nexxoo.TAG, "ListView Adapter Position: " + position);
			item = new Item();
			item.catalogName = (TextView) v
					.findViewById(R.id.catalog_listview_item_name);
			item.catalogName.setTypeface(Misc.getCustomFont(mContext,
					Misc.FONT_NORMAL));
			item.catalogSize = (TextView) v.findViewById(R.id.catalog_listview_item_size);
			item.catalogSize.setTypeface(Misc.getCustomFont(mContext,
					Misc.FONT_NORMAL));
			item.catalogCover = (ImageView) v.findViewById(R.id.catalog_listview_item_cover);

			item.catalogName.setText(mCatalogList.get(position).getName());
			String deteilInformation = mCatalogList.get(position).getPages()
					+Nexxoo.PAGES+	Nexxoo
					.readableFileSize(mCatalogList.get(position)
							.getSize());
			item.catalogSize.setText(deteilInformation);
			if (!mCatalogList.get(position).getmPictureList().isEmpty()) {
				ImageLoader imageLoader = ImageLoader.getInstance(); // Get singleton instance
				imageLoader.displayImage(mCatalogList.get(position).getmPictureList().get
								(0).getmUrl(),
						item.catalogCover,new ImageLoadingListener(){

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
				item.catalogCover.setImageResource(R.drawable.default_no_image);
			}


			v.setTag(item);
		} else {
			item = (Item) v.getTag();
		}

		return v;
	}

	@Override
	public int getCount() {
		return mCatalogList.size();
	}

	@Override
	public Catalog getItem(int position) {
		return mCatalogList.get(position);
	}

	private static class Item {
		TextView catalogName;
		TextView catalogSize;
		ImageView catalogCover;
	}

}