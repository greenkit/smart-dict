package com.greenkit.smart.database.table;

import android.database.sqlite.SQLiteDatabase;

public class PlanTable extends Table {

    public static final String TABLE_NAME                  = "plan";
    public static final String COLUMN_NAME_BOOKS           = "books";
    public static final String COLUMN_NAME_MODE            = "mode";
    public static final String COLUMN_NAME_CHAPTER_NUMBER  = "chapter_number";
    public static final String COLUMN_NAME_CURRENT_CHAPTER = "current_chapter";

    public static void createTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE  IF NOT EXISTS " + TABLE_NAME + " ("
                + COLUMN_NAME_ID + " INTEGER PRIMARY KEY,"
                + COLUMN_NAME_BOOKS + " TEXT,"
                + COLUMN_NAME_MODE + " INTEGER,"
                + COLUMN_NAME_CHAPTER_NUMBER + " INTEGER,"
                + COLUMN_NAME_CURRENT_CHAPTER + " INTEGER"
                + ");");
    }

    public static void dropTable(SQLiteDatabase db) {
        db.execSQL("DROP TABLE " + TABLE_NAME);
    }
}
