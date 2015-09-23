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
import de.nexxoo.kiosk_app.entity.Catalog;
import de.nexxoo.kiosk_app.tools.FileStorageHelper;
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
	private FileStorageHelper helper;


	public CatalogListAdapter(Context context, int layoutId, List<Catalog> objects) {
		super(context, layoutId, objects);
		mInflater = LayoutInflater.from(context);
		mContext = context;
		mCatalogList = new ArrayList<Catalog>(objects);
		mLayoutId = layoutId;
		helper = new FileStorageHelper(context);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		Item item;
		View v = convertView;
		if (v == null) {
			v = mInflater.inflate(mLayoutId, parent, false);
			v.setBackgroundColor(mContext.getResources().getColor(
					R.color.RealWhite));
			item = new Item();
			item.catalogName = (TextView) v
					.findViewById(R.id.catalog_listview_item_name);
			item.catalogName.setTypeface(Misc.getCustomFont(mContext,
					Misc.FONT_NORMAL));
			item.catalogSize = (TextView) v.findViewById(R.id.catalog_listview_item_size);
			item.catalogSize.setTypeface(Misc.getCustomFont(mContext,
					Misc.FONT_NORMAL));
			item.catalogCover = (ImageView) v.findViewById(R.id.catalog_listview_item_cover);
			v.setTag(item);
		} else {
			if (convertView.getTag() != null)
				item = (Item) v.getTag();
			else {
				v = mInflater.inflate(mLayoutId, parent, false);
				v.setBackgroundColor(mContext.getResources().getColor(
						R.color.RealWhite));
				item = new Item();
				item.catalogName = (TextView) v
						.findViewById(R.id.catalog_listview_item_name);
				item.catalogName.setTypeface(Misc.getCustomFont(mContext,
						Misc.FONT_NORMAL));
				item.catalogSize = (TextView) v.findViewById(R.id.catalog_listview_item_size);
				item.catalogSize.setTypeface(Misc.getCustomFont(mContext,
						Misc.FONT_NORMAL));
				item.catalogCover = (ImageView) v.findViewById(R.id.catalog_listview_item_cover);
				v.setTag(item);
			}
		}

		item.catalogName.setText(mCatalogList.get(position).getName());
		String detailInformation = mCatalogList.get(position).getPages()
				+Nexxoo.PAGES+	Nexxoo
				.readableFileSize(mCatalogList.get(position)
						.getSize());
		item.catalogSize.setText(detailInformation);
		if (!mCatalogList.get(position).getmPictureList().isEmpty()) {
			ImageLoader imageLoader = ImageLoader.getInstance(); // Get singleton instance
			imageLoader.displayImage(mCatalogList.get(position).getmPictureList().get
							(0).getmUrl(),
					item.catalogCover,new ImageLoadingListener(){

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
			item.catalogCover.setImageResource(R.drawable.default_no_image);
		}

		return v;
	}

	@Override
	public int getItemViewType(int position) {
		String fileName = mCatalogList.get(position).getFileName();
		boolean isContentDownloaded = helper.isContentDownloaded(fileName);
		return isContentDownloaded?1:0;
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