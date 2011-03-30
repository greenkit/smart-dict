package com.greenkit.smart.database.table;

import android.database.sqlite.SQLiteDatabase;

public class WordTable extends Table{

    public static final String TABLE_NAME                = "vocabulary";
    public static final String COLUMN_NAME_WORD          = "word";
    public static final String COLUMN_NAME_SYMBOL        = "symbol";
    public static final String COLUMN_NAME_TRANSLATION   = "translation";
    public static final String COLUMN_NAME_PRONUNCIATION = "pronunciation";
    public static final String COLUMN_NAME_EXAMPLES      = "examples";
    public static final String COLUMN_NAME_BOOKS         = "books";
    public static final String COLUMN_NAME_STUDY_COUNT   = "study_count";
    public static final String COLUMN_NAME_MISTAKE_COUNT = "mistake_count";
    public static final String COLUMN_NAME_SEARCH_COUNT  = "search_count";
    public static final String COLUMN_NAME_LEVEL         = "level";

    public static void createTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE  IF NOT EXISTS " + TABLE_NAME + " ("
                + COLUMN_NAME_ID + " INTEGER PRIMARY KEY,"
                + COLUMN_NAME_WORD + " TEXT,"
                + COLUMN_NAME_SYMBOL + " TEXT,"
                + COLUMN_NAME_TRANSLATION + " TEXT,"
                + COLUMN_NAME_PRONUNCIATION + " TEXT,"
                + COLUMN_NAME_EXAMPLES + " TEXT,"
                + COLUMN_NAME_BOOKS + " TEXT,"
                + COLUMN_NAME_STUDY_COUNT + " INTEGER,"
                + COLUMN_NAME_MISTAKE_COUNT + " INTEGER,"
                + COLUMN_NAME_SEARCH_COUNT + " INTEGER,"
                + COLUMN_NAME_LEVEL + " INTEGER"
                + ");");
    }

    public static void dropTable(SQLiteDatabase db) {
        db.execSQL("DROP TABLE " + TABLE_NAME);
    }
}
