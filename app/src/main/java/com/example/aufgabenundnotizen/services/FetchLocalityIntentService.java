package com.example.aufgabenundnotizen.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

import com.example.aufgabenundnotizen.helpers.Constants;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by Tobias on 11.03.16.
 * Diese Klasse handhabt Abfragen asynchron auf einem Workerthread und
 * stoppt sich selber, wenn die Verarbeitung abgeschlossen ist.
 */
public class FetchLocalityIntentService extends IntentService {

    protected ResultReceiver mReceiver;

    public FetchLocalityIntentService() {
        super("FetchLocalityIntentService");
    }

    public FetchLocalityIntentService(String name) {
        super(name);
    }

    public static void start(Context context, ResultReceiver receiver, Location location) {
        Intent intent = new Intent(context, FetchLocalityIntentService.class);
        intent.putExtra(Constants.ARG_RECEIVER, receiver);
        intent.putExtra(Constants.ARG_LOCATION_DATA_EXTRA, location);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        mReceiver = intent.getParcelableExtra(Constants.ARG_RECEIVER);
        Location location = intent.getParcelableExtra(Constants.ARG_LOCATION_DATA_EXTRA);

        Log.i("demo", "loation? " + location + "receiver? " + mReceiver);

        if (location == null | mReceiver == null) {
            return;
        }

        double latitude = location.getLatitude();
        double longitude = location.getLongitude();

        // Reverse Geocoding
        Geocoder geoCoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = null;

        try {
            addresses = geoCoder.getFromLocation(latitude, longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String locality = "";

        if (addresses != null && addresses.size() > 0) {
            locality = addresses.get(0).getLocality();
        }

        deliverResultToReceiver(locality);


    }

    private void deliverResultToReceiver(String result) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.ARG_RESULT_DATA_KEY, result);
        mReceiver.send(0, bundle);
    }
}
