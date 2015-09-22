package de.nexxoo.kiosk_app;

import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import de.nexxoo.kiosk_app.tools.Misc;
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
		getActionBar().setIcon(R.drawable.ic_arrow_back);
		getActionBar().setTitle(Nexxoo.getStyledText(this, "Impressum"));
		super.onCreate(savedInstanceState);
		setContentView(R.layout.imprint);

		TextView title = (TextView) findViewById(R.id.imprint_title);
		TextView content = (TextView) findViewById(R.id.imprint_content);

		title.setTypeface(Misc.getCustomFont(this,Misc.FONT_BOLD ));
		content.setTypeface(Misc.getCustomFont(this, Misc.FONT_NORMAL));
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