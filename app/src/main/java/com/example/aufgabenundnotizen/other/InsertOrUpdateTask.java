package com.example.aufgabenundnotizen.other;

import android.content.Context;
import android.os.AsyncTask;

import com.example.aufgabenundnotizen.data.DatabaseAdapter;
import com.example.aufgabenundnotizen.models.Item;

/**
 * Created by Tobias on 16.03.16.
 */
public class InsertOrUpdateTask extends AsyncTask<Void, Void, Integer> {

    private Receiver mReceiver;
    private DatabaseAdapter mDatabaseAdapter;
    private boolean mIsInsert;
    private Item mItem;

    public InsertOrUpdateTask(Context context, Receiver receiver, boolean isInsert) {
        super();
        mReceiver = receiver;
        mDatabaseAdapter = DatabaseAdapter.getInstance(context);
        mIsInsert = isInsert;
    }

    public interface Receiver {
        void onPreExecute();

        void onPostExecute(int res);
    }

    /**
     * Wird auf dem Mainthread ausgeführt.
     */
    @Override
    protected void onPreExecute() {
        mReceiver.onPreExecute();
    }

    @Override
    protected Integer doInBackground(Void... params) {
        int res = -1;

        if (mItem != null) {
            if (mIsInsert) {
                // Insert
                mDatabaseAdapter.addItem(mItem);

            } else {
                // Update
                mDatabaseAdapter.updateItem(mItem);

            }
        }

        return res;
    }

    /**
     * Wird auf dem Mainthread ausgeführt.
     */
    @Override
    protected void onPostExecute(Integer integer) {
        mReceiver.onPostExecute(integer);
    }

    public void setItem(Item item) {
        this.mItem = item;
    }
}
