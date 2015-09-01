package de.nexxoo.kiosk_app;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import de.nexxoo.kiosk_app.db.DatabaseHandler;

import java.util.List;

/**
 * Created by b.yuan on 01.09.2015.
 */
public class TestDBActivity extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.testdb);

		DatabaseHandler db = new DatabaseHandler(this);

		db.addContent(4);
		db.addContent(11);
		db.addContent(5);
		db.addContent(6);
		db.addContent(7);

		List<Integer> contacts = db.getAllContents();

		for (int cn : contacts) {
			String log = "Id: " + cn;
			// Writing Contacts to log
			Log.d("Name: ", log);
		}
	}
}