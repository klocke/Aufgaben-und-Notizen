package com.example.aufgabenundnotizen.models;

import java.util.Date;

/**
 * Created by Tobias on 16.02.16.
 */
public class TodoItem extends Item {

    private Date mDueDate;
    private Date mReminderDate;
    private String mLocation;   // TODO
    private boolean mDone;

    /**
     * Konstruktor für neues Item.
     */
    public TodoItem(String title, String notes, Date dueDate, Date reminderDate, String location) {
        super(title, notes);
        mDueDate = dueDate;
        mReminderDate = reminderDate;
        mLocation = location;
        mDone = false;
    }

    /**
     * Konstruktur um Eigenschaften eines "bestehenden" wieder herzustellen.
     */
    public TodoItem(String id, String title, Date creationDate, boolean done, Date dueDate, Date reminderDate, String location, String notes) {
        super(id, title, creationDate, notes);
        mDueDate = dueDate;
        mReminderDate = reminderDate;
        mLocation = location;
        mDone = done;
    }

    public Date getDueDate() {
        return mDueDate;
    }

    public void setDueDate(Date dueDate) {
        mDueDate = dueDate;
    }

    public Date getReminderDate() {
        return mReminderDate;
    }

    public void setReminderDate(Date reminderDate) {
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
