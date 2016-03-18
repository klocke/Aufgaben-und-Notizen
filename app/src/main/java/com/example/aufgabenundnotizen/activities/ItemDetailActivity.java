package com.example.aufgabenundnotizen.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.example.aufgabenundnotizen.R;
import com.example.aufgabenundnotizen.fragments.ItemDetailFragment;
import com.example.aufgabenundnotizen.helpers.Constants;
import com.example.aufgabenundnotizen.loaders.BaseLoader;
import com.example.aufgabenundnotizen.other.FilterType;

/**
 * Diese Activity stellt eine einzelne Detailansicht für ein Item dar und
 * wird nur verwendet, wenn weniger als 480dp Bildschirmbreite verfügbar sind.
 * Wenn mind. 480dp in der Breite verfügbar sind (Tablet oder Smartphone in Landscape Ausrichtung),
 * werden die Details seite an seite mit der Liste von Items angezeigt in einer {@Link ItemListActivity}.
 */
public class ItemDetailActivity extends AppCompatActivity {

    private boolean mIsNewItem;
    private FilterType mFilterType;
    private boolean mShouldSave;

    public static void start(Context context, String itemId, FilterType filterType) {
        Bundle extras = new Bundle();
        extras.putString(Constants.ARG_ITEM_ID, itemId);
        extras.putSerializable(Constants.ARG_ITEMS_FILTER, filterType);

        Intent intent = new Intent(context, ItemDetailActivity.class);
        intent.putExtras(extras);

        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);

        // Den Up button in der Action Bar anzeigen.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            // Wenn keine Item Id mitgegeben wurde, handelt es sich um ein neues Item
            mIsNewItem = extras.getString(Constants.ARG_ITEM_ID) == null;
            mFilterType = (FilterType) extras.getSerializable(Constants.ARG_ITEMS_FILTER);

            setupTitle(actionBar, mIsNewItem, mFilterType);

            // savedInstanceState ist nicht null, wenn ein Fragmentstatus von
            // vorherigen Konfigurationen für diese Activity gespeichert wurde
            // (z.B. bei Drehung des Bildschirms von portrait in landscape).
            // In diesem Fall, wird das fragment automatisch wieder zu seinem
            // Container hinzugefügt sodass wir es nicht manuell hinzufügen müssen.
            // Für mehr Informationen, siehe den Fragment API guide auf:
            //
            // http://developer.android.com/guide/components/fragments.html
            //
            if (savedInstanceState == null) {
                // Erzeuge das detail Fragment und füge es der Activity
                // mithilfe einer fragment Transaktion hinzu.

                ItemDetailFragment fragment = ItemDetailFragment.newInstance(extras);
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.item_detail_container, fragment)
                        .commit();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // Diese ID stellt den Home oder Up Button dar.
                // Im Falle dieser Activity wird der Up Button angezeigt.
                navigateUp();
                return true;
            case R.id.action_finished:
                mShouldSave = true;

                // Bei Klick auf Fertig wird der Loader benachrichtigt und lädt erneut.
                Intent intent = new Intent(BaseLoader.ACTION_FORCE_LOAD);
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);

                navigateUp();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
    }

    private void navigateUp() {
        // Benutze NavUtils um den Nutzern die Navigation eines Levels nach oben in der Anwendungsstruktur
        // zu erlauben. Für mehr details, siehe das Navigation Pattern auf Android Design:
        //
        // http://developer.android.com/design/patterns/navigation.html#up-vs-back
        NavUtils.navigateUpTo(this, new Intent(this, ItemListActivity.class));
    }

    private void setupTitle(ActionBar actionBar, boolean newItem, FilterType filterType) {
        if (actionBar == null || filterType == null) {
            return;
        }

        String title = null;

        if (mIsNewItem) {
            if (mFilterType == FilterType.TODOS) {
                title = getString(R.string.title_item_detail_add_todo);

            } else if (mFilterType == FilterType.NOTES) {
                title = getString(R.string.title_item_detail_add_note);

            }

        } else {
            if (mFilterType == FilterType.TODOS) {
                title = getString(R.string.title_item_detail_edit_todo);

            } else if (mFilterType == FilterType.NOTES) {
                title = getString(R.string.title_item_detail_edit_note);

            }
        }

        if (title != null) {
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setTitle(title);
        }
    }

    public boolean shouldSave() {
        return mShouldSave;
    }

    public void setShouldSave(boolean shouldSave) {
        mShouldSave = shouldSave;
    }
}
