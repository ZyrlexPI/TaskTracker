package com.example.tasktracker.dataStore

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import okio.Path.Companion.toPath

fun createDataStoreAll(producePath: () -> String): DataStore<Preferences> {
    return PreferenceDataStoreFactory.createWithPath(produceFile = { producePath().toPath() })
}

fun createDataStore(context: Context): DataStore<Preferences> {
    Log.d("DataStore_Create", "DataStore_Create")
    return createDataStoreAll { context.filesDir.resolve(DATA_STORE_FILE_NAME).absolutePath }
}

internal const val DATA_STORE_FILE_NAME = "pref.preferences_pb"
