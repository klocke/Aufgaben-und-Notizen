package com.example.aufgabenundnotizen.helpers;

import com.example.aufgabenundnotizen.BuildConfig;

/**
 * Created by Tobias on 02.03.16.
 * Diese Klasse enthält eindeutige Schlüssel,
 * die innerhalb der App für Datenaustausche verwendet werden.
 */
public final class Constants {

    private Constants() {
        throw new IllegalStateException("No instances.");
    }

    public static final String PACKAGE_NAME = BuildConfig.APPLICATION_ID;

    // Argumente für Bundle
    public static final String ARG_ITEM_ID = PACKAGE_NAME + ".ITEM_ID";
    public static final String ARG_ITEMS_FILTER = PACKAGE_NAME + ".ITEMS_FILTER";
    public static final String ARG_RECEIVER = PACKAGE_NAME + ".RECEIVER";
    public static final String ARG_LOCATION_DATA_EXTRA = PACKAGE_NAME + ".LOCATION_DATA_EXTRA";
    public static final String ARG_RESULT_DATA_KEY = PACKAGE_NAME + ".RESULT_DATA_KEY";
    public static final String ARG_IS_INSERT = PACKAGE_NAME + ".IS_INSERT";

    // Actions für Intents
    public static final String ACTION_REFRESH_ITEMS = Constants.PACKAGE_NAME + ".REFRESH_ITEM";
}
