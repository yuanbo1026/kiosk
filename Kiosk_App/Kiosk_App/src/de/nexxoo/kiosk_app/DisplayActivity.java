package de.nexxoo.kiosk_app;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;

/**
 * Created by clive on 3/25/2014.
 *
 *      www.101apps.co.za
 */
public class DisplayActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.display);

        String uri = getIntent().getStringExtra("uri");

        Bitmap bitmap = BitmapFactory.decodeFile(uri);

        ImageView imageView = (ImageView) findViewById(R.id.imageView);
        imageView.setImageBitmap(bitmap);
    }
}
