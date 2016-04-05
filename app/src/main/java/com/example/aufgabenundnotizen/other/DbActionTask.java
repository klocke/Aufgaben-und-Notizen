package com.example.aufgabenundnotizen.other;

import android.content.Context;
import android.os.AsyncTask;

import com.example.aufgabenundnotizen.data.DatabaseAdapter;
import com.example.aufgabenundnotizen.models.Item;

public class DbActionTask extends AsyncTask<Void, Void, Integer> {

    private Receiver mReceiver;
    private DatabaseAdapter mDatabaseAdapter;
    private Action mAction;
    private Item mItem;

    public DbActionTask(Context context, Receiver receiver, Action action) {
        super();
        mReceiver = receiver;
        mDatabaseAdapter = DatabaseAdapter.getInstance(context);
        mAction = action;
    }

    public interface Receiver {
        void onPreExecute();

        void onPostExecute(int res);
    }

    public enum Action {
        INSERT,
        UPDATE,
        DELETE
    }

    /**
     * Wird auf dem Mainthread ausgeführt.
     */
    @Override
    protected void onPreExecute() {
        if (mReceiver != null) {
            mReceiver.onPreExecute();
        }
    }

    @Override
    protected Integer doInBackground(Void... params) {
        int res = -1;

        if (mItem != null) {
            if (mAction == Action.INSERT) {
                mDatabaseAdapter.addItem(mItem);

            } else if (mAction == Action.UPDATE) {
                mDatabaseAdapter.updateItem(mItem);

            } else if (mAction == Action.DELETE) {
                mDatabaseAdapter.deleteItem(mItem);

            }
        }

        return res;
    }

    /**
     * Wird auf dem Mainthread ausgeführt.
     */
    @Override
    protected void onPostExecute(Integer integer) {
        if (mReceiver != null) {
            mReceiver.onPostExecute(integer);
        }
    }

    public void setItem(Item item) {
        this.mItem = item;
    }

}
