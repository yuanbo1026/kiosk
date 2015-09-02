package de.nexxoo.kiosk_app;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import de.nexxoo.kiosk_app.entity.Catalog;
import de.nexxoo.kiosk_app.layout.SquareLayout;
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
public class CatalogGridViewAdapter extends ArrayAdapter<Catalog> {

	private LayoutInflater mInflater;
	private Context mContext;
	private List<Catalog> mCatalogList;
	private int mLayoutId;
	private FileStorageHelper fileHelper;

	public CatalogGridViewAdapter(Context context, int layoutId, List<Catalog> objects) {
		super(context, layoutId, objects);
		mInflater = LayoutInflater.from(context);
		mContext = context;
		mCatalogList = new ArrayList<Catalog>(objects);
		mLayoutId = layoutId;
		fileHelper = new FileStorageHelper(context);
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final int currentPosition = position;
		Item item;
		if (convertView == null) {
			convertView = mInflater.inflate(mLayoutId, parent, false);
			convertView.setBackgroundColor(mContext.getResources().getColor(
					R.color.RealWhite));

//			Log.e(Nexxoo.TAG,"GridViewAdapter Position: "+position);
			item = new Item();
			item.catalogName = (TextView) convertView
					.findViewById(R.id.catalog_gridview_item_name);
			item.catalogName.setTypeface(Misc.getCustomFont(mContext, Misc.FONT_NORMAL));
			item.catalogSize = (TextView) convertView.findViewById(R.id.catalog_gridview_item_size);
			item.catalogSize.setTypeface(Misc.getCustomFont(mContext, Misc.FONT_NORMAL));
			item.catalogCover = (ImageView) convertView.findViewById(R.id.catalog_gridview_item_cover);
			item.trash_button = (ImageView) convertView.findViewById(R.id
					.catalog_grid_item_trash_button);
			item.watch_button = (ImageView) convertView.findViewById(R.id.catalog_grid_item_watch_button);

			convertView.setTag(item);
		} else {
			item = (Item) convertView.getTag();
		}

		item.catalogName.setText(mCatalogList.get(position).getName());
		String deteilInformation = mCatalogList.get(position).getPages()
				+Nexxoo.PAGES+	Nexxoo
				.readableFileSize(mCatalogList.get(position)
						.getSize());
		item.catalogSize.setText(deteilInformation);
		item.trash_button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				File video = new File(fileHelper.getFileAbsolutePath(mCatalogList
						.get(currentPosition).getFileName()));
				video.delete();
				ImageView image = (ImageView) v;
				image.setVisibility(View.INVISIBLE);
			}
		});
		item.catalogCover.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Nexxoo.saveContentId(mContext,mCatalogList.get(position).getContentId());
				SquareLayout parent = (SquareLayout) v.getParent();
				LinearLayout fatherView = (LinearLayout)parent.getParent();
				ImageView mImageView = (ImageView) fatherView.findViewById(R.id
						.catalog_grid_item_trash_button);

				String filename = mCatalogList.get(position).getFileName();
				if (fileHelper.isContentDownloaded(filename)) {
					File file = new File(fileHelper.getFileAbsolutePath(filename));
					Intent target = new Intent(Intent.ACTION_VIEW);
					target.setDataAndType(Uri.fromFile(file), "application/pdf");
					target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

					Intent i = Intent.createChooser(target, "Open File");
					mContext.startActivity(i);
				} else {
					DownloadAsyncTask task = new DownloadAsyncTask(mContext,
							mCatalogList
									.get(position).getUrl(), mCatalogList.get
							(position).getFileName(),mImageView, Global
							.DOWNLOAD_TASK_TYPE_PDF);
					task.execute();
				}
			}
		});
		item.watch_button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Nexxoo.saveContentId(mContext, mCatalogList.get(position).getContentId());
				RelativeLayout parent = (RelativeLayout) v.getParent();
				ImageView mImageView = (ImageView) parent.findViewById(R.id
						.catalog_grid_item_trash_button);

				String filename = mCatalogList.get(position).getFileName();
				if (fileHelper.isContentDownloaded(filename)) {
					File file = new File(fileHelper.getFileAbsolutePath(filename));
					Intent target = new Intent(Intent.ACTION_VIEW);
					target.setDataAndType(Uri.fromFile(file), "application/pdf");
					target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

					Intent i = Intent.createChooser(target, "Open File");
					mContext.startActivity(i);
				} else {
					DownloadAsyncTask task = new DownloadAsyncTask(mContext,
							mCatalogList
									.get(position).getUrl(), mCatalogList.get
							(position).getFileName(),mImageView, Global
							.DOWNLOAD_TASK_TYPE_PDF);
					task.execute();
				}
			}
		});
		if (!mCatalogList.get(position).getmPictureList().isEmpty()) {
			ImageLoader imageLoader = ImageLoader.getInstance(); // Get singleton instance
			imageLoader.displayImage(mCatalogList.get(position).getmPictureList().get
							(0).getmUrl(),
					item.catalogCover, new ImageLoadingListener() {

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

		if (fileHelper.isContentDownloaded(mCatalogList.get(position).getFileName()))
			item.trash_button.setVisibility(View.VISIBLE);
		else
			item.trash_button.setVisibility(View.INVISIBLE);

		return convertView;
	}

	@Override
	public int getCount() {
		return mCatalogList.size();
	}

	@Override
	public Catalog getItem(int position) {
		return mCatalogList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	private static class Item {
		TextView catalogName;
		TextView catalogSize;
		ImageView catalogCover;
		ImageView trash_button;
		ImageView watch_button;
	}

}