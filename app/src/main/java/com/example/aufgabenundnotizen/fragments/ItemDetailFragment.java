package com.example.aufgabenundnotizen.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.aufgabenundnotizen.R;
import com.example.aufgabenundnotizen.customviews.DateView;
import com.example.aufgabenundnotizen.helpers.Args;
import com.example.aufgabenundnotizen.loaders.SingleItemLoader;
import com.example.aufgabenundnotizen.models.Item;

/**
 * Dieses Fragment stellt eine einzelne Item Detailansicht dar.
 * Es ist entweder in einer {@Link ItemListActivity} in Zwei-Fenster-Ansicht (z.B. Tablets)
 * oder in einer {@Link ItemDetailActivity} (z.B. Smartphone in Portraitausrichtung) enthalten.
 */
public class ItemDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Item>, View.OnClickListener {

    private Item mItem;
    private boolean mIsNewItem;

    private EditText mEdtTitle;
    private DateView mDavDate;

    /**
     * Zwingend notwendiger Leerkonstruktor für den fragment manager um das Fragment
     * zu instanziieren (z.B. beim Ändern der Bildschirmausrichtung).
     */
    public ItemDetailFragment() {
    }

    public static ItemDetailFragment newInstance(Bundle args) {
        ItemDetailFragment fragment = new ItemDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        if (args != null) {
            mIsNewItem = args.getString(Args.ARG_ITEM_ID) == null;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_item_detail, container, false);

        mEdtTitle = (EditText) rootView.findViewById(R.id.edt_title);
        mDavDate = (DateView) rootView.findViewById(R.id.dav_dateview);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (!mIsNewItem) {
            loadData();
        }
    }

    @Override
    public Loader<Item> onCreateLoader(int id, Bundle args) {
        return new SingleItemLoader(getContext().getApplicationContext(), args.getString(Args.ARG_ITEM_ID));
    }

    @Override
    public void onLoadFinished(Loader<Item> loader, Item data) {
        mItem = data;
        // TODO: Detailansicht anpassen

        if (mItem != null) {
            if (mEdtTitle != null) {
                mEdtTitle.setText(mItem.getTitle());
            }

            if (mDavDate != null) {
//                TodoItem item = (TodoItem) mItem;
//
//                mDavDate.setDate(item.getDueDate());
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Item> loader) {
        mItem = null;
    }

    private void loadData() {
        getLoaderManager().initLoader(R.id.single_item_loader_id, getArguments(), this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            // TODO: Buttons hinzufügen und so
        }
    }

}
