package com.dreamfish.restreminders.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.dreamfish.restreminders.database.MainDatabaseHelper;
import com.dreamfish.restreminders.database.MainDbSchema;

/**
 * 设置帮助类
 */
public class SettingsUtils {

  public SettingsUtils(Context context) {
    MainDatabaseHelper calcHistoryDatabaseHelper = new MainDatabaseHelper(context);
    databaseRead = calcHistoryDatabaseHelper.getReadableDatabase();
    databaseWrite = calcHistoryDatabaseHelper.getWritableDatabase();
  }

  private SQLiteDatabase databaseRead;
  private SQLiteDatabase databaseWrite;

  public SQLiteDatabase getDatabaseRead() { return databaseRead; }
  public SQLiteDatabase getDatabaseWrite() { return databaseWrite; }

  /**
   * 写入设置
   * @param key 键值
   * @param value 设置值
   */
  public void writeSettings(String key, String value) {
    ContentValues contentValues = new ContentValues();
    contentValues.put(MainDbSchema.ReminderConfigTable.COLS.VALUE, value);
    contentValues.put(MainDbSchema.ReminderConfigTable.COLS.KEY, key);
    Cursor cursor = databaseRead.query(MainDbSchema.ReminderConfigTable.NAME, MainDbSchema.ReminderConfigTable.COL_KEYS,
            MainDbSchema.ReminderConfigTable.COLS.KEY + "=?", new String[] { key },
            null, null, null);

    if (cursor.moveToFirst()) {
      databaseWrite.update(MainDbSchema.ReminderConfigTable.NAME,
              contentValues,
              MainDbSchema.ReminderConfigTable.COLS.KEY + "=?", new String[] { key });
    } else {
      databaseWrite.insert(MainDbSchema.ReminderConfigTable.NAME,null, contentValues);
    }

    cursor.close();
  }

  /**
   * 读取设置
   * @param key 键值
   * @param defaultValue 默认值
   * @return 返回设置值
   */
  public String readSettings(String key, String defaultValue) {

    String returnValue = defaultValue;
    Cursor cursor = databaseRead.query(MainDbSchema.ReminderConfigTable.NAME, MainDbSchema.ReminderConfigTable.COL_KEYS,
            MainDbSchema.ReminderConfigTable.COLS.KEY + "=?", new String[] { key },
            null, null, null);

    if (cursor.moveToFirst())
      returnValue = cursor.getString(cursor.getColumnIndex(MainDbSchema.ReminderConfigTable.COLS.VALUE));

    cursor.close();
    return returnValue;
  }

}
