package com.example.aufgabenundnotizen.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.aufgabenundnotizen.helpers.JodaTimeUtils;
import com.example.aufgabenundnotizen.models.NoteItem;
import com.example.aufgabenundnotizen.models.TodoItem;

import org.joda.time.LocalDate;

/**
 * Created by Tobias on 22.02.16.
 * <p>
 * Hier wird die Struktur der Datenbank definiert und aufgebaut (onCreate),
 * sowie Änderungen an dieser (onUpgrade).
 */
class DatabaseHandler extends SQLiteOpenHelper {

    // Meta infos
    static final int DATABASE_VERSION = 11;
    static final String DATABASE_NAME = "items.db";

    // Tabellen
    static final String TABLE_ITEM = "item";
    static final String TABLE_TYPE = "type";

    // Item Tabelle Spalten Namen
    static final String KEY_ID = "_id"; // Lt. Android Konventionen wird der Primärschlüssel unterstrichen
    static final String KEY_TYPE = "type_name";
    static final String KEY_TITLE = "title";
    static final String KEY_CREATIONDATE = "creation_date";
    static final String KEY_NOTES = "notes";
    static final String KEY_DUEDATE = "due_date";
    static final String KEY_REMINDERDATE = "reminder_date";
    static final String KEY_LOCATION = "location";
    static final String KEY_DONE = "done";

    // Type Tabelle Spalten Namen
    static final String KEY_TYPENAME = "type_name";

    // Type Tabelle Initialisierungswerte
    static final String TYPE_TODO = "todo";
    static final String TYPE_NOTE = "note";

    // SQL Statements
    static final String CREATE_TABLE_ITEM =
            "CREATE TABLE " + TABLE_ITEM + " (" +
                    KEY_ID + " TEXT PRIMARY KEY NOT NULL," +
                    KEY_TYPE + " TEXT NOT NULL," +
                    KEY_TITLE + " TEXT," +
                    KEY_CREATIONDATE + " INTEGER," +
                    KEY_NOTES + " TEXT," +
                    KEY_DUEDATE + " INTEGER," +
                    KEY_REMINDERDATE + " INTEGER," +
                    KEY_LOCATION + " TEXT," +
                    KEY_DONE + " INTEGER" +
                    ");";

    static final String CREATE_TABLE_TYPE =
            "CREATE TABLE " + TABLE_TYPE + " (" +
                    KEY_TYPENAME + " TEXT PRIMARY KEY NOT NULL," +
                    "FOREIGN KEY (" + KEY_TYPENAME + ") REFERENCES item(" + KEY_TYPE + ")" +
                    ");";

    static final String DROP_TABLE_ITEM = "DROP TABLE IF EXISTS " + TABLE_ITEM;
    static final String DROP_TABLE_TYPE = "DROP TABLE IF EXISTS " + TABLE_TYPE;

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Wird aufgerufen, wenn die Datenbank das erste Mal erzeugt wird.
     * Hier werden CREATE TABLEs und default Werte implementiert.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(CREATE_TABLE_ITEM);
            db.execSQL(CREATE_TABLE_TYPE);

            // "Enum" Werte initialisieren
            ContentValues values1 = new ContentValues();
            values1.put(KEY_TYPENAME, TYPE_TODO);
            db.insert(TABLE_TYPE, null, values1);

            ContentValues values2 = new ContentValues();
            values2.put(KEY_TYPENAME, TYPE_NOTE);
            db.insert(TABLE_TYPE, null, values2);

//            createTestData(db, 15);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Wird aufgerufen, wenn die Datenbankstruktur verändert werden muss.
     * Hier werden DROP TABLEs, ADD TABLEs u.ä. implementiert.
     * Wichtig: Wird nur aufgerufen, wenn die Version erhöht wird!
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            db.execSQL(DROP_TABLE_ITEM);
            db.execSQL(DROP_TABLE_TYPE);
            onCreate(db);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createTestData(SQLiteDatabase db, long count) {
        for (int i = 1; i <= count; i++) {
            ContentValues values = new ContentValues();

            if (i % 2 == 0) {
                TodoItem todoItem = new TodoItem("todo " + i, "todoNotes " + i, new LocalDate(JodaTimeUtils.getGermanDateTimeZone()), JodaTimeUtils.getGermanDateTime(), "todoLocation " + i);

                values.put(KEY_TYPE, TYPE_TODO);
                values.put(KEY_ID, todoItem.getId());
                values.put(KEY_TITLE, todoItem.getTitle());
                values.put(KEY_CREATIONDATE, JodaTimeUtils.toMillisSinceEpoch(todoItem.getCreationDate()));
                values.put(KEY_NOTES, todoItem.getNotes());
                values.put(KEY_DUEDATE, JodaTimeUtils.toMillisSinceEpoch(todoItem.getDueDate()));
                values.put(KEY_REMINDERDATE, JodaTimeUtils.toMillisSinceEpoch(todoItem.getReminderDate()));
                values.put(KEY_LOCATION, todoItem.getLocation());
                values.put(KEY_DONE, todoItem.getDone());
            } else {
                NoteItem noteItem = new NoteItem("note " + i, "noteNotes " + i);

                values.put(KEY_TYPE, TYPE_NOTE);
                values.put(KEY_ID, noteItem.getId());
                values.put(KEY_TITLE, noteItem.getTitle());
                values.put(KEY_CREATIONDATE, JodaTimeUtils.toMillisSinceEpoch(noteItem.getCreationDate()));
                values.put(KEY_NOTES, noteItem.getNotes());
            }

            db.insert(TABLE_ITEM, null, values);
        }
    }
}
