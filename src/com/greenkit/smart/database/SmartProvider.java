package com.greenkit.smart.database;

import java.util.HashMap;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.greenkit.smart.database.table.Smart;
import com.greenkit.smart.database.table.Smart.Words;


public class SmartProvider extends ContentProvider {

    private static final String TAG = "SmartProvider";

    static final String DATABASE_NAME = "smart.db";
    private static final int DATABASE_VERSION = 2;
    private static final String WORDS_TABLE_NAME = "words";

    private static HashMap<String, String> sWordProjectionMap;
    private static final UriMatcher sUriMatcher;
    private static final int WORDS = 1;

    private DatabaseHelper mOpenHelper;

    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(Smart.AUTHORITY, "words", WORDS);

        sWordProjectionMap = new HashMap<String, String>();
        sWordProjectionMap.put(Words._ID, Words._ID);
        sWordProjectionMap.put(Words.NAME, Words.NAME);
        sWordProjectionMap.put(Words.TRANSLATION, Words.TRANSLATION);
        sWordProjectionMap.put(Words.SOUNDMARK, Words.SOUNDMARK);
        sWordProjectionMap.put(Words.PRONUNCIATION, Words.PRONUNCIATION);
        sWordProjectionMap.put(Words.EXAMPLE, Words.EXAMPLE);
    }

    @Override
    public boolean onCreate() {
    	mOpenHelper = new DatabaseHelper(getContext());
    	return true;
    }

    /**
     * This class helps open, create, and upgrade the database file.
     */
    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + WORDS_TABLE_NAME + " ("
                    + Words._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + Words.NAME + " TEXT,"
                    + Words.TRANSLATION + " TEXT,"
                    + Words.SOUNDMARK + " TEXT,"
                    + Words.PRONUNCIATION + " TEXT,"
                    + Words.EXAMPLE + " TEXT"
                    + ");");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS words");
            onCreate(db);
        }
    }

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int count;
        switch (sUriMatcher.match(uri)) {
        case WORDS:
        	count = db.delete(WORDS_TABLE_NAME, selection, selectionArgs);
        	break;

        default:
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
	}

	@Override
	public String getType(Uri uri) {
		switch (sUriMatcher.match(uri)) {
		case WORDS:
			return Words.CONTENT_ITEM_TYPE;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues initialValues) {
		if (sUriMatcher.match(uri) != WORDS) {
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		ContentValues values;
		if (initialValues != null) {
			values = new ContentValues(initialValues);
		} else {
			values = new ContentValues();
		}

		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		long id = db.insert(WORDS_TABLE_NAME, null, values);
		if (id > 0) {
			Uri wordUri = ContentUris.withAppendedId(Words.CONTENT_URI, id);
			getContext().getContentResolver().notifyChange(wordUri, null);
			return wordUri;
		}
		return null;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		qb.setTables(WORDS_TABLE_NAME);

		switch (sUriMatcher.match(uri)) {
		case WORDS:
			qb.setProjectionMap(sWordProjectionMap);
			break;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		String orderBy;
		if (TextUtils.isEmpty(sortOrder)) {
			orderBy = Words.DEFAULT_SORT_ORDER;
		} else {
			orderBy = sortOrder;
		}

        // Get the database and run the query
        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        Cursor c = qb.query(db, projection, selection, selectionArgs, null, null, orderBy);

        // Tell the cursor what uri to watch, so it knows when its source data changes
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int count;
        switch (sUriMatcher.match(uri)) {
        case WORDS:
        	count = db.update(WORDS_TABLE_NAME, values, selection, selectionArgs);
        	break;
        default:
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}

}
