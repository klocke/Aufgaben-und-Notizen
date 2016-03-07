package com.example.aufgabenundnotizen.models;

import java.util.Date;
import java.util.UUID;

/**
 * Created by Tobias on 22.02.16.
 */
public abstract class Item {

    protected String mId;
    protected String mTitle;
    protected Date mCreationDate;
    protected String mNotes;
    protected byte[] mFiles;    // List byte Array?!

    // TODO Notes?!

    /**
     * Konstruktor für neues Item.
     */
    public Item(String title, String notes) {
        mTitle = title;
        mNotes = notes;
        mId = UUID.randomUUID().toString(); // eindeutigen Schlüssel erzeugen
        mCreationDate = new Date(); // aktuellen Zeitpunkt speichern
    }

    /**
     * Konstruktur um Eigenschaften eines "bestehenden" wieder herzustellen.
     */
    public Item(String id, String title, Date creationDate, String notes) {
        mId = id;
        mTitle = title;
        mCreationDate = creationDate;
        mNotes = notes;
    }

    public String getId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public Date getCreationDate() {
        return mCreationDate;
    }


    public String getNotes() {
        return mNotes;
    }

    public void setNotes(String notes) {
        mNotes = notes;
    }

    public byte[] getFiles() {
        return mFiles;
    }

    public void setmFiles(byte[] files) {
        mFiles = files;
    }

    @Override
    public String toString() {
        return "Id: " + mId + "\tTitel: " + mTitle + "\tErstellungsdatum: " + mCreationDate + "\t";
    }
}
