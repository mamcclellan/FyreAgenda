package org.mam.kch.fyreagenda.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DatabaseHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "theartistformallyknownasFYRE!";
    private static final String TABLE_TASKS = "tasks";

    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_DETAILS = "details";
    private static final String KEY_CREATION_TIME = "creation_time";
    private static final String KEY_COMPLETION_TIME = "completion_time";
    private static final String KEY_TASK_TYPE = "task_type";
    private static final String KEY_TASK_COMPLETE = "task_complete";
    private static final String KEY_EDITED = "task_edited";
    private static final String KEY_NEW_TASK_TYPE = "new_task_type";
    private static final String KEY_LIST_POSITION = "list_position";


    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String create = "CREATE TABLE " + TABLE_TASKS + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT,"
                + KEY_DETAILS + " TEXT," + KEY_CREATION_TIME + " INTEGER,"
                + KEY_COMPLETION_TIME + " INTEGER," + KEY_TASK_TYPE + " INTEGER,"
                + KEY_TASK_COMPLETE + " BOOLEAN," + KEY_EDITED + " BOOLEAN,"
                + KEY_NEW_TASK_TYPE + " BOOLEAN," + KEY_LIST_POSITION + " INTEGER"
                + ")";
        db.execSQL(create);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TASKS);
        onCreate(db);
    }

    // Add new taskItem
    public void addTask(Task.TaskItem taskItem) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, taskItem.getName());
        values.put(KEY_DETAILS, taskItem.getDetails());
        values.put(KEY_CREATION_TIME, taskItem.getCreationTime());
        values.put(KEY_COMPLETION_TIME, taskItem.getCompletionTime());
        values.put(KEY_TASK_TYPE, taskItem.getTaskTypeValue());
        values.put(KEY_TASK_COMPLETE, taskItem.getTaskComplete());
        values.put(KEY_EDITED, taskItem.isEdited());
        values.put(KEY_NEW_TASK_TYPE, taskItem.isEdited());
        values.put(KEY_LIST_POSITION, taskItem.getListPosition());

        int rowID = (int) db.insert(TABLE_TASKS, null, values);
        db.close();
        taskItem.setID(String.valueOf(rowID));
    }

    // Update taskItem
    public int updateTask(Task.TaskItem taskItem) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, taskItem.getName());
        values.put(KEY_DETAILS, taskItem.getDetails());
        values.put(KEY_CREATION_TIME, taskItem.getCreationTime());
        values.put(KEY_COMPLETION_TIME, taskItem.getCompletionTime());
        values.put(KEY_TASK_TYPE, taskItem.getTaskTypeValue());
        values.put(KEY_TASK_COMPLETE, taskItem.getTaskComplete());
        values.put(KEY_EDITED, taskItem.isEdited());
        values.put(KEY_NEW_TASK_TYPE, taskItem.isEdited());
        values.put(KEY_LIST_POSITION, taskItem.getListPosition());

        int result = db.update(TABLE_TASKS, values, KEY_ID + " = ?",
                new String[] { String.valueOf(taskItem.getID()) });
        db.close();
        return result;
    }

    // Delete taskItem
    public void deleteTask(Task.TaskItem taskItem) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TASKS, KEY_ID + " = ?",
                new String[] { String.valueOf(taskItem.getID()) });
        db.close();
    }

    // Retrieve all taskItems
    public boolean loadTasks() {
        // dirty way to check if data was already loaded (e.g. onCreate() called after
        // app has already started
        if (Task.THISWEEK.size() > 0
                || Task.NEXTWEEK.size() > 0
                || Task.THISMONTH.size() > 0
                || Task.ARCHIVE.size() > 0) return true;

        String selectQuery = "SELECT * FROM " + TABLE_TASKS;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);

        if (cursor.moveToFirst()) {
            do {
                Task.TaskItem taskItem = new Task.TaskItem();
                // Strings are in order of sql table declaration
                taskItem.setID(cursor.getString(0));
                taskItem.setName(cursor.getString(1));
                taskItem.setDetails(cursor.getString(2));
                taskItem.setCreationTime(cursor.getLong(3));
                taskItem.setCompletionTime(cursor.getLong(4));
                if (cursor.getInt(6) == 1) taskItem.setTaskComplete(true);
                else taskItem.setTaskComplete(false);
                if (cursor.getInt(7) == 1) taskItem.setEdited(true);
                else taskItem.setEdited(false);
                if (cursor.getInt(8) == 1) taskItem.setNewTaskType(true);
                else taskItem.setNewTaskType(false);
                taskItem.setListPosition(cursor.getInt(9));
                int taskTypeValue = cursor.getInt(5);
                switch(taskTypeValue) {
                    case 0:
                        taskItem.setTaskType(Task.TaskType.THISWEEK);
                        break;
                    case 1:
                        taskItem.setTaskType(Task.TaskType.NEXTWEEK);
                        break;
                    case 2:
                        taskItem.setTaskType(Task.TaskType.THISMONTH);
                        break;
                    case 3:
                        taskItem.setTaskType(Task.TaskType.ARCHIVED);
                        break;
                    default:
                        taskItem.setTaskType(Task.TaskType.ARCHIVED);
                }

                Task.addItemFromDatabase(taskItem);

            } while (cursor.moveToNext());
        }
        else {
            // We did not find any saved data
            cursor.close();
            db.close();
            return false;
        }
        // We found saved data
        cursor.close();
        db.close();
        return true;
    }


}
