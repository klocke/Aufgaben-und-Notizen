package com.example.aufgabenundnotizen.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.aufgabenundnotizen.R;
import com.example.aufgabenundnotizen.adapters.RecyclerViewAdapter;
import com.example.aufgabenundnotizen.helpers.Args;
import com.example.aufgabenundnotizen.loaders.AllItemsLoader;
import com.example.aufgabenundnotizen.models.Item;
import com.example.aufgabenundnotizen.other.DividerItemDecoration;
import com.example.aufgabenundnotizen.other.FilterType;

import java.util.Collections;
import java.util.List;

/**
 * Created by Tobias on 19.02.16.
 * TODO: http://stackoverflow.com/questions/15897547/loader-unable-to-retain-itself-during-certain-configuration-change
 */
public class ItemListFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<Item>> {

    private boolean mTwoPane;

    private RecyclerView mRecyclerView;
    private RecyclerViewAdapter mRecyclerViewAdapter;

    // Leerkonstruktor wird benötigt
    public ItemListFragment() {
    }

    public static ItemListFragment newInstance(FilterType filterType) {
        ItemListFragment fragment = new ItemListFragment();
        Bundle args = new Bundle();
        args.putSerializable(Args.ARG_ITEMS_FILTER, filterType);
        fragment.setArguments(args);
        return fragment;
    }

    public boolean isTwoPane() {
        return mTwoPane;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_item_list, container, false);

        mRecyclerView = (RecyclerView) v.findViewById(R.id.item_list);
        mRecyclerViewAdapter = setupRecyclerView(mRecyclerView);

        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        loadData();

        if (getActivity().findViewById(R.id.item_detail_container) != null) {
            // Die detail container view wird nur bei großen Bildschirmen
            // vorhanden sein (res/values-w480dp).
            // Wenn diese View vorhanden ist, dann sollte die Activity
            // im two-pane Modus sein.
            mTwoPane = true;
        }
    }

    @Override
    public Loader<List<Item>> onCreateLoader(int id, Bundle args) {
        FilterType filterType = (FilterType) args.get(Args.ARG_ITEMS_FILTER);
        return new AllItemsLoader(getContext().getApplicationContext(), filterType);
    }

    @Override
    public void onLoadFinished(Loader<List<Item>> loader, List<Item> data) {
        mRecyclerViewAdapter.swapData(data);
    }

    @Override
    public void onLoaderReset(Loader<List<Item>> loader) {
        mRecyclerViewAdapter.swapData(Collections.<Item>emptyList());
    }

    private RecyclerViewAdapter setupRecyclerView(RecyclerView recyclerView) {
        int dividerPaddingLeft = getResources().getDimensionPixelSize(R.dimen.divider_padding_left);
        DividerItemDecoration decoration = new DividerItemDecoration(getContext());
        decoration.setPadding(dividerPaddingLeft);
        recyclerView.addItemDecoration(decoration);
        RecyclerViewAdapter recyclerViewAdapter = new RecyclerViewAdapter(this, getActivity().getSupportFragmentManager());
        recyclerView.setAdapter(recyclerViewAdapter);
        return recyclerViewAdapter;
    }

    private void loadData() {
        getLoaderManager().initLoader(R.id.all_items_loader_id, getArguments(), this);
    }
}
