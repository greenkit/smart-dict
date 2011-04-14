package com.greenkit.smart.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

import com.greenkit.smart.database.table.BookTable;
import com.greenkit.smart.database.table.PlanTable;
import com.greenkit.smart.database.table.WordTable;
import com.greenkit.smart.datatype.Word;
import com.greenkit.smart.utils.Utils;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "smart_dict.db";
    public static final int DATABASE_VERSION = 1;

    private static final String[] WORD_TABLE_COLUMNS_ALL = {
        WordTable.COLUMN_NAME_ID,
        WordTable.COLUMN_NAME_WORD,
        WordTable.COLUMN_NAME_SYMBOL,
        WordTable.COLUMN_NAME_TRANSLATION,
        WordTable.COLUMN_NAME_EXAMPLES,
        WordTable.COLUMN_NAME_LEVEL,
        WordTable.COLUMN_NAME_PRONUNCIATION,
        WordTable.COLUMN_NAME_BOOKS,
        WordTable.COLUMN_NAME_STUDY_COUNT,
        WordTable.COLUMN_NAME_SEARCH_COUNT,
        WordTable.COLUMN_NAME_MISTAKE_COUNT
    };

    private static final String[] WORD_TABLE_COLUMNS_LIST = {
        WordTable.COLUMN_NAME_ID,
        WordTable.COLUMN_NAME_WORD,
        WordTable.COLUMN_NAME_TRANSLATION,
        WordTable.COLUMN_NAME_STUDY_COUNT,
        WordTable.COLUMN_NAME_SEARCH_COUNT,
        WordTable.COLUMN_NAME_MISTAKE_COUNT,
        WordTable.COLUMN_NAME_LEVEL,
    };

    private static DatabaseHelper sDatabaseHelper;

    private SQLiteDatabase mWritableDatabase;

    private DatabaseHelper(Context context, String name, CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public synchronized static DatabaseHelper getInstance(Context context) {
        if(sDatabaseHelper == null) {
            sDatabaseHelper = new DatabaseHelper(context, DatabaseHelper.DATABASE_NAME,
                    null, DatabaseHelper.DATABASE_VERSION);
        }

        return sDatabaseHelper;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        WordTable.createTable(db);
        BookTable.createTable(db);
        PlanTable.createTable(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        WordTable.dropTable(db);
        BookTable.dropTable(db);
        PlanTable.dropTable(db);

        onCreate(db);
    }

    public int increaseWordSeachCountById (long id) {
        int count = 0;
        if (id >= 0) {
            SQLiteDatabase db = getWritableDatabase();
            count = getWordSeachCountById(id, db) + 1;
            ContentValues values = new ContentValues(1);
            values.put(WordTable.COLUMN_NAME_SEARCH_COUNT, count);
            db.update(WordTable.TABLE_NAME, values, WordTable.COLUMN_NAME_ID + "=?",
                    new String[] { String.valueOf(id) });
            db.close();
        }

        return count;
    }

    private int getWordSeachCountById (long id, SQLiteDatabase db) {
        int count = 0;
        Cursor c = db.query(WordTable.TABLE_NAME, new String[] { WordTable.COLUMN_NAME_ID,
                WordTable.COLUMN_NAME_SEARCH_COUNT }, WordTable.COLUMN_NAME_ID + "=?",
                    new String[] { String.valueOf(id) }, null, null, null);

        if (c.moveToFirst()) {
            count = c.getInt(c.getColumnIndexOrThrow(WordTable.COLUMN_NAME_SEARCH_COUNT));
        }

        c.close();

        return count;
    }

    public Cursor queryWordDetailById(long id) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(WordTable.TABLE_NAME, WORD_TABLE_COLUMNS_ALL,
                WordTable.COLUMN_NAME_ID + "=?", new String[] { String.valueOf(id) },
                    null, null, null);

        return c;
    }

    public Cursor queryWordBySearchKey (String key) {
        String selection = null;
        String[] args = null;

        if (!Utils.isEmpty(key)) {
            selection = WordTable.COLUMN_NAME_WORD + " like ?";
            args = new String[] { key + "%" };
        }

        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(WordTable.TABLE_NAME, WORD_TABLE_COLUMNS_LIST,
                selection, args, null, null, WordTable.ORDER_BY, null);
        return c;
    }

    public void beginTransaction () {
        mWritableDatabase = getWritableDatabase();
        mWritableDatabase.beginTransaction();
    }

    public void setTransactionSuccessful () {
        if (mWritableDatabase != null) {
            mWritableDatabase.setTransactionSuccessful();
        }
    }

    public void endTransaction () {
        if (mWritableDatabase != null) {
            mWritableDatabase.endTransaction();
            mWritableDatabase.close();
            mWritableDatabase = null;
        }
    }

    public long insertWordInTransaction (Word word) {
        long id = -1;

        if (mWritableDatabase != null) {
            id = insertWord(word, mWritableDatabase);
        }

        return id;
    }

    public long insertWord(Word word) {
        SQLiteDatabase db = getWritableDatabase();
        long id;

        try {
            id = insertWord(word, db);
        } finally {
            if(db !=null) {
                db.close();
                db = null;
            }
        }

        return id;
    }

    public void insertWords(Word[] words) {
        SQLiteDatabase db = getWritableDatabase();
        try {
            db.beginTransaction();

            for(int i = 0; i < words.length; i++) {
                insertWord(words[i], db);
            }

            db.endTransaction();
        } finally {
            if(db != null) {
                db.close();
                db = null;
            }
        }
    }

    private long insertWord(Word word, SQLiteDatabase db) {
        ContentValues values = new ContentValues(10);
        values.put(WordTable.COLUMN_NAME_WORD, word.getWord());
        values.put(WordTable.COLUMN_NAME_SYMBOL, word.getSymbol());
        values.put(WordTable.COLUMN_NAME_TRANSLATION, word.getTranslation());
        values.put(WordTable.COLUMN_NAME_PRONUNCIATION, word.getPronunciation());
        values.put(WordTable.COLUMN_NAME_EXAMPLES, word.getExample());
        values.put(WordTable.COLUMN_NAME_BOOKS, word.getBookRef());
        values.put(WordTable.COLUMN_NAME_STUDY_COUNT, word.getStudyCount());
        values.put(WordTable.COLUMN_NAME_MISTAKE_COUNT, word.getMistakeCount());
        values.put(WordTable.COLUMN_NAME_SEARCH_COUNT, word.getSearchCount());
        values.put(WordTable.COLUMN_NAME_LEVEL, word.getLevel());
        return db.insertOrThrow(WordTable.TABLE_NAME, null, values);
    }
}
