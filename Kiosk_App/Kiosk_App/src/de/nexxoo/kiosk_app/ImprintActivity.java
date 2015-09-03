package de.nexxoo.kiosk_app;

import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;
import de.nexxoo.kiosk_app.tools.Nexxoo;

/**
 * Created by b.yuan on 05.08.2015.
 */
public class ImprintActivity extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		getActionBar().setDisplayHomeAsUpEnabled(false);
		getActionBar().setHomeButtonEnabled(true);
		getActionBar().setDisplayUseLogoEnabled(false);
		getActionBar().setIcon(R.drawable.ic_chevron_left_white_36dp);
		getActionBar().setTitle(Nexxoo.getStyledText(this, "Impressum"));
		super.onCreate(savedInstanceState);
		setContentView(R.layout.imprint);
	}

	@Override
	public boolean onOptionsItemSelected (MenuItem menuItem){
		switch (menuItem.getItemId()) {
			case android.R.id.home:
				finish();
		}
		return (super.onOptionsItemSelected(menuItem));
	}
}