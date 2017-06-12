package com.quocngay.carparkbooking.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.quocngay.carparkbooking.R;
import com.quocngay.carparkbooking.activity.MainActivity;
import com.quocngay.carparkbooking.adapter.BookedTicketAdapter;
import com.quocngay.carparkbooking.dbcontext.DbContext;
import com.quocngay.carparkbooking.model.BookedTicketModel;
import com.quocngay.carparkbooking.model.GarageModel;
import com.quocngay.carparkbooking.other.Constant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class OpenTicketsFragment extends Fragment {

    private static String TAG = OpenTicketsFragment.class.getSimpleName();
    private BookedTicketAdapter ticketAdapter;
    ListView listTicket;
    private DbContext dbContext;
    private SharedPreferences pref;
    List<BookedTicketModel> ticketList;
    private Socket mSocket;
    {
        try {
//            mSocket = IO.socket("http://10.16.110.117:3000");
            mSocket = IO.socket("http://192.168.0.105:3000");
        } catch (URISyntaxException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    public OpenTicketsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        dbContext = DbContext.getInst();
        View view;

        if(!MainActivity.isInternetAvaiable(10000)) {
            view = inflater.inflate(R.layout.fragment_error, container, false);
            return view;
        } else {
            view = inflater.inflate(R.layout.fragment_open_tickets, container, false);
            listTicket = (ListView) view.findViewById(R.id.list_ticket);

            pref = getActivity().getSharedPreferences(Constant.APP_PREF, MODE_PRIVATE);
            String token = pref.getString(Constant.APP_PREF_TOKEN, "");
            token = "5e19efdfd462138bb264abec60024e3be01c22e99720c10a498057bba6ba48752aa586b3045f90b1f999053ccf5186cde5d6dd23f26e69415aeb5d1a85f64050";
            mSocket.connect();
            mSocket.emit(Constant.SERVER_EMIT_APP_REQUEST_OPEN_TICKETS, token);
            mSocket.on(Constant.SERVER_EMIT_SERVER_RESPONSE_OPEN_TICKETS, onNewMessage_getOpenTickets);

            ticketList = dbContext.getAllOpenBookedTicketModel();
            ticketAdapter = new BookedTicketAdapter(ticketList, getContext());
            listTicket.setAdapter(ticketAdapter);

            return view;
        }
    }

    private Emitter.Listener onNewMessage_getOpenTickets = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        JSONObject jsonObj = ((JSONObject) args[0]);
                        if(jsonObj.getBoolean(Constant.SERVER_RESPONSE_RESULT)) {
                            dbContext = DbContext.getInst();
                            JSONArray garaList = jsonObj.getJSONObject(Constant.SERVER_RESPONSE_DATA).getJSONArray("garageList");
                            JSONArray tickets = jsonObj.getJSONObject(Constant.SERVER_RESPONSE_DATA).getJSONArray("ticketList");
                            for(int i=0; i< garaList.length(); i++) {
                                GarageModel gara = GarageModel.createByJson(garaList.getJSONObject(i));
                                if(gara != null) {
                                    dbContext.addGaraModel(gara);
                                }
                            }

                            for(int i=0; i<tickets.length(); i++) {
                                BookedTicketModel ticket = BookedTicketModel.createByJson(tickets.getJSONObject(i));
                                if(ticket != null) {
                                    dbContext.addBookedTicketModel(ticket);
                                }
                            }

                            ticketList = dbContext.getAllOpenBookedTicketModel();
                            ticketAdapter = new BookedTicketAdapter(ticketList, getContext());
                            listTicket.setAdapter(ticketAdapter);
                        } else {
                            Toast.makeText(getContext(), "Data return from server is incorrect", Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, e.getMessage());
                    }

                }
            });
        }
    };
}
