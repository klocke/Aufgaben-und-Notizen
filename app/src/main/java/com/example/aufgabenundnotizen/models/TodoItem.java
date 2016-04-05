package com.example.aufgabenundnotizen.models;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;


public class TodoItem extends Item {

    private LocalDate mDueDate;
    private DateTime mReminderDate;
    private String mLocation;
    private boolean mDone;

    /**
     * Konstruktor für neues Item.
     */
    public TodoItem(String title, String notes, LocalDate dueDate, DateTime reminderDate, String location) {
        super(title, notes);
        mDueDate = dueDate;
        mReminderDate = reminderDate;
        mLocation = location;
        mDone = false;
    }

    /**
     * Konstruktur um Eigenschaften eines "bestehenden" wieder herzustellen.
     */
    public TodoItem(String id, String title, DateTime creationDate, boolean done, LocalDate dueDate, DateTime reminderDate, String location, String notes) {
        super(id, title, creationDate, notes);
        mDueDate = dueDate;
        mReminderDate = reminderDate;
        mLocation = location;
        mDone = done;
    }

    public LocalDate getDueDate() {
        return mDueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        mDueDate = dueDate;
    }

    public DateTime getReminderDate() {
        return mReminderDate;
    }

    public void setReminderDate(DateTime reminderDate) {
        mReminderDate = reminderDate;
    }

    public String getLocation() {
        return mLocation;
    }

    public void setLocation(String location) {
        mLocation = location;
    }

    public boolean getDone() {
        return mDone;
    }

    public void setDone(boolean done) {
        mDone = done;
    }

    @Override
    public String toString() {
        return super.toString() + "\tFälligkeitsdatum: " + mDueDate + "\tErinnerungsdatum: " + mReminderDate + "\tErledigt: " + mDone + "\tNotizen: " + mNotes;
    }
}
