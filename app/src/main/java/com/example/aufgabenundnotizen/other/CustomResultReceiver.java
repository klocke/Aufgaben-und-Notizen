package com.example.aufgabenundnotizen.other;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

/**
 * Created by Tobias on 11.03.16.
 */
public class CustomResultReceiver extends ResultReceiver {

    private Receiver mReceiver;

    public CustomResultReceiver(Handler handler, Receiver receiver) {
        super(handler);
        mReceiver = receiver;
    }

    public interface Receiver {
        void onReceiveResult(int resultCode, Bundle resultData);
    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {
        mReceiver.onReceiveResult(resultCode, resultData);
    }

}
