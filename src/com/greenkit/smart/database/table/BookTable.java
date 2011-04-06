package com.greenkit.smart.database.table;

import android.database.sqlite.SQLiteDatabase;

public class BookTable extends Table {

    public static final String TABLE_NAME            = "book";
    public static final String COLUMN_NAME_BOOK_NAME = "book_name";
    public static final String COLUMN_NAME_WORD_NUMBER    = "word_number";

    public static void createTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE  IF NOT EXISTS " + TABLE_NAME + " ("
                + COLUMN_NAME_ID + " INTEGER PRIMARY KEY,"
                + COLUMN_NAME_BOOK_NAME + " TEXT,"
                + COLUMN_NAME_WORD_NUMBER + " INTEGER"
                + ");");
    }

    public static void dropTable(SQLiteDatabase db) {
        db.execSQL("DROP TABLE " + TABLE_NAME);
    }
}
