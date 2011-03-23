package com.greenkit.smart.database;

import com.greenkit.smart.datatable.BookTable;
import com.greenkit.smart.datatable.PlanTable;
import com.greenkit.smart.datatable.WordTable;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "smart_dict.db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context, String name, CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
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
    }
}
