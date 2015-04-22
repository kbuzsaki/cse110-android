package edu.ucsd.studentpoll;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorDescription;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;

import static android.provider.CalendarContract.Calendars;
import static android.provider.CalendarContract.Events;
import static android.provider.CalendarContract.Instances;

import android.text.TextUtils;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class GoogleIntegration {

    /* private constructor to prevent instantiation */
    private GoogleIntegration() {
    }

    public static String getWifiResults(Context context) {
        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        List<ScanResult> results = wifi.getScanResults();

        // this makes me so sad
        List<String> ssids = new ArrayList<>();
        for(ScanResult result : results) {
            ssids.add(result.SSID + " - " + result.BSSID);
        }

        return TextUtils.join("\n", ssids);
    }

    public static String getWifiP2P(Context context) {
        WifiP2pManager p2pWifi = (WifiP2pManager) context.getSystemService(Context.WIFI_P2P_SERVICE);

        // ui where you pick ONE account
        // then pick N calendars

        return "";
    }

    // Projection array. Creating indices for this array instead of doing
    // dynamic lookups improves performance.
    private static final String[] CALENDAR_PROJECTION = new String[] {
            Calendars._ID,                           // 0
            Calendars.NAME,
            Calendars.ACCOUNT_NAME
    };

    // The indices for the projection array above.
    private static final int CALENDAR_PROJECTION_ID_INDEX = 0;
    private static final int CALENDAR_PROJECTION_NAME_INDEX = 1;
    private static final int CALENDAR_PROJECTION_ACCOUNT_NAME_INDEX = 2;

    private static final String[] EVENT_PROJECTION = new String[] {
            Events._ID,
            Events.CALENDAR_ID,
            Events.TITLE
    };

    private static final int EVENT_PROJECTION_ID_INDEX = 0;
    private static final int EVENT_PROJECTION_CALENDAR_ID_INDEX = 1;
    private static final int EVENT_PROJECTION_TITLE_INDEX = 2;

    private static final String[] INSTANCE_PROJECTION = new String[] {
            Instances._ID,
            Instances.EVENT_ID,
            Instances.START_DAY,
            Instances.END_DAY
    };

    private static final int INSTANCE_PROJECTION_ID_INDEX = 0;
    private static final int INSTANCE_PROJECTION_EVENT_ID_INDEX = 1;
    private static final int INSTANCE_PROJECTION_START_DAY_INDEX = 2;
    private static final int INSTANCE_PROJECTION_END_DAY_INDEX = 3;

    private static Date getDate(int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getDefault());
        calendar.set(2015, month, day);
        return calendar.getTime();
    }

    public static String getCalendarResults(Context context) {
        ContentResolver contentResolver = context.getContentResolver();

        String calendarSelection = "((" + Calendars.ACCOUNT_NAME + " = ?) AND (" + Calendars.OWNER_ACCOUNT + " = ?))";
        String[] calendarArgs = new String[] {"saltor.tara@gmail.com", "saltor.tara@gmail.com"};

        Cursor calendarCursor = contentResolver.query(Calendars.CONTENT_URI, CALENDAR_PROJECTION, calendarSelection, calendarArgs, null);
        calendarCursor.moveToNext();
        long calendarId = calendarCursor.getLong(CALENDAR_PROJECTION_ID_INDEX);

        String eventSelection = "((" + Events.CALENDAR_ID + " = ?))";
        String[] eventArgs = new String[]{"" + calendarId};

        Cursor eventCursor = contentResolver.query(Events.CONTENT_URI, EVENT_PROJECTION, eventSelection, eventArgs, null);

//        String instanceSelection = "((" + Instances.BEGIN + " > ?) AND (" + Instances.END + " < ?))";
//        String[] instanceArgs = new String[] {"" + getDate(Calendar.APRIL, 12).getTime(), "" + getDate(Calendar.APRIL, 19).getTime()};
//
//        Cursor instanceCursor = contentResolver.query(Instances.CONTENT_URI, INSTANCE_PROJECTION, instanceSelection, instanceArgs, null);

        List<String> results = new ArrayList<>();

        while(eventCursor.moveToNext()) {
            String title = eventCursor.getString(EVENT_PROJECTION_TITLE_INDEX);
            results.add(title);
        }

//        Cursor calendarCursor = contentResolver.query(Calendars.CONTENT_URI, CALENDAR_PROJECTION, null, null, null);
//
//        List<String> results = new ArrayList<>();
//        while(calendarCursor.moveToNext()) {
//            String calendarName = calendarCursor.getString(CALENDAR_PROJECTION_NAME_INDEX);
//            String accountName = calendarCursor.getString(CALENDAR_PROJECTION_ACCOUNT_NAME_INDEX);
//            results.add(calendarName + " - " + accountName);
//        }

        return TextUtils.join("\n", results);
    }

    public static String getAccountNames(Context context) {
        AccountManager accountManager = AccountManager.get(context);

        List<String> results = new ArrayList<>();
        for (Account account : accountManager.getAccounts()) {
            results.add(account.toString());
        }

        return TextUtils.join("\n", results);

//        AuthenticatorDescription[] descriptions =  accountManager.getAuthenticatorTypes();
//        for (AuthenticatorDescription description : descriptions) {
//            if (description.type.equals(account.type)) {
//                PackageManager pm = getContext().getPackageManager();
//                return pm.getDrawable(description.packageName, description.iconId, null);
//            }
//        }
//        return null;
    }
}
