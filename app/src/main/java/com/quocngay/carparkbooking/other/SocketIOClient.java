package com.quocngay.carparkbooking.other;

import android.util.Log;

import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import java.net.URISyntaxException;

/**
 * Created by Quang Si on 7/16/2017.
 */

public class SocketIOClient {
    public static SocketIOClient client;

    public Socket mSocket;

    {
        try {
            mSocket = IO.socket(Constant.SERVER_HOST);
        } catch (URISyntaxException e) {
            Log.e("Error", e.getMessage());
        }
    }

    public SocketIOClient() {
        if (client == null)
            client = this;
        if (mSocket.connected()) {
            mSocket.disconnect();
        }
        mSocket.connect();
    }

}
