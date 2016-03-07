package com.example.aufgabenundnotizen.loaders;

import android.content.Context;
import android.util.Log;

import com.example.aufgabenundnotizen.data.DatabaseAdapter;
import com.example.aufgabenundnotizen.other.FilterType;
import com.example.aufgabenundnotizen.models.Item;

import java.util.List;

/**
 * Created by Tobias on 25.02.16.
 */
public class AllItemsLoader extends BaseLoader<List<Item>> {

    private FilterType mFilterType;

    public AllItemsLoader(Context context, FilterType filterType) {
        super(context);
        mFilterType = filterType;
    }

    @Override
    public List<Item> loadInBackground() {
        Log.i("demo", "AllItemsLoader:loadInBackground");
        return DatabaseAdapter.getInstance(getContext()).getItems(mFilterType);
    }
}
