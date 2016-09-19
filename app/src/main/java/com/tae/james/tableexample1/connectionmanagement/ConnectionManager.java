package com.tae.james.tableexample1.connectionmanagement;

import android.content.Context;
import android.net.ConnectivityManager;
import android.util.Log;

/**
 * Created by jamessmith on 15/09/2016.
 */
public class ConnectionManager {

    private static Context context;

    public ConnectionManager(Context context) {
        this.context = context;
    }

    public static final boolean isInternetOn() {

        // get Connectivity Manager object to check connection
        ConnectivityManager connection = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        // Check for network connections
        if (connection.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTED ||
                connection.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTING ||
                connection.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTING ||
                connection.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTED) {
            Log.v("CManager", "got connection");
            return true;
        } else {
            Log.v("CManager", "got no connection");
            return false;
        }
    }
}
