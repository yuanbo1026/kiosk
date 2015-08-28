package de.nexxoo.kiosk_app.webservice;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;
import de.nexxoo.kiosk_app.download.DownloadAsyncTask;
import de.nexxoo.kiosk_app.download.OnDownloadResult;
import de.nexxoo.kiosk_app.entity.BaseEntity;
import de.nexxoo.kiosk_app.tools.Nexxoo;
import de.nexxoo.kiosk_app.webservice.error.JSONErrorHandler;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

/**
 * Static class that provides an interface for our webservice to interact with the
 * app stock webservice.
 * @author d.fredrich
 *
 */
public class NexxooWebservice {
	/**
	 * add this to test Git connection
	 */
	private static final String TAG = "NexxooWebservice";
	
	private static final String URL = "https://www.appstock.de/kiosk/ws/index.php";
	private static final String BASICAUTH = "nexxoo:wenexxoo4kiosk!";
	private static final String TOKEN = "";
	
	/**
	 * Content type for allowing all types in response
	 */
	public static final int CONTENTFILTER_ALL = 0;
	/**
	 * Content type for allowing only apps in response
	 */
	public static final int CONTENTFILTER_APPS = 1;
	/**
	 * Content type for allowing only magazines in response
	 */
	public static final int CONTENTFILTER_MAGAZINES = 2;
	/**
	 * Content type for allowing only videos in response
	 */
	public static final int CONTENTFILTER_VIDEOS = 3;
	
	/**
	 * Constant for requesting all content, regardless of what price
	 */
	public static final int CONTENTPRICE_ALL = 0;
	/**
	 * Constant for only requesting free content
	 */
	public static final int CONTENTPRICE_FREE = 1;
	/**
	 * Constant for only requesting content that needs to be purchased
	 */
	public static final int CONTENTPRICE_BUY = 2;
	
	public static final long CATEGORYFILTER_ALL = 0;
	
	/**
	 * Constant showing that the user made no like\dislike yet.
	 */
	public static final int CONTENT_LIKE_NONE = 0;
	/**
	 * Constant showing user liked the content.
	 */
	public static final int CONTENT_LIKE_YES = 1;
	/**
	 * Constant showing user disliked the content.
	 */
	public static final int CONTENT_LIKE_NO = -1;
	
	/* Constants for webservice tasks */
	
	protected static final int WEBTASK_REGISTERACCOUNT = 5;
	protected static final int WEBTASK_LOGINACCOUNT = 6;
	protected static final int WEBTASK_VALIDATE_ACCOUNT_USERNAME = 7;
	protected static final int WEBTASK_VALIDATE_ACCOUNT_EMAIL = 8;
	protected static final int WEBTASK_VALIDATE_ACCOUNT_ALL = 9;
	protected static final int WEBTASK_UPDATEACCOUNT = 10;
	protected static final int WEBTASK_GETCATEGORIES = 11;
	protected static final int WEBTASK_GETCONTENT = 12;
	protected static final int WEBTASK_GETOWNEDCONTENT = 13;
	protected static final int WEBTASK_GETCOMMENTS = 14;
	protected static final int WEBTASK_GETDOWNLOAD = 15;
	protected static final int WEBTASK_LOGOUTACCOUNT = 16;
	protected static final int WEBTASK_VALIDATE_PASSWORD = 17;
	protected static final int WEBTASK_GETWISHLIST = 18;
	protected static final int WEBTASK_ADDTOWISHLIST = 19;
	protected static final int WEBTASK_REMOVEFROMWISHLIST = 20;
	protected static final int WEBTASK_SEARCH = 21;
	protected static final int WEBTASK_BUY = 22;
	protected static final int WEBTASK_REVIEWCONTENT = 23;
	protected static final int WEBTASK_CANBUY = 24;
	protected static final int WEBTASK_FORGOTPASSWORD = 25;
	protected static final int WEBTASK_REFRESHACCOUNT = 26;
	protected static final int WEBTASK_UPGRADEACCOUNT = 27;
	protected static final int WEBTASK_GETPRICEFORDEVACCOUNT = 28;
	protected static final int WEBTASK_GETLASTESTVERSIONCODE = 30;
	public static final int WEBTASK_GETUPDATE = 31;
	
	/* Constants for parameter identifier */
	private static final String PARA_USERNAME = "username";
	private static final String PARA_PASSWORD = "password";
	private static final String PARA_EMAIL = "email";
	private static final String PARA_ACCOUNTID = "accid";
	private static final String PARA_SESSIONKEY = "sessionkey";
	private static final String PARA_CONTENTID = "contentid";
	private static final String PARA_FIRSTNAME = "firstname";
	private static final String PARA_LASTNAME = "lastname";
	private static final String PARA_STREET = "street";
	private static final String PARA_HOUSENUMBER = "housenumber";
	private static final String PARA_CITY = "city";
	private static final String PARA_ZIP = "zip";
	private static final String PARA_BIRTHDATE = "dateofbirth";
	private static final String PARA_STARTCOUNT = "start";
	private static final String PARA_ENDCOUNT = "end";
	private static final String PARA_CONTENTTYPE = "type";
	private static final String PARA_QUORUM = "quorum";
	private static final String PARA_DEVICEID = "deviceid";
	private static final String PARA_PRICETYPE = "pricetype";
	private static final String PARA_STATUS = "status";
	private static final String PARA_CATEGORYTYPE = "catfilter";
	private static final String PARA_QUERY = "query";
	private static final String PARA_ISFEATURED = "featured";
	private static final String PARA_ISSPECIALDEAL = "sdeal";
	private static final String PARA_LIKEDISLIKE = "likes";
	private static final String PARA_COMMENT = "comment";
	private static final String PARA_MESSAGE = "msg";
	private static final String PARA_IBAN = "iban";
	private static final String PARA_BIC = "bic";
	private static final String PARA_OWNERNAME = "ownername";
	private static final String PARA_PAYPALRESPONSE = "ppjson";
	private static final String PARA_IP = "ip";
	
	/**
	 * add two params for register
	 */
	private static final String PARA_TERMS = "terms";
	private static final String PARA_RECALL = "recall";
	
	private static List<DownloadAsyncTask> activeDownloads;
	
	
	private static void doWebCall(int task, List<NameValuePair> additionalParams, final OnJSONResponse callback, boolean async){
		String basicAuthBase64 = null;
		if (BASICAUTH != null) {
			basicAuthBase64 =  Base64.encodeToString(BASICAUTH.getBytes(), Base64.NO_WRAP);
//			Log.e("BASIC", basicAuthBase64);
		}
		
		if (async){
			NexxooHttpJsonRequestAsyncTask asyncTask = 
					new NexxooHttpJsonRequestAsyncTask(URL, basicAuthBase64, task, TOKEN, additionalParams, new IWebServiceResponse() {
						
						@Override
						public void onReceivedResponse(String json) {
							if (json == null){
								callback.onReceivedError(JSONErrorHandler.getErrorDescriptionByErrorCode(JSONErrorHandler.WS_ERROR_UNKNOWN), JSONErrorHandler.WS_ERROR_UNKNOWN);
								return;
							}
//							Log.d(Nexxoo.TAG, "Response: " + json);
							try {
								JSONObject obj = new JSONObject(json);
								Log.d(Nexxoo.TAG, "JSON: " + json.toString());
								//check if result == 1
								int resultCode = obj.getInt("result");
								if (resultCode == 1)								
									callback.onReceivedJSONResponse(obj);
								else
									callback.onReceivedError(JSONErrorHandler.getErrorDescriptionByErrorCode(resultCode), resultCode);
							} catch (JSONException e) {
								callback.onReceivedError(JSONErrorHandler.getErrorDescriptionByErrorCode(JSONErrorHandler.WS_ERROR_UNKNOWN), JSONErrorHandler.WS_ERROR_UNKNOWN);
							} catch (NullPointerException e) {
								e.printStackTrace();
								if(callback != null)
									callback.onReceivedError(JSONErrorHandler.getErrorDescriptionByErrorCode(JSONErrorHandler.WS_ERROR_UNKNOWN), JSONErrorHandler.WS_ERROR_UNKNOWN);

							}
						}

						@Override
						public void onReceivedError(int code) {
							callback.onReceivedError(JSONErrorHandler.getErrorDescriptionByErrorCode(code), code);				
						}
					});
			asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null);
		} else {
			NexxooHttpJsonRequest request = new NexxooHttpJsonRequest();
			String json = request.performJsonRequest(URL, task, TOKEN, additionalParams);
			if (json != null){
				//check if we got an error
				if (json.startsWith("IOException")){
					callback.onReceivedError(JSONErrorHandler.getErrorDescriptionByErrorCode(JSONErrorHandler.WS_ERROR_IOEXCEPTION), 
							JSONErrorHandler.WS_ERROR_IOEXCEPTION);
				} else if (json.startsWith("ClientProtocolException")) {
					callback.onReceivedError(JSONErrorHandler.getErrorDescriptionByErrorCode(JSONErrorHandler.WS_ERROR_CLIENTPROTOCOLEXCEPTION), 
							JSONErrorHandler.WS_ERROR_CLIENTPROTOCOLEXCEPTION);
				} else {			
					//no error, try to parse JSON
					try {
						JSONObject obj = new JSONObject(json);
						//check if result == 1
						int resultCode = obj.getInt("result");
						if (resultCode == 1)								
							callback.onReceivedJSONResponse(obj);
						else
							callback.onReceivedError(JSONErrorHandler.getErrorDescriptionByErrorCode(resultCode), resultCode);
						
					} catch (JSONException e) {
						callback.onReceivedError(JSONErrorHandler.getErrorDescriptionByErrorCode(JSONErrorHandler.WS_ERROR_UNKNOWN), JSONErrorHandler.WS_ERROR_UNKNOWN);
					}
				}
			} else {
				callback.onReceivedError(JSONErrorHandler.getErrorDescriptionByErrorCode(JSONErrorHandler.WS_ERROR_UNKNOWN), JSONErrorHandler.WS_ERROR_UNKNOWN); 
			}			
		}
		
	}

	public static void getContent(boolean executeAsync, int startWith, int endWith, int
			contentType, OnJSONResponse callback){
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair(PARA_STARTCOUNT, Integer.toString(startWith)));
		params.add(new BasicNameValuePair(PARA_ENDCOUNT, Integer.toString(endWith)));
		params.add(new BasicNameValuePair(PARA_CONTENTTYPE, Integer.toString(contentType)));
		doWebCall(WEBTASK_GETCONTENT, params, callback, executeAsync);
	}
	
	private static void performDownload(Context ctx, BaseEntity content, List<NameValuePair>
			additionalParams, final OnDownloadResult callback){
		String basicAuthBase64 = null;
		if (BASICAUTH != null)
			basicAuthBase64 =  Base64.encodeToString(BASICAUTH.getBytes(), Base64.NO_WRAP);
		
		DownloadAsyncTask dlTask = new DownloadAsyncTask(URL, 
				basicAuthBase64, 
				WEBTASK_GETDOWNLOAD, 
				TOKEN, 
				additionalParams, 
				ctx, 
				content, 
				callback);			
		dlTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void[]) null);
		
		if(activeDownloads == null){
			activeDownloads = new ArrayList<DownloadAsyncTask>();
		}
		activeDownloads.add(dlTask);
	}
	
	/*public static void cancelAllDownlads(){
		for(DownloadAsyncTask task: activeDownloads){
			task.cancel(true);
		}
	}
	
	public static void cancelDownload(BaseEntity content){
		if(hasActiveDownloads()){
			for(DownloadAsyncTask task: activeDownloads){
				if(task.getContent().getId() == content.getId()){
					task.cancel(true);
				}
			}
		}
	}*/
	
	public static List<DownloadAsyncTask> getActiveDownloads(){
		return activeDownloads;
	}
	
	public static boolean hasActiveDownloads() {
		if(activeDownloads == null) return false;
		return activeDownloads.size() != 0;
	}
	
	public static boolean isDownloading(Activity activity, BaseEntity content) {
		if(hasActiveDownloads()) {
			ListIterator<DownloadAsyncTask> li = activeDownloads.listIterator(activeDownloads.size());

			while(li.hasPrevious()) {
				DownloadAsyncTask task = li.previous();
				if(task.getContent().getContentId() == content.getContentId() && !task
						.isCancelled()){
					return true;
				}
			}
		}
		return false;
	}
	
	/*public static boolean reattachDownloadProgress(Activity activity, BaseEntity content){
		*//**
		 * iteration direction was reversed
		 * sometimes old (canceled) download task will not be removed
		 * from activeDownloads list. As a result it will be first on the list
		 * this code (due to return true) will only catch first (in this case canceled)
		 * item. Reversing order fixed this issue.
		 *//*
		if(hasActiveDownloads()) {
			ListIterator<DownloadAsyncTask> li = activeDownloads.listIterator(activeDownloads.size());
			// Iterate in reverse.
			while(li.hasPrevious()) {
				DownloadAsyncTask task = li.previous();
				if(task.getContent().getId() == content.getId() && !task.isCancelled()){
					Log.e(TAG, "Found active download for " + task.getContent().getName());
					task.setCallback(FileStorageHelper.getCallback(activity, content, true));
					return true;
				}
			}
		}
		return false;
	}*/
	
	/*private static void getContent(boolean executeAsync, int startWith, int endWith, int contentType,
			int priceType, long categoryFilter, boolean isFeatured, boolean isSpecialDeal, OnJSONResponse callback){
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair(PARA_STARTCOUNT, Integer.toString(startWith)));
		params.add(new BasicNameValuePair(PARA_ENDCOUNT, Integer.toString(endWith)));
		params.add(new BasicNameValuePair(PARA_CONTENTTYPE, Integer.toString(contentType)));
		params.add(new BasicNameValuePair(PARA_PRICETYPE, Integer.toString(priceType)));
		params.add(new BasicNameValuePair(PARA_CATEGORYTYPE, Long.toString(categoryFilter)));
		params.add(new BasicNameValuePair(PARA_STATUS, Integer.toString(2))); //we only need to get published content
		params.add(new BasicNameValuePair(PARA_ISFEATURED, Boolean.toString(isFeatured)));
		params.add(new BasicNameValuePair(PARA_ISSPECIALDEAL, Boolean.toString(isSpecialDeal)));
		doWebCall(WEBTASK_GETCONTENT, params, callback, executeAsync);
	}*/

	/**
	 * Get the download link of a content. Webservice does checking if account has the right to download the requested content.
	 * @param executeAsync Execute webcall async.
	 * @param callback Event called on response.
	 */
	public static void getDownload(boolean executeAsync, Context context,
			BaseEntity content,  OnDownloadResult callback){
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair(PARA_CONTENTID, Long.toString(content
				.getContentId())));

		performDownload(context, content, params, callback);
	}

	/**
	 * Searches the database for the given query. Search contains fields "name" of a content like %query%.
	 * @param executeAsync Execute webcall async.
	 * @param searchText The query.
	 * @param callback Event called on response.
	 */
	public static void searchForContent(boolean executeAsync, String searchText, OnJSONResponse callback){
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair(PARA_QUERY, searchText));
		doWebCall(WEBTASK_SEARCH, params, callback, executeAsync);
	}
}
