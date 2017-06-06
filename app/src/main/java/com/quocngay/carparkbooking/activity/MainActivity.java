package com.quocngay.carparkbooking.activity;

import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import com.quocngay.carparkbooking.R;
import com.quocngay.carparkbooking.adapter.TabPagerAdapter;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class MainActivity extends AppCompatActivity {

    public static final int FRAG_OPEN_TICKETS = 0;
    public static final int FRAG_MAP = 1;
    public static final int FRAG_HISTORY = 2;
    public static final int FRAG_PROFILE = 3;
    ViewPager viewPager;
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initHomeActivity();
    }

    private void initHomeActivity() {
        viewPager = (ViewPager) findViewById(R.id.home_pager);
        final TabPagerAdapter tabPagerAdapter = new TabPagerAdapter(getSupportFragmentManager());
        viewPager.setOffscreenPageLimit(tabPagerAdapter.getCount());
        viewPager.setAdapter(tabPagerAdapter);

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigation);
        final BottomNavigationMenuView menuView = (BottomNavigationMenuView) bottomNavigationView.getChildAt(0);

        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_open_tickets:
                                viewPager.setCurrentItem(FRAG_OPEN_TICKETS, true);
                                break;
                            case R.id.action_map:
                                viewPager.setCurrentItem(FRAG_MAP, true);
                                break;
                            case R.id.action_history:
                                viewPager.setCurrentItem(FRAG_HISTORY, true);
                                break;
                            case R.id.action_profile:
                                viewPager.setCurrentItem(FRAG_PROFILE, true);
                                break;
                        }
                        return true;
                    }
                });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                menuView.getChildAt(position).callOnClick();
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
                /*if (arg0 == ViewPager.SCROLL_STATE_IDLE) {
                    if (viewPager.getCurrentItem() != FRAG_SEARCH) {
                        // Hide the keyboard.
                        ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
                                .hideSoftInputFromWindow(viewPager.getWindowToken(), 0);
                    }
                }*/

            }
        });
    }

    public static boolean isInternetAvaiable(int timeOut) {
        InetAddress inetAddress = null;
        try {
            Future<InetAddress> future = Executors.newSingleThreadExecutor().submit(new Callable<InetAddress>() {
                @Override
                public InetAddress call() {
                    try {
                        return InetAddress.getByName("goole.com");
                    } catch (UnknownHostException e) {
                        return null;
                    }
                }
            });
            inetAddress = future.get(timeOut, TimeUnit.MILLISECONDS);
            future.cancel(true);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            e.printStackTrace();
        }
        return inetAddress != null && !inetAddress.equals("");
    }

}
