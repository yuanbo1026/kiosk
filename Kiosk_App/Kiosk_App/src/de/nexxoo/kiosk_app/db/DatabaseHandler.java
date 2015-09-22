package de.nexxoo.kiosk_app.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import de.nexxoo.kiosk_app.tools.FileStorageHelper;

import java.io.*;
import java.util.ArrayList;
import java.util.List;


public class DatabaseHandler extends SQLiteOpenHelper {

	private static final int DATABASE_VERSION = 1;
	private static String DB_PATH;
	private static final String DATABASE_NAME = "content.db";
	private static final String TABLE_CONTENTS = "contents";
	private static final String KEY_ID = "id";

	public static String CONTENTID = "contentid";
	public static String NAME = "name";
	public static String DESCRIPTION = "description";
	public static String FILENAME = "filename";
	public static String SIZE = "size";
	public static String CONTENTTYPE = "contenttypeid";
	public static String COVERIMAGENAME = "coverimage";
	public static String DURATION = "duration";
	public static String PAGES = "pages";

	private Context mContext;

	private SQLiteDatabase myDataBase;
	private FileStorageHelper mFileStorageHelper;

	public DatabaseHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		mContext = context;
		mFileStorageHelper = new FileStorageHelper(context);
		DB_PATH = mFileStorageHelper.getDBFolder();
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_CONTENTS_TABLE = "CREATE TABLE " + TABLE_CONTENTS + "(" +
				KEY_ID + " INTEGER PRIMARY KEY," +
				CONTENTID + " INTEGER," +
				NAME + " TEXT," +
				DESCRIPTION + " TEXT," +
				FILENAME + " TEXT," +
				SIZE + " INTEGER," +
				CONTENTTYPE + " INTEGER," +
				COVERIMAGENAME + " TEXT," +
				DURATION + " INTEGER," +
				PAGES + " INTEGER" +
				")";


		db.execSQL(CREATE_CONTENTS_TABLE);
	}

	/**
	 * Creates a empty database on the system and rewrites it with your own database.
	 */
	public void createDataBase() throws IOException {

		boolean dbExist = checkDataBase();
		if (dbExist) {
			this.getReadableDatabase();
		}

	}

	/**
	 * Check if the database already exist to avoid re-copying the file each time you open the application.
	 *
	 * @return true if it exists, false if it doesn't
	 */
	private boolean checkDataBase() {

		String myPath = DB_PATH + DATABASE_NAME;
		return new File(myPath).exists();
	}

	public void openDataBase() throws SQLException {

		//Open the database
		String myPath = DB_PATH + DATABASE_NAME;
		myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTENTS);
		onCreate(db);
	}

	/**
	 * All CRUD(Create, Read, Update, Delete) Operations
	 */

	// Adding new contact
	public void addContent(int id, String name, String filename, int
			size, String coverimage, int contenttypeid, int duration, int pages) {
		if (!isContentExist(id)) {
			SQLiteDatabase db = this.getWritableDatabase();

			ContentValues values = new ContentValues();
			values.put(CONTENTID, id);
			values.put(NAME, name);
			values.put(FILENAME, filename);
			values.put(SIZE, size);
			values.put(COVERIMAGENAME, coverimage);
			values.put(CONTENTTYPE, contenttypeid);
			if (duration > 0)
				values.put(DURATION, duration);
			if (pages > 0)
				values.put(PAGES, pages);

			// Inserting Row
			db.insert(TABLE_CONTENTS, null, values);
			db.close(); // Closing database connection
		}

	}

	// Getting All Contents
	public List<Integer> getAllContents() {
		List<Integer> contactList = new ArrayList<Integer>();
		// Select All Query
		String selectQuery = "SELECT  * FROM " + TABLE_CONTENTS;

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				// Adding contact to list
				contactList.add(cursor.getInt(0));
			} while (cursor.moveToNext());
		}

		// return contact list
		return contactList;
	}


	// Getting contacts Count
	public int getContentsCount() {
		String countQuery = "SELECT  * FROM " + TABLE_CONTENTS;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);
		cursor.close();

		// return count
		return cursor.getCount();
	}

	public boolean isContentExist(int id) {
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.query(TABLE_CONTENTS, new String[]{
						CONTENTID}, CONTENTID + "=?",
				new String[]{String.valueOf(id)}, null, null, null, null);
		if (cursor != null)
			return true;
		else
			return false;


	}

}
