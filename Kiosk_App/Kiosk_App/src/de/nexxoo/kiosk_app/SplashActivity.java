package de.nexxoo.kiosk_app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

/**
 * Created by b.yuan on 18.08.2015.
 */
public class SplashActivity extends Activity {

	private Context mContext;
	private static int SPLASH_TIME_OUT = 2000;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);
		mContext = this;
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				Intent i = new Intent(mContext, MainActivity.class);
				startActivity(i);
				finish();
			}
		}, SPLASH_TIME_OUT);
	}

}