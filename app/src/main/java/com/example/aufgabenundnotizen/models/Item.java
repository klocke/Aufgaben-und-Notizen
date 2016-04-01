package com.example.aufgabenundnotizen.models;

import com.example.aufgabenundnotizen.helpers.JodaTimeUtils;
import com.example.aufgabenundnotizen.other.FilterType;

import org.joda.time.DateTime;

import java.util.UUID;

/**
 * Created by Tobias on 22.02.16.
 */
public abstract class Item {

    protected String mId;
    protected String mTitle;
    protected DateTime mCreationDate;
    protected String mNotes;

    /**
     * Konstruktor für neues Item.
     */
    public Item(String title, String notes) {
        mTitle = title;
        mNotes = notes;
        mId = UUID.randomUUID().toString(); // eindeutigen Schlüssel erzeugen
        mCreationDate = JodaTimeUtils.getGermanDateTime(); // aktuellen Zeitpunkt speichern
    }

    /**
     * Konstruktur um Eigenschaften eines "bestehenden" wieder herzustellen.
     */
    public Item(String id, String title, DateTime creationDate, String notes) {
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

    public DateTime getCreationDate() {
        return mCreationDate;
    }


    public String getNotes() {
        return mNotes;
    }

    public void setNotes(String notes) {
        mNotes = notes;
    }

    public FilterType getFilterType() {
        if (this instanceof TodoItem) {
            return FilterType.TODOS;
        } else {
            return FilterType.NOTES;
        }
    }

    @Override
    public String toString() {
        return "Id: " + mId + "\tTitel: " + mTitle + "\tErstellungsdatum: " + mCreationDate + "\t";
    }

}
