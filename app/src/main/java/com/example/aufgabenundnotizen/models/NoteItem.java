package com.example.aufgabenundnotizen.models;

import org.joda.time.DateTime;

/**
 * Created by Tobias on 16.02.16.
 */
public class NoteItem extends Item {

    /**
     * Konstruktor für neues Item.
     */
    public NoteItem(String title, String notes) {
        super(title, notes);
    }

    /**
     * Konstruktur um Eigenschaften eines "bestehenden" wieder herzustellen.
     */
    public NoteItem(String id, String title, DateTime creationDate, String notes) {
        super(id, title, creationDate, notes);
    }
}
