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
import de.nexxoo.kiosk_app.db.DatabaseHandler;
import de.nexxoo.kiosk_app.entity.Manual;
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
public class ManualGridViewAdapter extends ArrayAdapter<Manual> {

	private LayoutInflater mInflater;
	private Context mContext;
	private List<Manual> mManualList;
	private int mLayoutId;
	private FileStorageHelper fileHelper;
	private DatabaseHandler dbHandler;
	private UpdateSwipeListViewMenuItem callback;

	public ManualGridViewAdapter(Context context, int layoutId, List<Manual> objects) {
		super(context, layoutId, objects);
		mInflater = LayoutInflater.from(context);
		mContext = context;
		mManualList = new ArrayList<Manual>(objects);
		mLayoutId = layoutId;
		fileHelper = new FileStorageHelper(context);
		dbHandler = new DatabaseHandler(context);
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		Item item;
		if (convertView == null) {
			convertView = mInflater.inflate(mLayoutId, parent, false);
			convertView.setBackgroundColor(mContext.getResources().getColor(
					R.color.RealWhite));

//			Log.e(Nexxoo.TAG,"GridViewAdapter Position: "+position);
			item = new Item();
			item.manualName = (TextView) convertView
					.findViewById(R.id.manual_gridview_item_name);
			item.manualName.setTypeface(Misc.getCustomFont(mContext, Misc.FONT_NORMAL));
			item.manualSize = (TextView) convertView.findViewById(R.id.manual_gridview_item_size);
			item.manualSize.setTypeface(Misc.getCustomFont(mContext, Misc.FONT_NORMAL));
			item.manualCover = (ImageView) convertView.findViewById(R.id.manual_gridview_item_cover);
			item.trash_button = (ImageView) convertView.findViewById(R.id
					.manual_grid_item_trash_button);
			item.watch_button = (ImageView) convertView.findViewById(R.id.manual_grid_item_watch_button);

			convertView.setTag(item);
		} else {
			item = (Item) convertView.getTag();
		}

		item.manualName.setText(mManualList.get(position).getName());
		String deteilInformation = mManualList.get(position).getPages()
				+Nexxoo.PAGES+	Nexxoo
				.readableFileSize(mManualList.get(position)
						.getSize());
		item.manualSize.setText(deteilInformation);
		item.trash_button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				File video = new File(fileHelper.getDownloadAbsolutePath(mManualList
						.get(position).getFileName()));
				video.delete();
				ImageView image = (ImageView) v;
				image.setVisibility(View.INVISIBLE);
				//hide trash button on listview item
				callback.updateListViewItemIcon(position, false);
			}
		});
		item.manualCover.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Nexxoo.saveContentId(mContext,mManualList.get(position).getContentId());
				SquareLayout parent = (SquareLayout) v.getParent();
				LinearLayout fatherView = (LinearLayout)parent.getParent();
				ImageView mImageView = (ImageView) fatherView.findViewById(R.id
						.manual_grid_item_trash_button);
				//show trash button on listview item
				callback.updateListViewItemIcon(position,true);
				String filename = mManualList.get(position).getFileName();
				if (fileHelper.isContentDownloaded(filename)) {
					File file = new File(fileHelper.getDownloadAbsolutePath(filename));
					Intent target = new Intent(Intent.ACTION_VIEW);
					target.setDataAndType(Uri.fromFile(file), "application/pdf");
					target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

					Intent i = Intent.createChooser(target, mContext
							.getString(R.string.open_pdf_file));
					mContext.startActivity(i);
				} else {
					DownloadAsyncTask task = new DownloadAsyncTask(mContext,
							mManualList
									.get(position).getUrl(), mManualList.get
							(position).getFileName(),mImageView, Global
							.DOWNLOAD_TASK_TYPE_PDF);
					task.execute();
				}
			}
		});
		item.watch_button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Nexxoo.saveContentId(mContext,mManualList.get(position).getContentId());
				RelativeLayout parent = (RelativeLayout) v.getParent();
				ImageView mImageView = (ImageView) parent.findViewById(R.id
						.manual_grid_item_trash_button);
				callback.updateListViewItemIcon(position,true);
				String filename = mManualList.get(position).getFileName();
				if (fileHelper.isContentDownloaded(filename)) {
					File file = new File(fileHelper.getDownloadAbsolutePath(filename));
					Intent target = new Intent(Intent.ACTION_VIEW);
					target.setDataAndType(Uri.fromFile(file), "application/pdf");
					target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

					Intent i = Intent.createChooser(target, mContext
							.getString(R.string.open_pdf_file));
					mContext.startActivity(i);
				} else {
					DownloadAsyncTask task = new DownloadAsyncTask(mContext,
							mManualList
									.get(position).getUrl(), mManualList.get
							(position).getFileName(),mImageView, Global
							.DOWNLOAD_TASK_TYPE_PDF);
					task.execute();
				}
			}
		});
		if (!mManualList.get(position).getmPictureList().isEmpty()) {
			ImageLoader imageLoader = ImageLoader.getInstance(); // Get singleton instance
			imageLoader.displayImage(mManualList.get(position).getmPictureList().get
							(0).getmUrl(),
					item.manualCover, new ImageLoadingListener() {

						@Override
						public void onLoadingStarted(String imageUri, View view) {
//							Log.d(Nexxoo.TAG, "Image loading starts: " + imageUri);
						}

						@Override
						public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
							ImageView mImageView = (ImageView) view;
							mImageView.setImageResource(R.drawable.default_no_image);
						}

						@Override
						public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
//						Log.e(Nexxoo.TAG,"Image loading completes: "+imageUri);
						}

						@Override
						public void onLoadingCancelled(String imageUri, View view) {

						}
					});
		} else {
			item.manualCover.setImageResource(R.drawable.default_no_image);
		}
		if (fileHelper.isContentDownloaded(mManualList.get(position).getFileName()))
			item.trash_button.setVisibility(View.VISIBLE);
		else
			item.trash_button.setVisibility(View.INVISIBLE);

		return convertView;
	}

	@Override
	public int getCount() {
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
		ImageView trash_button;
		ImageView watch_button;
	}

	public UpdateSwipeListViewMenuItem getCallback() {
		return callback;
	}

	public void setCallback(UpdateSwipeListViewMenuItem callback) {
		this.callback = callback;
	}


}