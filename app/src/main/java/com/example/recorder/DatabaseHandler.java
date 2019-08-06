package com.example.recorder;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.provider.ContactsContract;

import com.example.recorder.data.RecordingItem;
import com.example.recorder.listeners.OnDatabaseChangedListeners;

public class DatabaseHandler extends SQLiteOpenHelper {
    private Context mContext;
    private static OnDatabaseChangedListeners changedListeners;
    public static final String DATABASE_NAME = "saved_recordings.db";
    private static final int DATABASE_VERSION = 1;

    public static abstract class DatabaseItem implements BaseColumns{
        public static final String TABLE_NAME = "saved_recordings";

        public static final String COLUMN_NAME_RECORDING_NAME = "recording_name";
        public static final String COLUMN_NAME_RECORDING_FILE_PATH = "file_path";
        public static final String COLUMN_NAME_RECORDING_LENGTH = "length";
        public static final String COLUMN_NAME_TIME_ADDED = "time_added";
    }

    private static final String COMMA = ",";

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + DatabaseItem.TABLE_NAME + " (" +
                    DatabaseItem._ID + " INTEGER PRIMARY KEY " + COMMA +
                    DatabaseItem.COLUMN_NAME_RECORDING_NAME + " TEXT " + COMMA +
                    DatabaseItem.COLUMN_NAME_RECORDING_FILE_PATH + " TEXT " + COMMA +
                    DatabaseItem.COLUMN_NAME_RECORDING_LENGTH + " INTEGER " + COMMA +
                    DatabaseItem.COLUMN_NAME_TIME_ADDED + " INTEGER " + ")";
    private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + DatabaseItem.TABLE_NAME;
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
    public DatabaseHandler(Context context){
        super(context,DATABASE_NAME,null, DATABASE_VERSION );
        mContext = context;
    }
    public long addRecording(String recordName, String filePath, long length){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseItem.COLUMN_NAME_RECORDING_NAME, recordName);
        values.put(DatabaseItem.COLUMN_NAME_RECORDING_FILE_PATH, filePath);
        values.put(DatabaseItem.COLUMN_NAME_RECORDING_LENGTH, length);
        values.put(DatabaseItem.COLUMN_NAME_TIME_ADDED, System.currentTimeMillis());
        long rowId = db.insert(DatabaseItem.TABLE_NAME, null, values);

        if (changedListeners != null){
            changedListeners.onNewDatabaseEntryAdded();
        }

        return rowId;
    }
    public void removeItemWithId(int id){
        SQLiteDatabase db = getWritableDatabase();
        String [] whereArgs = {String.valueOf(id)};
        db.delete(DatabaseItem.TABLE_NAME, "_ID=?", whereArgs);
        if (changedListeners != null){
            changedListeners.onNewDatabaseEntryRemoved();
        }
    }
    public void renameItem(RecordingItem item, String recordName, String filePath){
        SQLiteDatabase database = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseItem.COLUMN_NAME_RECORDING_NAME, recordName);
        values.put(DatabaseItem.COLUMN_NAME_RECORDING_FILE_PATH, filePath);
        database.update(DatabaseItem.TABLE_NAME, values, DatabaseItem._ID+"="+item.getId(), null);
        if (changedListeners != null){
            changedListeners.onNewDatabaseEntryRenamed();
        }
    }

    public int getCount(){

        SQLiteDatabase db = getReadableDatabase();
        String [] projection = { DatabaseItem._ID };
        Cursor cursor = db.query(DatabaseItem.TABLE_NAME,projection,null,null,null,null,null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    public RecordingItem getItemAt(int position){
        SQLiteDatabase db = getReadableDatabase();
        String [] projection = {
                DatabaseItem._ID,
                DatabaseItem.COLUMN_NAME_RECORDING_NAME,
                DatabaseItem.COLUMN_NAME_RECORDING_FILE_PATH,
                DatabaseItem.COLUMN_NAME_RECORDING_LENGTH,
                DatabaseItem.COLUMN_NAME_TIME_ADDED
        };
        Cursor cursor = db.query(DatabaseItem.TABLE_NAME, projection,null,null,null,null,null);
        if (cursor.moveToPosition(position)) {
            RecordingItem item = new RecordingItem();
            item.setId(cursor.getInt(cursor.getColumnIndex(DatabaseItem._ID)));
            item.setName(cursor.getString(cursor.getColumnIndex(DatabaseItem.COLUMN_NAME_RECORDING_NAME)));
            item.setFilePath(cursor.getString(cursor.getColumnIndex(DatabaseItem.COLUMN_NAME_RECORDING_FILE_PATH)));
            item.setLength(cursor.getInt(cursor.getColumnIndex(DatabaseItem.COLUMN_NAME_RECORDING_LENGTH)));
            item.setTime(cursor.getLong(cursor.getColumnIndex(DatabaseItem.COLUMN_NAME_TIME_ADDED)));
            cursor.close();
            return item;
        }
        return null;
    }

    public static void setChangedListeners(OnDatabaseChangedListeners databaseChangedListeners){
        changedListeners = databaseChangedListeners;
    }


}
