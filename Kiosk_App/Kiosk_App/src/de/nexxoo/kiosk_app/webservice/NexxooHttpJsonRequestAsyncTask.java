package de.nexxoo.kiosk_app.webservice;

import android.os.AsyncTask;
import de.nexxoo.kiosk_app.webservice.error.JSONErrorHandler;
import org.apache.http.NameValuePair;

import java.util.List;

public class NexxooHttpJsonRequestAsyncTask extends AsyncTask<Void, Integer, String> {

	private String mWebservice;
	private String mBasicAuth;
	private IWebServiceResponse mCallback;
	
	private int mTask;
	private String mToken;
	private List<NameValuePair> mAdditionalParams;
	
	public NexxooHttpJsonRequestAsyncTask(String webservice, String basicAuth,
			int task, String token, List<NameValuePair> additionalParams,
			IWebServiceResponse callback){
		mWebservice = webservice;
		mCallback = callback;
		mBasicAuth = basicAuth;
		
		mTask = task;
		mToken = token;
		mAdditionalParams = additionalParams;
	}
	
	@Override
	protected String doInBackground(Void... params) {
		NexxooHttpJsonRequest request = new NexxooHttpJsonRequest(mBasicAuth);		
		return request.performJsonRequest(mWebservice, mTask, mToken, mAdditionalParams);
	}

	@Override
	protected void onPostExecute(String result) {
		if (result != null){
			if (result.startsWith("IOException")){
				mCallback.onReceivedError(JSONErrorHandler.WS_ERROR_IOEXCEPTION);
			} else if (result.startsWith("ClientProtocolException")) {
				mCallback.onReceivedError(JSONErrorHandler.WS_ERROR_CLIENTPROTOCOLEXCEPTION);
			} else {
				mCallback.onReceivedResponse(result);
			}
		}			
		else
			mCallback.onReceivedError(JSONErrorHandler.WS_ERROR_UNKNOWN);
	}

}
