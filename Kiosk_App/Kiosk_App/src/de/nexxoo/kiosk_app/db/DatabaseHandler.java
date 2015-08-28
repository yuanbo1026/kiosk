package de.nexxoo.kiosk_app.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler extends SQLiteOpenHelper {

	// All Static variables
	// Database Version
	private static final int DATABASE_VERSION = 1;

	// Database Name
	private static final String DATABASE_NAME = "kiosk";

	// Contacts table name
	private static final String TABLE_CONTENTS = "contents";

	// Contacts Table Columns names
	private static final String KEY_ID = "id";
	private static final String KEY_CONTENT_ID ="contentid";

	/*unused
	private static final String KEY_NAME = "name";
	private static final String KEY_URL = "url";
	private static final String KEY_DESCRIPTION = "description";
	private static final String KEY_FILENAME = "filename";
	private static final String KEY_CATEGORY = "category";*/


	// data type
	private static final String TEXT_TYPE = " TEXT";
	private static final String INTEGER_TYPE = " INTEGER";
	private static final String COMMA_SEP = ",";

	public DatabaseHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	// Creating Tables
	@Override
	public void onCreate(SQLiteDatabase db) {
		/*String CREATE_CONTENTS_TABLE =
				"CREATE TABLE "
						+ TABLE_CONTENTS + "("
						+ KEY_ID + " TEXT PRIMARY KEY,"
						+ KEY_CONTENT_ID + INTEGER_TYPE + COMMA_SEP
						+ KEY_DESCRIPTION + TEXT_TYPE + COMMA_SEP
						+ KEY_FILENAME + TEXT_TYPE + COMMA_SEP
						+ KEY_CATEGORY + INTEGER_TYPE
						+ ")";*/
		String CREATE_CONTENTS_TABLE =
				"CREATE TABLE "
						+ TABLE_CONTENTS + "("
						+ KEY_ID + " TEXT PRIMARY KEY" + COMMA_SEP
						+ KEY_CONTENT_ID + INTEGER_TYPE + ")";
		db.execSQL(CREATE_CONTENTS_TABLE);
	}

	// Upgrading database
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Drop older table if existed
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTENTS);
		// Create tables again
		onCreate(db);
	}

	/**
	 * All CRUD(Create, Read, Update, Delete) Operations
	 */

	// Adding new CONTENT
	public void addContent(Content content) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_CONTENT_ID, content.getContentid());
		/*values.put(KEY_URL, content.getUrl());
		values.put(KEY_DESCRIPTION, content.getDescription());
		values.put(KEY_FILENAME, content.getFilename());
		values.put(KEY_CATEGORY, content.getCategory());*/

		// Inserting Row
		db.insert(TABLE_CONTENTS, null, values);
//		db.insertOrThrow(TABLE_CONTENTS, null, values);
		db.close(); // Closing database connection
	}

	// Getting single content
	/*public Content getContent(String name) {
		SQLiteDatabase db = this.getReadableDatabase();
		Content content;
		Cursor cursor = db.query(TABLE_CONTENTS, new String[]{
						KEY_NAME,
						KEY_URL,
						KEY_DESCRIPTION,
						KEY_FILENAME,
						KEY_CATEGORY}, KEY_NAME + "=?",
				new String[]{name}, null, null, null, null);

		if (cursor.getCount()>0) {
			cursor.moveToFirst();
			content = new Content(
					cursor.getString(0),
					cursor.getString(1),
					cursor.getString(2),
					cursor.getString(3),
					Integer.parseInt(cursor.getString(4)));
			cursor.close();
			db.close();
			return content;
		} else{
			db.close();
			return null;
		}
		// return content

	}*/

	// Getting All Contents
	public List<Content> getAllContents() {
		List<Content> contentList = new ArrayList<Content>();
		// Select All Query
		String selectQuery = "SELECT  * FROM " + TABLE_CONTENTS;

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				Content content = new Content();
				content.setContentid(cursor.getInt(0));
				/*content.setName(cursor.getString(0));
				content.setUrl(cursor.getString(1));
				content.setDescription(cursor.getString(2));
				content.setFilename(cursor.getString(3));
				content.setCategory(cursor.getInt(4));*/

				// Adding content to list
				contentList.add(content);
			} while (cursor.moveToNext());
		}

		// return content list
		return contentList;
	}

	// Updating single content
	public int updateContent(Content content) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_CONTENT_ID, content.getContentid());
		/*values.put(KEY_DESCRIPTION, content.getDescription());
		values.put(KEY_URL, content.getUrl());
		values.put(KEY_FILENAME, content.getFilename());
		values.put(KEY_CATEGORY, content.getCategory());*/

		// updating row
		return db.update(TABLE_CONTENTS, values, KEY_CONTENT_ID + " = ?",
				new String[]{String.valueOf(content.getContentid())});
	}

	// Deleting single content
	public void deleteContent(Content content) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_CONTENTS, KEY_CONTENT_ID + " = ?",
				new String[]{String.valueOf(content.getContentid())});
		db.close();
	}


	public Boolean isContextExist(String name) {
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.query(TABLE_CONTENTS, new String[]{
						KEY_CONTENT_ID}, KEY_CONTENT_ID + "=?",
				new String[]{name}, null, null, null, null);
		if (cursor != null)
			return true;
		else
			return false;

	}

}
