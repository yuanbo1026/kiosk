package de.nexxoo.kiosk_app.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import de.nexxoo.kiosk_app.entity.BaseEntity;
import de.nexxoo.kiosk_app.entity.Manual;
import de.nexxoo.kiosk_app.entity.Video;

import java.util.ArrayList;
import java.util.List;

public class ContentDBHelper extends SQLiteOpenHelper {

	private static final int DATABASE_VERSION = 1;

	private static final String DATABASE_NAME = "kioskDB";
	private static final String TABLE_CONTENTS = "contents";

	private static final String KEY_ID = "id";
	public static String CONTENTID = "contentid";
	public static String NAME = "name";
	public static String FILENAME = "filename";
	public static String SIZE = "size";
	public static String CONTENTTYPE = "contenttypeid";
	public static String COVERIMAGENAME = "coverimage";
	public static String DURATION = "duration";
	public static String PAGES = "pages";

	public ContentDBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	// Creating Tables
	@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_CONTENTS + "("
				+ KEY_ID + " INTEGER PRIMARY KEY,"
				+ CONTENTID + " INTEGER,"
				+ NAME + " INTEGER,"
				+ FILENAME + " TEXT," +
				SIZE + " INTEGER," +
				CONTENTTYPE + " INTEGER," +
				COVERIMAGENAME + " TEXT," +
				DURATION + " INTEGER," +
				PAGES + " INTEGER" + ")";
		db.execSQL(CREATE_CONTACTS_TABLE);
	}

	// Upgrading database
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTENTS);
		onCreate(db);
	}

	/**
	 * All CRUD(Create, Read, Update, Delete) Operations
	 */

	// Adding new contact
	public void addContact(BaseEntity content) {
		SQLiteDatabase db = this.getWritableDatabase();
		if (isContentExist(content.getContentId())) {
			updateContact(content);
		} else {
			db.insert(TABLE_CONTENTS, null, convertContentIntoValues(content));
		}


		db.close();
	}


	// Getting single contact
	public Content getContent(int id) {
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.query(TABLE_CONTENTS, new String[]{
						CONTENTID}, CONTENTID + "=?",
				new String[]{String.valueOf(id)}, null, null, null, null);
		if (cursor != null) {
			cursor.moveToFirst();
			return convertCursorToContent(cursor);
		} else
			return null;

	}

	// Getting All Contacts
	public List<Content> getAllContacts() {
		List<Content> contactList = new ArrayList<Content>();
		// Select All Query
		String selectQuery = "SELECT  * FROM " + TABLE_CONTENTS;

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		if (cursor.moveToFirst()) {
			do {
				contactList.add(convertCursorToContent(cursor));
			} while (cursor.moveToNext());
		}

		return contactList;
	}

	private Content convertCursorToContent(Cursor cursor) {
		Content content = new Content();
		content.setContentId(cursor.getInt(1));
		content.setName(cursor.getString(2));
		content.setFileName(cursor.getString(3));
		content.setSize(cursor.getInt(4));
		content.setCoverImageName(cursor.getString(5));
		content.setContentTypeId(cursor.getInt(6));
		content.setPages(cursor.getInt(7));
		return content;
	}

	//	 Updating single contact
	public int updateContact(BaseEntity content) {
		SQLiteDatabase db = this.getWritableDatabase();

		return db.update(TABLE_CONTENTS, convertContentIntoValues(content), CONTENTID + " = ?",
				new String[]{String.valueOf(content.getContentId())});
	}

	private ContentValues convertContentIntoValues(BaseEntity content) {
		ContentValues values = new ContentValues();
		values.put(CONTENTID, content.getContentId());
		values.put(NAME, content.getName());
		values.put(FILENAME, content.getFileName());
		values.put(SIZE, content.getSize());
		values.put(COVERIMAGENAME, content.getName() + ".jpg");
		values.put(CONTENTTYPE, content.getContentTypeId());
		if (content.getContentTypeId() < 3) {
			Manual manual = (Manual) content;
			values.put(PAGES, manual.getPages());
		}
		if (content.getContentTypeId() == 3) {
			Video video = (Video) content;
			values.put(DURATION, video.getDuration());
		}

		return values;
	}

	// Deleting single contact
	public void deleteContent(int id) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_CONTENTS, CONTENTID + " = ?",
				new String[]{String.valueOf(id)});
		db.close();
	}


	// Getting contacts Count
	public int getContentsCount() {
		String countQuery = "SELECT  * FROM " + TABLE_CONTENTS;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);
		int count = cursor.getCount();
		cursor.close();

		return count;
	}

	public boolean isContentExist(int contentid){
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(TABLE_CONTENTS, new String[]{CONTENTID}, CONTENTID + "=?",
				new String[]{String.valueOf(contentid)}, null, null, null, null);
		int count = cursor.getCount();
		cursor.close();
		return count>0;
	}

}
