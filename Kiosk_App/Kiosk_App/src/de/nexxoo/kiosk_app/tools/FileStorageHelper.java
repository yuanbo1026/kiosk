package de.nexxoo.kiosk_app.tools;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.util.Log;
import de.nexxoo.kiosk_app.download.OnDownloadResult;
import de.nexxoo.kiosk_app.entity.BaseEntity;
import de.nexxoo.kiosk_app.webservice.NexxooWebservice;

import java.io.File;

public class FileStorageHelper {

	private Context mContext;

	private static final String DOWNLOADFOLDER = "downloads";

	public FileStorageHelper(Context context) {
		mContext = context;
	}

	/**
	 * Returns the download folder for this app. Downloads will be removed when
	 * app is uninstalled. If available external Storage will be used, otherwise
	 * internal memory
	 * 
	 * @return Path to download location for app or <code>null</code> if context
	 *         is not set
	 */
	public String getDownloadFolder() {
		if (mContext != null) {
			if (Environment.getExternalStorageState().equals(
					Environment.MEDIA_MOUNTED)
					&& mContext.getExternalFilesDir(null) != null) {
				// external is available and will be used
				return mContext.getExternalFilesDir(null).getAbsolutePath()
						+ File.separator + DOWNLOADFOLDER + File.separator;
			}
			// we switch to internal memory
			return mContext.getFilesDir().getAbsolutePath() + File.separator
					+ DOWNLOADFOLDER + File.separator;
		}
		return null;
	}

	public boolean isContentDownloaded(String fileName){
		File file = new File(getDownloadFolder()+fileName);
		return file.exists();
	}

	public String getFileAbsolutePath(String fileName){
		return getDownloadFolder()+fileName;
	}

	/**
	 * Checks if app given by package name is installed on device.
	 * 
	 * @param packageName
	 *            The package name where the app should be found
	 * @return true if app is installed on device. false if not.
	 */
	public static boolean isInstalled(String packageName, Context ctx) {
		PackageManager pm = ctx.getPackageManager();
		try {
			pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
			return true;
		} catch (PackageManager.NameNotFoundException e) {
			return false;
		}
	}

	/**
	 * Check if app given by package name is on current version.
	 * 
	 * @param packageName
	 *            The package name where the app should be found
	 * @param currentVersion
	 *            value to check against
	 * @param ctx
	 *            Context of the activity\app
	 * @return true if app is on current version. false if not.
	 */
	public static boolean isNewestVersion(String packageName,
			int currentVersion, Context ctx) {
		try {
			Log.e("APP_VERSION", packageName + " - - " + ctx.getPackageManager().getPackageInfo(packageName, 0).versionCode);
			return (ctx.getPackageManager().getPackageInfo(packageName, 0).versionCode == currentVersion);
		} catch (PackageManager.NameNotFoundException e) {
			return false;
		}
	}

	/*public static OnDownloadResult getCallback(final Activity activity, final BaseEntity
			content, boolean reattach){

		return new OnDownloadResult() {

			@Override
			public void onDownloadSuccess(final BaseEntity content) {
				if (activity != null) {
					File file = new File(content
							.getFileName());
					if (activity instanceof ContentDetailActivity) {
						((ContentDetailActivity) activity)
								.hideProgress();
					}

				}

			}

			@Override
			public void onDownloadProgress(int percent) {
				if (activity != null && activity instanceof ContentDetailActivity) {
					((ContentDetailActivity) activity)
							.setDownloadProgress(percent);
				}

			}

			@Override
			public void onDownloadFailed(String msg, int code) {

			}

			@Override
			public void onDownloadStarted(final BaseEntity content) {
			}
		};
	}*/

	/**
	 * Checks if given content (magazine or video) is downloaded or not.
	 * 
	 * @param content
	 *            that should be checked for isDownloaded
	 * @return <code>true</code> if content is downloaded. <code>false</code> if
	 *         not.
	 */
//	public static boolean isDownloaded(Content content, Context ctx) {
//		if(NexxooWebservice.hasActiveDownloads()){
//			for(DownloadAsyncTask task: NexxooWebservice.getActiveDownloads()){
//				if(task.getContent().getContentId() == content.getContentId() && !task.isCancelled()){					
//					return false;
//				}
//			}
//		}
//		File file = new File(content.getLocalFileName(ctx));
//		return file.exists();
//	}

	/**
	 *
	 * @param content
	 * @param activity
	 * @param callback
	 */
	public static void download(final BaseEntity content, final Activity activity,
								OnDownloadResult callback) {

		NexxooWebservice.getDownload(true, activity, content, callback);
	}
//	
//	public static OnDownloadResult getCallback(final Activity activity, final Content content, boolean reattach){
//		Intent intent = new Intent(activity, ContentDetailActivity.class);
//		intent.putExtra(activity.getString(R.string.appstock_extra_content), content);
//		final PendingIntent pIntent = PendingIntent.getActivity(activity, (int) content.getContentId(),
//				intent, PendingIntent.FLAG_CANCEL_CURRENT);
//		
//		final NotificationManager mNotifyManager = (NotificationManager) activity
//				.getSystemService(Context.NOTIFICATION_SERVICE);
//		final NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
//				activity);
//		
//		mBuilder.setContentTitle(
//				activity.getString(R.string.appstock_download_downloading)
//						.replaceAll(Nexxoo.REPLACE_1, content.getName()))
//				.setSmallIcon(R.drawable.ic_favorite)
//				.setContentIntent(pIntent).setOngoing(true).setContentText(
//						activity.getString(
//								R.string.appstock_download_downloadof)
//								.replaceAll(Nexxoo.REPLACE_1,content.getName()));
//		
//		return new OnDownloadResult() {
//
//			@Override
//			public void onDownloadSuccess(final Content content) {
//				if (activity != null) {
//					mBuilder.setContentText(
//							activity.getString(R.string.appstock_download_done_open))
//							.setProgress(100, 100, false)
//							.setAutoCancel(true);
//
//					File file = new File(content
//							.getLocalFileName(activity));
//					Intent intentForPrepare = null;
//
//					if (activity instanceof ContentDetailActivity) {
//						((ContentDetailActivity) activity)
//								.hideProgress();
//					}
//
//					try {
//						if(content instanceof App){
//							intentForPrepare = new Intent(
//									Intent.ACTION_VIEW);
//							intentForPrepare.setDataAndType(
//									Uri.fromFile(file),
//									content.getMimeType());
//							PendingIntent pIntent = PendingIntent
//									.getActivity(
//											activity,
//											NexxooPayment.PAYPAL_REQUEST_CODE_PAYMENT,
//											intentForPrepare,
//											PendingIntent.FLAG_UPDATE_CURRENT);
//	
//							activity.startActivity(intentForPrepare);
//							
//							Intent intent = new Intent(activity, InstallAppActivity.class);
//							intent.putExtra(activity.getString(R.string.appstock_extra_content), content);
//							pIntent = PendingIntent.getActivity(activity, (int) content.getContentId(),
//									intent, PendingIntent.FLAG_CANCEL_CURRENT);
//
//								mBuilder.setOngoing(
//										false).setContentIntent(pIntent);
//						}
//
//						if (content.getContentType() == App.CONTENTTYPE) {
//							mBuilder.setContentText(activity
//									.getString(R.string.appstock_download_done_start));
//						}
//						Log.d("JRe","notyfication");
//						mBuilder.setOngoing(
//								false);
//						ImageLoader.getInstance().loadImage(content.getIcon().getmUrl(), new SimpleImageLoadingListener(){
//						    @Override
//						    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
//						    	 int density= activity.getResources().getDisplayMetrics().densityDpi;
//
//						    	 switch(density){
//						    	 case DisplayMetrics.DENSITY_LOW:
//						    	    	loadedImage=Bitmap.createScaledBitmap(loadedImage, 48, 48, false);
//						    	     break;
//						    	 case DisplayMetrics.DENSITY_MEDIUM:
//						    	    	loadedImage=Bitmap.createScaledBitmap(loadedImage, 64, 64, false);
//						    	     break;
//						    	 case DisplayMetrics.DENSITY_HIGH:
//						    	    	loadedImage=Bitmap.createScaledBitmap(loadedImage, 96, 96, false);
//						    	     break;
//						    	 case DisplayMetrics.DENSITY_XHIGH:
//						    	    	loadedImage=Bitmap.createScaledBitmap(loadedImage, 128, 128, false);
//							    	 break;
//						    	 case DisplayMetrics.DENSITY_XXHIGH:
//						    	    	loadedImage=Bitmap.createScaledBitmap(loadedImage, 192, 192, false);
//						    	     break;
//						    	 }
//								mBuilder.setLargeIcon(loadedImage);
//								mNotifyManager.notify(
//										(int) content.getContentId(),
//										mBuilder.build());
//						    }
//						});
//					} catch (ActivityNotFoundException e) {
//						Log.d(Nexxoo.TAG2,
//								"Activity for pending intent not found: "
//										+ e.getMessage());
//					}
//				}
//
//			}
//
//			@Override
//			public void onDownloadProgress(int percent) {
//				mBuilder.setProgress(100, percent, false);
//				mNotifyManager.notify((int) content.getContentId(),
//						mBuilder.build());	
//				if (activity != null && activity instanceof ContentDetailActivity) {
//					((ContentDetailActivity) activity)
//							.setDownloadProgress(percent);
//				}
//				
//			}
//
//			@Override
//			public void onDownloadFailed(String msg, int code) {
//
//				// mBuilder.setContentText(msg).setProgress(0, 0, false)
//				// .setOngoing(false).setAutoCancel(true);
//				// mNotifyManager.notify((int) content.getContentId(),
//				// mBuilder.build());
//				mNotifyManager.cancel((int) content.getContentId());
//				if (activity instanceof ContentDetailActivity) {
//					((ContentDetailActivity) activity).hideProgress();
//					Log.d(Nexxoo.TAG, "Progress hidden");
//				}
//
//			}
//
//			@Override
//			public void onDownloadStarted(final Content content) {
//				Log.d("JD", "onDownloadStarted");
//				mBuilder.setContentTitle(
//						activity.getString(R.string.appstock_download_downloading)
//								.replaceAll(Nexxoo.REPLACE_1, content.getName()))
//						.setContentText(
//								activity.getString(R.string.appstock_download_preparing))
//						.setProgress(0, 0, true).setSmallIcon(R.drawable.ic_favorite)
//						.setContentIntent(pIntent).setOngoing(true);
//				
//				mBuilder.setContentText(
//						activity.getString(
//								R.string.appstock_download_downloadof)
//								.replaceAll(Nexxoo.REPLACE_1,
//										content.getName()))
//						.setProgress(0, 0, true);
//				ImageLoader.getInstance().loadImage(content.getIcon().getmUrl(), new SimpleImageLoadingListener(){
//				    @Override
//				    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
//				    	 int density= activity.getResources().getDisplayMetrics().densityDpi;
//
//				    	 switch(density){
//				    	 case DisplayMetrics.DENSITY_LOW:
//				    	    	loadedImage=Bitmap.createScaledBitmap(loadedImage, 48, 48, false);
//				    	     break;
//				    	 case DisplayMetrics.DENSITY_MEDIUM:
//				    	    	loadedImage=Bitmap.createScaledBitmap(loadedImage, 64, 64, false);
//				    	     break;
//				    	 case DisplayMetrics.DENSITY_HIGH:
//				    	    	loadedImage=Bitmap.createScaledBitmap(loadedImage, 96, 96, false);
//				    	     break;
//				    	 case DisplayMetrics.DENSITY_XHIGH:
//				    	    	loadedImage=Bitmap.createScaledBitmap(loadedImage, 128, 128, false);
//					    	 break;
//				    	 case DisplayMetrics.DENSITY_XXHIGH:
//				    	    	loadedImage=Bitmap.createScaledBitmap(loadedImage, 192, 192, false);
//				    	     break;
//				    	 }
//						mBuilder.setLargeIcon(loadedImage);
//						mNotifyManager.notify(
//								(int) content.getContentId(),
//								mBuilder.build());
//				    }
//				});
//			}
//		};
//	}
}
