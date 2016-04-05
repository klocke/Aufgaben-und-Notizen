package com.example.aufgabenundnotizen.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.aufgabenundnotizen.R;
import com.example.aufgabenundnotizen.activities.ItemDetailActivity;
import com.example.aufgabenundnotizen.activities.ItemListActivity;
import com.example.aufgabenundnotizen.helpers.Constants;
import com.example.aufgabenundnotizen.helpers.Message;
import com.example.aufgabenundnotizen.loaders.SingleItemLoader;
import com.example.aufgabenundnotizen.models.Item;
import com.example.aufgabenundnotizen.models.NoteItem;
import com.example.aufgabenundnotizen.models.TodoItem;
import com.example.aufgabenundnotizen.other.CustomResultReceiver;
import com.example.aufgabenundnotizen.other.DbActionTask;
import com.example.aufgabenundnotizen.other.FilterType;
import com.example.aufgabenundnotizen.other.MapWrapper;
import com.example.aufgabenundnotizen.other.NotificationReceiver;
import com.example.aufgabenundnotizen.services.FetchLocalityIntentService;
import com.example.customviews.DateTimeView;
import com.example.customviews.DateView;
import com.example.customviews.DateViewBase;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import java.util.HashMap;
import java.util.Map;

/**
 * Dieses Fragment stellt eine einzelne Item Detailansicht dar.
 * Es ist entweder in einer {@Link ItemListActivity} in Zwei-Fenster-Ansicht (z.B. Tablets)
 * oder in einer {@Link ItemDetailActivity} (z.B. Smartphone in Portraitausrichtung) enthalten.
 */
public class ItemDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Item>, View.OnClickListener, View.OnFocusChangeListener, DateViewBase.OnHasChangesListener, TextWatcher, GoogleApiClient.OnConnectionFailedListener, DbActionTask.Receiver {

    private Item mItem;
    private boolean mIsNewItem;
    private FilterType mFilterType;

    private GoogleApiClient mGoogleApiClient;

    public static final int REQUEST_LOCATION = 1;

    private boolean mPermissionRequest;

    private DbActionTask mDbActionTask;
    private boolean mHasChanges;
    private boolean mIsInit = true;

    private EditText mEdtTitle;
    private DateView mDavDate;
    private DateTimeView mDtvDateTime;

    private View mLovLocation;
    private EditText mEdtLocation;
    private ImageView mImvPlace;
    private ImageView mImvMyLocality;

    private EditText mEdtNotes;
    private HashMap<String, Integer> hm;


    /**
     * Zwingend notwendiger Leerkonstruktor für den fragment manager um das Fragment
     * zu instanziieren (z.B. beim Ändern der Bildschirmausrichtung).
     */
    public ItemDetailFragment() {
    }

    public static ItemDetailFragment newInstance(String itemId, FilterType filterType) {
        ItemDetailFragment fragment = new ItemDetailFragment();

        Bundle args = new Bundle();
        args.putString(Constants.ARG_ITEM_ID, itemId);
        args.putSerializable(Constants.ARG_ITEMS_FILTER, filterType);

        fragment.setArguments(args);

        return fragment;
    }

    public static ItemDetailFragment newInstance(Bundle args) {
        ItemDetailFragment fragment = new ItemDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient
                    .Builder(getContext())
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_item_detail, container, false);

        // Title
        mEdtTitle = (EditText) rootView.findViewById(R.id.edt_title);

        mEdtTitle.addTextChangedListener(this);

        // Due date
        mDavDate = (DateView) rootView.findViewById(R.id.dav_dateview);

        mDavDate.setOnHasChangesListener(this);

        // reminder date
        mDtvDateTime = (DateTimeView) rootView.findViewById(R.id.dtv_datetimeview);

        mDtvDateTime.setOnHasChangesListener(this);

        // Location
        mLovLocation = rootView.findViewById(R.id.lov_location);
        mEdtLocation = (EditText) rootView.findViewById(R.id.edt_location);
        mImvPlace = (ImageView) rootView.findViewById(R.id.imv_place);
        mImvMyLocality = (ImageView) rootView.findViewById(R.id.imv_my_locality);

        mImvMyLocality.setOnClickListener(this);
        mImvMyLocality.setOnFocusChangeListener(this);
        mEdtLocation.setOnFocusChangeListener(this);
        mEdtLocation.addTextChangedListener(this);

        // Notes
        mEdtNotes = (EditText) rootView.findViewById(R.id.edt_notes);

        mEdtNotes.addTextChangedListener(this);

        Bundle args = getArguments();
        if (args != null) {
            mIsNewItem = args.getString(Constants.ARG_ITEM_ID) == null;
            mFilterType = (FilterType) args.getSerializable(Constants.ARG_ITEMS_FILTER);
        }

        if (mFilterType != null && mFilterType == FilterType.NOTES) {
            mDavDate.setVisibility(View.GONE);
            mDtvDateTime.setVisibility(View.GONE);
            mLovLocation.setVisibility(View.GONE);
        }

        return rootView;
    }

    @Override
    public void onStart() {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }

        if (!mIsNewItem) {
            loadData();
        }

        super.onStart();
    }

    private void init() {
        if (mItem != null) {
            if (mEdtTitle != null) {
                mEdtTitle.setText(mItem.getTitle());
            }

            if (mEdtNotes != null) {
                mEdtNotes.setText(mItem.getNotes());
            }

            if (mItem instanceof TodoItem) {
                TodoItem todoItem = (TodoItem) mItem;

                if (mDavDate != null) {
                    mDavDate.setDate(todoItem.getDueDate());
                }

                if (mDtvDateTime != null) {
                    DateTime reminderDateTime = todoItem.getReminderDate();
                    LocalDate reminderLocalDate = null;
                    LocalTime reminderLocalTime = null;

                    if (reminderDateTime != null) {
                        reminderLocalDate = reminderDateTime.toLocalDate();
                        reminderLocalTime = reminderDateTime.toLocalTime();
                    }

                    mDtvDateTime.setDate(reminderLocalDate);
                    mDtvDateTime.setTime(reminderLocalTime);

                }

                if (mEdtLocation != null) {
                    mEdtLocation.setText(todoItem.getLocation());
                }
            }
        }

        mIsInit = false;
    }

    @Override
    public void onStop() {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }

        save();

        super.onStop();
    }

    private void save() {
        boolean shouldSave = false;

        Activity activity = getActivity();

        if (activity instanceof ItemDetailActivity) {
            // Es wird nur gespeichert, wenn man den Haken anklickt
            shouldSave = ((ItemDetailActivity) activity).shouldSave();
            ((ItemDetailActivity) activity).setShouldSave(false);

        } else if (activity instanceof ItemListActivity) {
            // Wenn man sich im two-pane Modus befindet, wird gespeichert,
            // falls man Eingaben in einer View gemacht hat.

            shouldSave = mHasChanges;
        }

        if (shouldSave) {
            if (mIsNewItem) {
                mDbActionTask = new DbActionTask(getContext(), this, DbActionTask.Action.INSERT);
            } else {
                mDbActionTask = new DbActionTask(getContext(), this, DbActionTask.Action.UPDATE);
            }

            mDbActionTask.execute();

            if (mItem instanceof TodoItem) {
                registerNotification();
            }
        }
    }

    @Override
    public Loader<Item> onCreateLoader(int id, Bundle args) {
        return new SingleItemLoader(getContext().getApplicationContext(), args.getString(Constants.ARG_ITEM_ID));
    }

    @Override
    public void onLoadFinished(Loader<Item> loader, Item data) {
        mItem = data;
        init();

        getLoaderManager().destroyLoader(R.id.single_item_loader_id);
    }

    @Override
    public void onLoaderReset(Loader<Item> loader) {
    }

    private void loadData() {
        getLoaderManager().initLoader(R.id.single_item_loader_id, getArguments(), this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.imv_my_locality) {
            getMyLocality();
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        int id = v.getId();

        Drawable drawable = mImvPlace.getDrawable();
        int newColor;

        if ((id == R.id.edt_location) && hasFocus) {
            newColor = ContextCompat.getColor(getContext(), R.color.colorAccent);
        } else {
            newColor = ContextCompat.getColor(getContext(), R.color.colorPrimaryDark);
        }

        PorterDuffColorFilter filter = new PorterDuffColorFilter(newColor, PorterDuff.Mode.SRC_ATOP);
        drawable.setColorFilter(filter);

        mImvPlace.invalidateDrawable(drawable);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i("demo", "onConnectionFailed");
    }

    private void getMyLocality() {
        if (mGoogleApiClient.isConnected()) {
            Location location = getCurrentLocationWrapper();

            if (location != null) {
                CustomResultReceiver resultReceiver = new CustomResultReceiver(new Handler(), new CustomResultReceiver.Receiver() {
                    @Override
                    public void onReceiveResult(int resultCode, Bundle resultData) {
                        String locality = resultData.getString(Constants.ARG_RESULT_DATA_KEY);

                        if (locality != null && !locality.isEmpty()) {
                            mEdtLocation.setText(locality);
                        } else {
                            Message.show(getContext(), getString(R.string.error_locality_not_found));
                        }
                    }
                });
                FetchLocalityIntentService.start(getContext(), resultReceiver, location);
            } else {
                if (!mPermissionRequest) {
                    Message.show(getContext(), getString(R.string.error_locality_not_found));
                }
                mPermissionRequest = false;
            }
        } else {
            Log.i("demo", "keine Verbindung zu Google.");
        }
    }

    private Location getCurrentLocationWrapper() {
        Location location = null;

        int hasAccessFineLocationPermission = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION);
        if (hasAccessFineLocationPermission == PackageManager.PERMISSION_DENIED) {
            // Permission Denied

            // Wenn die Permission zum ersten mal angefordert wird oder der Nutzer die Permission mit Never ask again kennzeichnet.
            // Bei letzterem wird natürlich der System Dialog nicht wieder angezeigt.
            if (!ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {
                mPermissionRequest = true;
                showMessageOKCancel(getString(R.string.dialog_warning_permission_required),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                requestPermission();
                            }
                        });
            } else {
                requestPermission();
            }
        } else {
            location = getCurrentLocation();
        }

        return location;
    }

    /**
     * Vor dem Aufruf dieser Methode sollte sichergestellt sein, dass die erforderliche Permission vorhanden ist!
     */
    @SuppressWarnings("ResourceType")
    private Location getCurrentLocation() {
        // Dieser Aufruf erfordert Permission(s) und führt ohne Prüfung zu einer Exception.
        // Die Programmlogik stellt sicher, dass die erforderliche Permission gesetzt ist.
        // Daher wird die Warnung unterdrückt.
        return LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(getContext())
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    /**
     * UI Abfrage an den Nutzer, aber nur wenn der Nutzer ihn nicht bereits mit
     * Never ask again gekennzeichnet hat.
     */
    private void requestPermission() {
        mPermissionRequest = true;

        // UI Abfrage an Nutzer
        ActivityCompat.requestPermissions(getActivity(),
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                REQUEST_LOCATION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_LOCATION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getMyLocality();
                }
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onPreExecute() {
        String title = null;
        String notes = null;
        LocalDate dueDate = null;
        DateTime reminderDate = null;
        String location = null;

        if (mEdtTitle != null) {
            title = mEdtTitle.getText().toString();
        }

        if (mEdtNotes != null) {
            notes = mEdtNotes.getText().toString();
        }

        if (mDavDate != null) {
            dueDate = mDavDate.getDate();
        }

        if (mDtvDateTime != null) {
            reminderDate = mDtvDateTime.getDateTime();
        }

        if (mEdtLocation != null) {
            location = mEdtLocation.getText().toString();
        }

        if (mIsNewItem) {
            if (mFilterType == FilterType.TODOS) {
                mItem = new TodoItem(title, notes, dueDate, reminderDate, location);

            } else if (mFilterType == FilterType.NOTES) {
                mItem = new NoteItem(title, notes);

            }

        } else {
            mItem.setTitle(title);
            mItem.setNotes(notes);

            if (mItem instanceof TodoItem) {
                ((TodoItem) mItem).setDueDate(dueDate);
                ((TodoItem) mItem).setReminderDate(reminderDate);
                ((TodoItem) mItem).setLocation(location);
            }
        }

        mDbActionTask.setItem(mItem);
    }

    @Override
    public void onPostExecute(int res) {
        DbActionTask.Action action = null;

        if (mIsNewItem) {
            action = DbActionTask.Action.INSERT;
        } else {
            action = DbActionTask.Action.UPDATE;
        }

        ItemListFragment.sendBroadcast(getContext(), Constants.ACTION_REFRESH_ITEMS, mFilterType, mItem.getId(), action);
    }

    private void registerNotification(){
        TodoItem tItem = (TodoItem) mItem;
        boolean remNull = false;

        if (tItem.getReminderDate() == null){
            remNull = true;
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
        } else if (remNull == false) {
            for (Map.Entry<String, Integer> entry : hm.entrySet()) {
                ii = Double.parseDouble(String.valueOf(entry.getValue()));

                if (ii.intValue() > i) {
                    i = ii.intValue();
                }
            }
            i++;

            hm.put(tItem.getId(), i);
        } else {
            //Item ist nicht in HashMap und hat reminderDate auf null;
            return;
        }

        AlarmManager alarmMgr = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getContext(), NotificationReceiver.class);
        intent.putExtra("item_title", tItem.getTitle());
        intent.putExtra("item_id", tItem.getId());
        intent.putExtra("not_id", i);

        PendingIntent alarmIntent = PendingIntent.getBroadcast(getContext(), i, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        if (remNull == false) {
            alarmMgr.set(AlarmManager.RTC, tItem.getReminderDate().getMillis(), alarmIntent);
        }else{
            alarmMgr.cancel(alarmIntent);
            hm.remove(tItem.getId());
        }

        MapWrapper wrapperSet = new MapWrapper();
        wrapperSet.setHm(hm);
        String serializedMap = gson.toJson(wrapperSet);
        PreferenceManager.getDefaultSharedPreferences(getContext()).edit().putString("ITEM_HM", serializedMap).commit();
    }

        @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (!mIsInit) {
            mHasChanges = true;

            mEdtTitle.removeTextChangedListener(this);
            mEdtLocation.removeTextChangedListener(this);
            mEdtNotes.removeTextChangedListener(this);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
    }

    @Override
    public void onHasChanges(View v, boolean hasChanges) {
        if (!mIsInit) {
            mHasChanges = hasChanges;

            if (v instanceof DateViewBase) {
                ((DateViewBase) v).setOnHasChangesListener(null);
            }
        }
    }
}