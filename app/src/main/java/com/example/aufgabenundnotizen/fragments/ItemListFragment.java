package com.example.aufgabenundnotizen.fragments;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.aufgabenundnotizen.R;
import com.example.aufgabenundnotizen.activities.ItemDetailActivity;
import com.example.aufgabenundnotizen.adapters.RecyclerViewAdapter;
import com.example.aufgabenundnotizen.helpers.Constants;
import com.example.aufgabenundnotizen.loaders.AllItemsLoader;
import com.example.aufgabenundnotizen.loaders.SingleItemLoader;
import com.example.aufgabenundnotizen.models.Item;
import com.example.aufgabenundnotizen.models.NoteItem;
import com.example.aufgabenundnotizen.models.TodoItem;
import com.example.aufgabenundnotizen.other.DbActionTask;
import com.example.aufgabenundnotizen.other.DividerItemDecoration;
import com.example.aufgabenundnotizen.other.FilterType;
import com.example.aufgabenundnotizen.other.MapWrapper;
import com.example.aufgabenundnotizen.other.NotificationReceiver;
import com.google.gson.Gson;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemListFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<Item>>, RecyclerViewAdapter.OnItemClickListener {

    private ItemDetailFragment mItemDetailFragment;

    private FilterType mFilterType;
    private boolean mTwoPane;

    private RecyclerView mRecyclerView;
    private RecyclerViewAdapter mRecyclerViewAdapter;

    private boolean mIsReceiverRegistered;
    private BroadcastReceiver mReceiver;
    private LocalBroadcastManager mLocalBroadcastManager;

    private volatile boolean mUpdatedDone;

    // Leerkonstruktor wird benötigt
    public ItemListFragment() {
    }

    public static ItemListFragment newInstance(FilterType filterType) {
        ItemListFragment fragment = new ItemListFragment();
        Bundle args = new Bundle();
        args.putSerializable(Constants.ARG_ITEMS_FILTER, filterType);
        fragment.setArguments(args);
        return fragment;
    }

    public static void sendBroadcast(Context context, String action, FilterType filterType, String itemId, DbActionTask.Action dbAction) {
        Intent intent = new Intent();
        intent.setAction(action);

        Bundle extras = new Bundle();
        extras.putSerializable(Constants.ARG_ITEMS_FILTER, filterType);
        extras.putString(Constants.ARG_ITEM_ID, itemId);
        extras.putSerializable(Constants.ARG_DB_ACTION, dbAction);

        intent.putExtras(extras);

        Log.i("receiver", "sendBroadcast " + itemId);

        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();

        if (args != null) {
            mFilterType = (FilterType) args.get(Constants.ARG_ITEMS_FILTER);
        }
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
    public void onStart() {
        registerBroadcastReceiver();

        super.onStart();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser) {
            // Nach oben scrollen
            if (mRecyclerView != null) {
                // Letzte Position, weil das Layout "umgedreht" ist
                mRecyclerView.scrollToPosition(mRecyclerViewAdapter.getItemCount() - 1);
            }
        } else {
            // Wenn man zu einer anderen Page swiped,
            // Detail Fragment zerstören
            if (mItemDetailFragment != null) {
                getChildFragmentManager().beginTransaction()
                        .remove(mItemDetailFragment)
                        .commit();
            }
        }
    }

    public void registerBroadcastReceiver() {
        if (mReceiver == null) {
            mReceiver = new RefreshItemsReceiver();

            mLocalBroadcastManager = LocalBroadcastManager.getInstance(getContext());
            IntentFilter filter = new IntentFilter(Constants.ACTION_REFRESH_ITEMS);
            mLocalBroadcastManager.registerReceiver(mReceiver, filter);

            mIsReceiverRegistered = true;
        }
    }

    @Override
    public void onStop() {
        unregisterBroadcastReceiver();

        super.onStop();
    }

    public void unregisterBroadcastReceiver() {
        if (mIsReceiverRegistered) {
            LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mReceiver);
            mReceiver = null;
        }
    }

    @Override
    public Loader<List<Item>> onCreateLoader(int id, Bundle args) {
        return new AllItemsLoader(getContext().getApplicationContext(), mFilterType);
    }

    @Override
    public void onLoadFinished(Loader<List<Item>> loader, List<Item> data) {
        mRecyclerViewAdapter.swapData(data);
    }

    @Override
    public void onLoaderReset(Loader<List<Item>> loader) {
        mRecyclerViewAdapter.swapData(Collections.<Item>emptyList());
    }

    private void loadData() {
        getLoaderManager().initLoader(R.id.all_items_loader_id, getArguments(), this);
    }

    private RecyclerViewAdapter setupRecyclerView(RecyclerView recyclerView) {
        int dividerPaddingLeft = getResources().getDimensionPixelSize(R.dimen.divider_padding_left);

        // Neueste Items immer oben anzeigen
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        manager.setReverseLayout(true);
        manager.setStackFromEnd(true);
        recyclerView.setLayoutManager(manager);

        DividerItemDecoration decoration = new DividerItemDecoration(getContext());
        decoration.setPadding(dividerPaddingLeft);
        recyclerView.addItemDecoration(decoration);

        DefaultItemAnimator animator = new DefaultItemAnimator();
        animator.setAddDuration(300);
        animator.setMoveDuration(300);
        animator.setRemoveDuration(300);
        recyclerView.setItemAnimator(animator);

        RecyclerViewAdapter recyclerViewAdapter = new RecyclerViewAdapter();
        recyclerViewAdapter.setOnItemClickListener(this);

        Drawable drawableTodo = ContextCompat.getDrawable(getContext(), R.drawable.ic_todo);
        Drawable drawableNote = ContextCompat.getDrawable(getContext(), R.drawable.ic_note);
        Map<Integer, Drawable> drawables = new HashMap<>();
        drawables.put(R.id.todo_item, drawableTodo);
        drawables.put(R.id.note_item, drawableNote);
        recyclerViewAdapter.setDrawables(drawables);

        if (mFilterType == FilterType.ALL) {
            recyclerViewAdapter.setShowIcons(true);
        } else {
            recyclerViewAdapter.setShowIcons(false);
        }

        recyclerView.setAdapter(recyclerViewAdapter);
        return recyclerViewAdapter;
    }

    @Override
    public void onItemClick(Item item) {
        FilterType filterType = null;

        if (item instanceof TodoItem) {
            filterType = FilterType.TODOS;
        } else if (item instanceof NoteItem) {
            filterType = FilterType.NOTES;
        }

        String itemId = item.getId();

        if (mTwoPane) {
            mItemDetailFragment = ItemDetailFragment.newInstance(itemId, filterType);

            getChildFragmentManager().beginTransaction()
                    .replace(R.id.item_detail_container, mItemDetailFragment)
                    .commit();
        } else {
            ItemDetailActivity.start(getContext(), itemId, filterType);
        }
    }

    @Override
    public void onItemLongClick(final Item item) {
        alertActions(item).show();
    }

    @Override
    public void onItemCheckedChanged(Item item, boolean isChecked) {
        DbActionTask updateDoneTask = new DbActionTask(getContext(), null, DbActionTask.Action.UPDATE);

        if (item instanceof TodoItem) {
            ((TodoItem) item).setDone(isChecked);
        }

        updateDoneTask.setItem(item);
        updateDoneTask.execute();

        ItemListFragment.sendBroadcast(getContext(), Constants.ACTION_REFRESH_ITEMS, item.getFilterType(), item.getId(), DbActionTask.Action.UPDATE);
    }

    private AlertDialog alertActions(final Item item) {
        CharSequence actions[] = new CharSequence[]{getString(R.string.alertdialog_delete)};

        AlertDialog.Builder bldr = new AlertDialog.Builder(getContext());

        bldr.setItems(actions, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        alertDelete(item).show();
                        break;
                }
            }
        });
        return bldr.create();
    }

    private AlertDialog alertDelete(final Item item) {
        String msg = "";

        if (item instanceof NoteItem) {
            msg = "Möchten Sie die Notiz " + item.getTitle() + " wirklich löschen?";
        } else if (item instanceof TodoItem) {
            msg = "Möchten Sie die Aufgabe " + item.getTitle() + " wirklich löschen?";
        }

        AlertDialog.Builder bldr = new AlertDialog.Builder(getContext());

        bldr.setTitle(getString(R.string.deleteTitle));
        bldr.setIcon(R.mipmap.ic_launcher);
        bldr.setMessage(msg);
        bldr.setPositiveButton(R.string.deleteYes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                DbActionTask deleteTask = new DbActionTask(getContext(), new DbActionTask.Receiver() {
                    @Override
                    public void onPreExecute() {
                    }

                    @Override
                    public void onPostExecute(int res) {
                        FilterType filterType = null;

                        if (item instanceof TodoItem) {
                            filterType = FilterType.TODOS;
                        } else if (item instanceof NoteItem) {
                            filterType = FilterType.NOTES;
                        }
                        Log.i("receiver", "sendBroadcast");
                        ItemListFragment.sendBroadcast(getContext(), Constants.ACTION_REFRESH_ITEMS, filterType, item.getId(), DbActionTask.Action.DELETE);
                    }
                }, DbActionTask.Action.DELETE);

                deleteTask.setItem(item);
                deleteTask.execute();

                if (item instanceof TodoItem){
                    unregisterNotification((TodoItem) item);
                }
            }
        });
        bldr.setNegativeButton(R.string.deleteNo, null);
        return bldr.create();
    }

    private void unregisterNotification(TodoItem tItem){
        HashMap hm;

        if (tItem.getReminderDate() == null) {
            return;
        }

        Gson gson = new Gson();
        String hmS = PreferenceManager.getDefaultSharedPreferences(getContext()).getString("ITEM_HM","");
        if (hmS == "") {
            hm = new HashMap();
        } else {
            MapWrapper wrapperGet = gson.fromJson(hmS, MapWrapper.class);
            hm = wrapperGet.getHm();
        }

        int i = 0;
        Double ii;

        if (hm.containsKey(tItem.getId())){
            ii = (Double.parseDouble((String .valueOf(hm.get(tItem.getId())))));
            i = ii.intValue();
        } else {
            return;
        }

        AlarmManager alarmMgr = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getContext(), NotificationReceiver.class);
        intent.putExtra("item_title", tItem.getTitle());
        intent.putExtra("item_id", tItem.getId());
        intent.putExtra("not_id", i);

        PendingIntent alarmIntent = PendingIntent.getBroadcast(getContext(), i, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmMgr.cancel(alarmIntent);
        hm.remove(tItem.getId());

        MapWrapper wrapperSet = new MapWrapper();
        wrapperSet.setHm(hm);
        String serializedMap = gson.toJson(wrapperSet);
        PreferenceManager.getDefaultSharedPreferences(getContext()).edit().putString("ITEM_HM", serializedMap).commit();
    }

    private class RefreshItemsReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i("receiver", "onReceive");

            switch (intent.getAction()) {
                case Constants.ACTION_REFRESH_ITEMS:

                    Bundle extras = intent.getExtras();

                    if (extras == null) {
                        return;
                    }

                    FilterType filterType = (FilterType) extras.getSerializable(Constants.ARG_ITEMS_FILTER);
                    DbActionTask.Action dbAction = (DbActionTask.Action) extras.getSerializable(Constants.ARG_DB_ACTION);

                    Log.i("receiver", "filterType senden? " + filterType + "\nfilterType this? " + mFilterType);

                    if (filterType != null && (mFilterType == filterType || mFilterType == FilterType.ALL)) {

                        // Es wird ein Loader benötigt
                        if (dbAction == DbActionTask.Action.INSERT | dbAction == DbActionTask.Action.UPDATE) {
                            getLoaderManager().initLoader(R.id.refresh_item_loader_id, extras, new LoaderCallbacks());
                        }

                        Log.i("receiver", "itemId in onReceive? " + extras.getString(Constants.ARG_ITEM_ID));

                        if (dbAction == DbActionTask.Action.DELETE) {
                            mRecyclerViewAdapter.deleteItem(extras.getString(Constants.ARG_ITEM_ID));

                            // Detail Fragment entfernen
                            if (mItemDetailFragment != null) {
                                getChildFragmentManager().beginTransaction()
                                        .remove(mItemDetailFragment)
                                        .commit();
                            }
                        }
                    }
                    break;
            }
        }

        private class LoaderCallbacks implements LoaderManager.LoaderCallbacks<Item> {

            private Bundle mArgs;

            @Override
            public Loader<Item> onCreateLoader(int id, Bundle args) {
                Log.i("receiver", "createLoader with itemId? " + args.getString(Constants.ARG_ITEM_ID));

                mArgs = args;
                return new SingleItemLoader(getContext(), args.getString(Constants.ARG_ITEM_ID));
            }

            @Override
            public void onLoadFinished(Loader<Item> loader, Item data) {
                Log.i("receiver", "item loaded: " + data);

                if (mArgs != null) {
                    DbActionTask.Action action = (DbActionTask.Action) mArgs.getSerializable(Constants.ARG_DB_ACTION);

                    if (action == DbActionTask.Action.INSERT) {
                        mRecyclerViewAdapter.addItem(data);
                        mRecyclerView.scrollToPosition(mRecyclerViewAdapter.getItemCount() - 1);
                    } else if (action == DbActionTask.Action.UPDATE) {
                        mRecyclerViewAdapter.updateItem(data);
                    }
                }

                getLoaderManager().destroyLoader(R.id.refresh_item_loader_id);
            }

            @Override
            public void onLoaderReset(Loader<Item> loader) {
            }
        }
    }
}
