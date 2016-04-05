package com.example.aufgabenundnotizen.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import com.example.aufgabenundnotizen.helpers.JodaTimeUtils;
import com.example.aufgabenundnotizen.models.Item;
import com.example.aufgabenundnotizen.models.NoteItem;
import com.example.aufgabenundnotizen.models.TodoItem;
import com.example.aufgabenundnotizen.other.FilterType;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.aufgabenundnotizen.data.DatabaseHandler.KEY_CREATIONDATE;
import static com.example.aufgabenundnotizen.data.DatabaseHandler.KEY_DONE;
import static com.example.aufgabenundnotizen.data.DatabaseHandler.KEY_DUEDATE;
import static com.example.aufgabenundnotizen.data.DatabaseHandler.KEY_ID;
import static com.example.aufgabenundnotizen.data.DatabaseHandler.KEY_LOCATION;
import static com.example.aufgabenundnotizen.data.DatabaseHandler.KEY_NOTES;
import static com.example.aufgabenundnotizen.data.DatabaseHandler.KEY_REMINDERDATE;
import static com.example.aufgabenundnotizen.data.DatabaseHandler.KEY_TITLE;
import static com.example.aufgabenundnotizen.data.DatabaseHandler.KEY_TYPE;
import static com.example.aufgabenundnotizen.data.DatabaseHandler.TABLE_ITEM;
import static com.example.aufgabenundnotizen.data.DatabaseHandler.TYPE_NOTE;
import static com.example.aufgabenundnotizen.data.DatabaseHandler.TYPE_TODO;

/**
 * Hier wird der Teil der Datenbankschicht, der verantwortlich für Query, Insert, Update oder Delete Operationen ist, implementiert.
 * Es wird das Singleton Pattern verwendet um sicherzustellen, dass zu einem Zeitpunkt nur eine Instanz existiert.
 */
public class DatabaseAdapter {

    private static DatabaseAdapter mInstance;

    private DatabaseHandler mHandler;

    public static synchronized DatabaseAdapter getInstance(Context context) {
        if (mInstance == null) {
            // Es wird der ApplicationContext verwendet um einen Memoryleak zu verhindern
            mInstance = new DatabaseAdapter(context.getApplicationContext());
        }
        return mInstance;
    }

    private DatabaseAdapter(Context context) {
        mHandler = new DatabaseHandler(context);
    }

    public synchronized long addItem(Item item) {
        long rowId = -1;    // -1 = Fehler
        SQLiteDatabase db = null;

        try {
            db = mHandler.getWritableDatabase();

            rowId = db.insert(TABLE_ITEM, null, createContentValues(item));

            db.close();
        } catch (SQLiteException e) {
            e.printStackTrace();
        } finally {
            if (db != null) {
                db.close();
            }
        }

        return rowId;
    }

    public synchronized Item getItem(String id) {
        Item item = null;
        SQLiteDatabase db = null;

        try {
            db = mHandler.getReadableDatabase();

            String[] selectionArgs = {
                    id
            };

            Cursor cursor = db.query(TABLE_ITEM, null, KEY_ID + "=?", selectionArgs, null, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                item = createItem(cursor, getColumnAllocation(cursor));
            }

            db.close();
        } catch (SQLiteException e) {
            e.printStackTrace();
        } finally {
            if (db != null) {
                db.close();
            }
        }

        return item;
    }

    public synchronized List<Item> getItems(FilterType filterType) {
        List<Item> itemList = new ArrayList<>();
        SQLiteDatabase db = null;

        try {
            db = mHandler.getReadableDatabase();
            Cursor cursor = null;

            switch (filterType) {
                case ALL:
                    cursor = db.query(TABLE_ITEM, null, null, null, null, null, null);
                    break;
                case TODOS:
                    cursor = db.query(TABLE_ITEM, null, KEY_TYPE + "=?", new String[]{ TYPE_TODO }, null, null, null);
                    break;
                case NOTES:
                    cursor = db.query(TABLE_ITEM, null, KEY_TYPE + "=?", new String[]{ TYPE_NOTE }, null, null, null);
                    break;
            }

            if (cursor != null && cursor.moveToFirst()) {
                Map<String, Integer> columnAllocation = getColumnAllocation(cursor);

                do {
                    itemList.add(createItem(cursor, columnAllocation));
                } while (cursor.moveToNext());
            }

            db.close();
        } catch (SQLiteException e) {
            e.printStackTrace();
        } finally {
            if (db != null) {
                db.close();
            }
        }

        return itemList;
    }

    public synchronized List<Item> getItems() {
        return getItems(FilterType.ALL);
    }

    public synchronized int updateItem(Item item) {
        int affectedRows = 0;
        SQLiteDatabase db = null;

        try {
            db = mHandler.getWritableDatabase();

            String[] whereArgs = {
                    item.getId()
            };

            affectedRows = db.update(TABLE_ITEM, createContentValues(item), KEY_ID + "=?", whereArgs);

            db.close();
        } catch (SQLiteException e) {
            e.printStackTrace();
        } finally {
            if (db != null) {
                db.close();
            }
        }

        return affectedRows;
    }

    public synchronized int deleteItem(Item item) {
        int affectedRows = 0;
        SQLiteDatabase db = null;

        try {
            db = mHandler.getWritableDatabase();

            String[] whereArgs = {
                    item.getId()
            };

            affectedRows = db.delete(TABLE_ITEM, KEY_ID + "=?", whereArgs);

            db.close();
        } catch (SQLiteException e) {
            e.printStackTrace();
        } finally {
            if (db != null) {
                db.close();
            }
        }

        return affectedRows;
    }

    public synchronized int getItemsCount() {
        int count = 0;
        SQLiteDatabase db = null;

        try {
            db = mHandler.getReadableDatabase();

            Cursor cursor = db.query(TABLE_ITEM, null, null, null, null, null, null, null);
            count = cursor.getCount();

            db.close();
        } catch (SQLiteException e) {
            e.printStackTrace();
        } finally {
            if (db != null) {
                db.close();
            }
        }

        return count;
    }

    private Map<String, Integer> getColumnAllocation(Cursor cursor, String[] columns) {
        Map<String, Integer> columnAllocations = new HashMap<>();

        for (int i = 0; i < columns.length; i++) {
            String columnName = columns[i];
            int columnIndex = cursor.getColumnIndex(columnName);

            columnAllocations.put(columns[i], columnIndex);
        }

        return columnAllocations;
    }

    /**
     * Gibt eine Zuordnung der Spalten mit entspr. Index zurück.
     * Falls sich die Spalten von denen im cursor unterscheiden, kann man die überladene Methode nutzen.
     */
    private Map<String, Integer> getColumnAllocation(Cursor cursor) {
        return getColumnAllocation(cursor, cursor.getColumnNames());
    }

    private Item createItem(Cursor cursor, Map<String, Integer> columnAllocation) {
        Item item = null;

        String cId = cursor.getString(columnAllocation.get(KEY_ID));
        String type = cursor.getString(columnAllocation.get(KEY_TYPE));
        String title = cursor.getString(columnAllocation.get(KEY_TITLE));
        DateTime creationDate = JodaTimeUtils.toDateTime(cursor.getLong(columnAllocation.get(KEY_CREATIONDATE)));
        String notes = cursor.getString(columnAllocation.get(KEY_NOTES));

        if (type.equals(TYPE_TODO)) {
            LocalDate dueDate = JodaTimeUtils.toLocalDate(cursor.getLong(columnAllocation.get(KEY_DUEDATE)));
            DateTime reminderDate = JodaTimeUtils.toDateTime(cursor.getLong(columnAllocation.get(KEY_REMINDERDATE)));
            String location = cursor.getString(columnAllocation.get(KEY_LOCATION));
            boolean done = getBoolean(cursor.getInt(columnAllocation.get(KEY_DONE)));

            item = new TodoItem(cId, title, creationDate, done, dueDate, reminderDate, location, notes);
        } else if (type.equals(TYPE_NOTE)) {
            item = new NoteItem(cId, title, creationDate, notes);
        }

        return item;
    }

    private ContentValues createContentValues(Item item) {
        ContentValues values = new ContentValues();
        values.put(KEY_ID, item.getId());
        values.put(KEY_TITLE, item.getTitle());
        values.put(KEY_CREATIONDATE, JodaTimeUtils.toMillisSinceEpoch(item.getCreationDate()));
        values.put(KEY_NOTES, item.getNotes());

        if (item instanceof TodoItem) {
            TodoItem todoItem = (TodoItem) item;

            values.put(KEY_TYPE, TYPE_TODO);
            values.put(KEY_DUEDATE, JodaTimeUtils.toMillisSinceEpoch(todoItem.getDueDate()));
            values.put(KEY_REMINDERDATE, JodaTimeUtils.toMillisSinceEpoch(todoItem.getReminderDate()));
            values.put(KEY_LOCATION, todoItem.getLocation());
            values.put(KEY_DONE, todoItem.getDone());
        } else if (item instanceof NoteItem) {
            NoteItem noteItem = (NoteItem) item;

            values.put(KEY_TYPE, TYPE_NOTE);
        }

        return values;
    }

    /**
     * In SQLite gibt es keinen Boolean.
     * Daher sollen lt. doc integer mit 0 und 1 verwendet werden.
     * (s. https://www.sqlite.org/datatype3.html)
     */
    public static boolean getBoolean(int value) {
        if (value == 1) {
            return true;
        } else if (value == 0) {
            return false;
        } else {
            return false;
        }
    }
}
