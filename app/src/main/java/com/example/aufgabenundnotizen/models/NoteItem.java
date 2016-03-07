package com.example.aufgabenundnotizen.models;

import java.util.Date;

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
    public NoteItem(String id, String title, Date creationDate, String notes) {
        super(id, title, creationDate, notes);
    }
}
