package com.greenkit.smart.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

import com.greenkit.smart.database.table.BookTable;
import com.greenkit.smart.database.table.PlanTable;
import com.greenkit.smart.database.table.WordTable;
import com.greenkit.smart.datatype.Word;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "smart_dict.db";
    public static final int DATABASE_VERSION = 1;

    private static DatabaseHelper sDatabaseHelper;

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
