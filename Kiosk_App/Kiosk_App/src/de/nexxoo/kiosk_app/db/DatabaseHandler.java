package de.nexxoo.kiosk_app.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler extends SQLiteOpenHelper {

	private static final int DATABASE_VERSION = 1;

	// Database Name
	private static final String DATABASE_NAME = "kiosk";
	// Contacts table name
	private static final String TABLE_CONTENTS = "contents";
	// Contacts Table Columns names
	private static final String KEY_ID = "_id";
	private static final String KEY_CONTENT_ID = "contentid";
	// data type
	private static final String INTEGER_TYPE = " INTEGER";
	private static final String COMMA_SEP = ",";

	public DatabaseHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	// Creating Tables
	@Override
	public void onCreate(SQLiteDatabase db) {
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

	public void addContent(int id) {
		SQLiteDatabase db = this.getWritableDatabase();

		if (!isContentExist(id)) {
			ContentValues values = new ContentValues();
			values.put(KEY_CONTENT_ID, id);
			db.insert(TABLE_CONTENTS, null, values);
			db.close();
		}

	}

	public List<Integer> getAllContents() {
		List<Integer> contentList = new ArrayList<Integer>();
		String selectQuery = "SELECT  * FROM " + TABLE_CONTENTS;

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				if (!cursor.isNull(0))
					contentList.add(cursor.getInt(0));
			} while (cursor.moveToNext());
		}

		return contentList;
	}

	// Updating single content
	public int updateContent(Content content) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_CONTENT_ID, content.getContentid());
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


	public Boolean isContentExist(int id) {
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.query(TABLE_CONTENTS, new String[]{
						KEY_CONTENT_ID}, KEY_CONTENT_ID + "=?",
				new String[]{Integer.toString(id)}, null, null, null, null);
		if (cursor != null)
			return true;
		else
			return false;

	}

}
