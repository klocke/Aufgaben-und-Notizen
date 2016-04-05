package com.example.aufgabenundnotizen.loaders;

import android.content.Context;

import com.example.aufgabenundnotizen.data.DatabaseAdapter;
import com.example.aufgabenundnotizen.models.Item;
import com.example.aufgabenundnotizen.other.FilterType;

import java.util.List;

public class AllItemsLoader extends BaseLoader<List<Item>> {

    private FilterType mFilterType;

    public AllItemsLoader(Context context, FilterType filterType) {
        super(context);
        mFilterType = filterType;
    }

    @Override
    public List<Item> loadInBackground() {
        return DatabaseAdapter.getInstance(getContext()).getItems(mFilterType);
    }
}
