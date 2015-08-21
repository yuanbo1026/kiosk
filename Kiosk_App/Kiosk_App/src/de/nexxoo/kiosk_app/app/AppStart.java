package de.nexxoo.kiosk_app.app;

import android.app.Application;
import android.content.res.Configuration;
import android.util.Log;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import de.nexxoo.kiosk_app.tools.Global;
import de.nexxoo.kiosk_app.tools.Nexxoo;

public class AppStart extends Application {

	@Override
	public void onCreate() {


		if (!ImageLoader.getInstance().isInited()) {
			DisplayImageOptions options = new DisplayImageOptions.Builder()
					.cacheInMemory(true)
					.resetViewBeforeLoading(true)
					.build();

			ImageLoaderConfiguration imgCfg = new ImageLoaderConfiguration.Builder(getApplicationContext())
					.defaultDisplayImageOptions(options)
					.build();

			ImageLoader.getInstance().init(imgCfg);
		}


		super.onCreate();

		/**
		 * determine device resolution
		 */

		int screenSize = getResources().getConfiguration().screenLayout &
				Configuration.SCREENLAYOUT_SIZE_MASK;

		String toastMsg;
		switch (screenSize) {
			case Configuration.SCREENLAYOUT_SIZE_LARGE:
				toastMsg = "Large screen";
				break;
			case Configuration.SCREENLAYOUT_SIZE_NORMAL:
				toastMsg = "Normal screen";
				Global.isNormalScreenSize = true;
				break;
			case Configuration.SCREENLAYOUT_SIZE_SMALL:
				toastMsg = "Small screen";
				break;
			case Configuration.SCREENLAYOUT_SIZE_XLARGE:
				toastMsg = "XLarge screen";
				break;
			default:
				toastMsg = "Unknown screen";
		}
//		nuke();
		Log.d(Nexxoo.TAG, toastMsg+"-"+screenSize);
	}

	/*public void nuke() {
		try {
			TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
				public X509Certificate[] getAcceptedIssuers() {
					X509Certificate[] myTrustedAnchors = new X509Certificate[0];
					return myTrustedAnchors;
				}

				@Override
				public void checkClientTrusted(X509Certificate[] certs, String authType) {
				}

				@Override
				public void checkServerTrusted(X509Certificate[] certs, String authType) {
				}
			}};

			SSLContext sc = SSLContext.getInstance("SSL");
			sc.init(null, trustAllCerts, new SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
			HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
				@Override
				public boolean verify(String arg0, SSLSession arg1) {
					return true;
				}
			});
		} catch (Exception e) {
		}
	}*/

}