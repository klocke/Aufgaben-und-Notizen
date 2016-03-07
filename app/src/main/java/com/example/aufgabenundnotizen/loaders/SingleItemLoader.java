package com.example.aufgabenundnotizen.loaders;

import android.content.Context;

import com.example.aufgabenundnotizen.data.DatabaseAdapter;
import com.example.aufgabenundnotizen.models.Item;

/**
 * Created by Tobias on 25.02.16.
 */
public class SingleItemLoader extends BaseLoader<Item> {

    private String mItemId;

    public SingleItemLoader(Context context, String itemId) {
        super(context);
        mItemId = itemId;
    }

    @Override
    public Item loadInBackground() {
        if (mItemId == null) {
            return null;
        }

        return DatabaseAdapter.getInstance(getContext()).getItem(mItemId);
    }
}
