package de.nexxoo.kiosk_app.tools;

import android.content.Context;
import android.net.Uri;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by b.yuan on 17.08.2015.
 */
public class NetWorkImageLoader extends BaseImageDownloader {

	public NetWorkImageLoader(Context context) {
		super(context);
	}

	@Override
	protected InputStream getStreamFromNetwork(String imageUri, Object extra) throws IOException {
		return super.getStreamFromNetwork(imageUri, extra);
	}

	@Override
	protected HttpURLConnection createConnection(String url, Object extra) throws IOException {
		String encodedUrl = Uri.encode(url, ALLOWED_URI_CHARS);
		HttpURLConnection conn = (HttpURLConnection) new URL(encodedUrl).openConnection();
		conn.setRequestProperty("User-Agent","nexxoo");
		conn.setDoInput(true);
		conn.setDoOutput(true);
		conn.setConnectTimeout(connectTimeout);
		conn.setReadTimeout(readTimeout);
		return conn;
//		return super.createConnection(url, extra);
	}
}
