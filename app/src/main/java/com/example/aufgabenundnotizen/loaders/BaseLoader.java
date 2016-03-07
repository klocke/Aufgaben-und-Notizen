package com.example.aufgabenundnotizen.loaders;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.LocalBroadcastManager;

/**
 * Created by Tobias on 25.02.16.
 * Diese Klasse stellt eine eigene Implementierung des AsyncTaskLoaders dar,
 * die als Basisklasse für spezifische Loader verwendet werden soll.
 * Sie erlaubt eine höhere Kontrolle über die Automatismen von Loadern.
 * In abgeleiteten Klassen muss nur noch die loadInBackground Methode überschrieben werden.
 */
public abstract class BaseLoader<T> extends AsyncTaskLoader<T> {

    protected T mData;
    protected BroadcastReceiver mReceiver;
    public static final String ACTION_FORCE_LOAD = "com.example.aufgabenundnotizen.intent.action.FORCE_LOAD";

    public BaseLoader(Context context) {
        super(context);
    }

    // Liefert das Ergebnis an den registrierten Listener
    @Override
    public void deliverResult(T data) {
        mData = data;

        if (isStarted()) {
            // Wenn sich der Loader im Startstatus befindet,
            // wird das Ergebnis an den Client geliefert (durch die Superklasse).
            super.deliverResult(data);
        }
    }

    @Override
    protected void onStartLoading() {
        if (mData != null) {
            // zuvor bereits geladene Daten werden sofort geliefert
            deliverResult(mData);
        }

        // beginne die zugrundeliegende Datenquelle zu überwachen
        if (mReceiver == null) {
            mReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    forceLoad();
                }
            };

            LocalBroadcastManager manager = LocalBroadcastManager.getInstance(getContext());
            IntentFilter filter = new IntentFilter(ACTION_FORCE_LOAD);
            manager.registerReceiver(mReceiver, filter);
        }

        if (takeContentChanged() || mData == null) {
            // Wenn der Observer eine Änderung entdeckt, sollte er onContentChanged()
            // beim Loader aufrufen, was den nächsten Aufruf an TakeContentChanged() auslöst
            // und true zurückgibt. Wenn dies der Fall ist (oder die aktuellen Daten noch null sind),
            // wird ein neues Laden erzwungen.
            forceLoad();
        }
    }

    @Override
    protected void onStopLoading() {
        cancelLoad();
        // Der Observer wird nicht angerührt. Loader, die sich im "Stopstatus" befinden,
        // sollten immer noch die Datenquelle für Änderungen überwachen, damit der Loader
        // weiss, dass er ein neues Laden erzwingen soll, wenn neu gestartet wird.
    }

    @Override
    protected void onReset() {
        onStopLoading();

        if (mData != null) {
            // Ressourcen können explizit freigegeben werden.
            mData = null;
        }

        // Der Loader wird zurückgesetzt, also sollte die Überwachung eingestellt werden.
        if (mReceiver != null) {
           LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mReceiver);
            mReceiver = null;
        }
    }

    @Override
    public void onCanceled(T data) {
        super.onCanceled(data);
    }
}
