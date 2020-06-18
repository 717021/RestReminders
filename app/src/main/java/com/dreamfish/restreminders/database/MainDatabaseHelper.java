package com.dreamfish.restreminders.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MainDatabaseHelper extends SQLiteOpenHelper {

    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "reminders.db";

    /**
     * 在构造方法中，尽量少的向外提供参数接口，防止其他类修改数据库名称、
     * 版本号。
     */
    public MainDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
      db.execSQL("create table " + MainDbSchema.ReminderConfigTable.NAME + "(" +
        "_id integer primary key autoincrement, " +
        MainDbSchema.ReminderConfigTable.COLS.KEY + "," +
        MainDbSchema.ReminderConfigTable.COLS.VALUE +
        ")"
      );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
