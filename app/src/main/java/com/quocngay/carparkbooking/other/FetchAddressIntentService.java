package com.quocngay.carparkbooking.other;

import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.text.TextUtils;
import android.util.Log;

import com.quocngay.carparkbooking.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Quang Si on 7/11/2017.
 */

public class FetchAddressIntentService extends IntentService {

    protected ResultReceiver mReceiver;
    private Geocoder geocoder;

    public FetchAddressIntentService() {
        super("FetchAddressIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String errorMessage = "";

        // Get the location passed to this service through an extra.
        Location location = intent.getParcelableExtra(
                Constant.LOCATION_DATA_EXTRA);
        mReceiver = intent.getParcelableExtra(
                Constant.RECEIVER);
        List<Address> addresses = null;
        final Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {

            addresses = geocoder.getFromLocation(
                    location.getLatitude(),
                    location.getLongitude(),
                    // In this sample, get just a single address.
                    3);
        } catch (IOException ioException) {
            // Catch network or other I/O problems.
            errorMessage = getString(R.string.service_not_available);
            Log.e("TAG", errorMessage, ioException);
        } catch (IllegalArgumentException illegalArgumentException) {
            // Catch invalid latitude or longitude values.
            errorMessage = getString(R.string.invalid_lat_long_used);
            Log.e("TAG", errorMessage + ". " +
                    "Latitude = " + location.getLatitude() +
                    ", Longitude = " +
                    location.getLongitude(), illegalArgumentException);
        }

        // Handle case where no address was found.
        if (addresses == null || addresses.size() == 0) {
            if (errorMessage.isEmpty()) {
                errorMessage = getString(R.string.no_address_found);
                Log.e("TAG", errorMessage);
            }
            deliverResultToReceiver(Constant.FAILURE_RESULT, errorMessage, "");
        } else {
            Address address = addresses.get(0);
            ArrayList<String> addressFragments = new ArrayList<String>();

            // Fetch the address lines using getAddressLine,
            // join them, and send them to the thread.
            String addressTitle = address.getAddressLine(0);
            for (int i = 1; i <= address.getMaxAddressLineIndex(); i++) {
                addressFragments.add(address.getAddressLine(i));
            }
            Log.i("TAG", getString(R.string.address_found));
            deliverResultToReceiver(Constant.SUCCESS_RESULT,
                    TextUtils.join(", ", addressFragments), addressTitle);
        }
    }

    private void deliverResultToReceiver(int resultCode, String message, String title) {

        Bundle bundle = new Bundle();
        bundle.putString(Constant.RESULT_DATA_KEY, message);
        bundle.putString(Constant.RESULT_TITLE, title);
        mReceiver.send(resultCode, bundle);
    }

}
