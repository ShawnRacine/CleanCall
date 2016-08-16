package com.racine.cleancalls.activity;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.racine.cleancalls.R;
import com.racine.cleancalls.adapter.RecordsAdapter;
import com.racine.cleancalls.model.RecordsModel;
import com.racine.cleancalls.net.ThreadTask.VoidThreadTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Shawn Racine.
 */
public class CallRecordsFragment extends BaseFragment {
    private ListView listView;
    private List<RecordsModel> mList;
    private RecordsAdapter adapter;
    private static String[] strs = new String[]{
            "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"
    };

    @Override
    protected void onCreateView() {
        setContentView(R.layout.callblocker);
    }

    @Override
    protected void initComponents() {
        listView = (ListView) mView.findViewById(R.id.listView);
    }

    @Override
    protected void registListeners() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
    }

    @Override
    protected void loadDatas() {
        new VoidThreadTask() {

            @Override
            protected Void doInBackground(Void aVoid) {
                mList = getLogPhoneNum(mContext);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                adapter = new RecordsAdapter(mContext, mList);
                listView.setAdapter(adapter);
            }
        }.execute();
    }

    private ArrayList<String> getSMSPhoneNum(Context context) {
        Uri CONTENT_URI = Uri.parse("content://sms");
        ArrayList<String> numList = new ArrayList<String>();
        ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor = contentResolver.query(CONTENT_URI, null, null, null, Telephony.Sms.DEFAULT_SORT_ORDER);
        if (cursor == null)
            return null;

        int nameColumn = cursor.getColumnIndex("person");
        int phoneNumberColumn = cursor.getColumnIndex("address");
        int smsbodyColumn = cursor.getColumnIndex("body");
        int dateColumn = cursor.getColumnIndex("date");
        int typeColumn = cursor.getColumnIndex("type");//1:received; 2:sended.
        while (cursor.moveToNext()) {
            String nameId = cursor.getString(nameColumn);
            String phoneNumber = cursor.getString(phoneNumberColumn);
            String smsbody = cursor.getString(smsbodyColumn);
            Date d = new Date(Long.parseLong(cursor.getString(dateColumn)));
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd " + "\n" + "hh:mm:ss");
            String date = dateFormat.format(d);
            String type = cursor.getString(typeColumn);
            String name = "NULL";

            Uri personUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, phoneNumber);

            Cursor cur = contentResolver.query(personUri, new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME}, null, null, null);

            if (cur.moveToFirst()) {
                int nameIndex = cur.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME);

                name = cur.getString(nameIndex);
            }
            cur.close();

            numList.add(name + "-" + phoneNumber + "-" + smsbody + "-" + date + "-" + type);
        }

        cursor.close();
        return numList;
    }

    private ArrayList<RecordsModel> getLogPhoneNum(Context context) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        ArrayList<RecordsModel> numList = new ArrayList<RecordsModel>();

        ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor = null;
        try {
            cursor = contentResolver.query(CallLog.Calls.CONTENT_URI, null, null, null, CallLog.Calls.DATE + " desc");
            if (cursor == null)
                return null;

            while (cursor.moveToNext()) {
                String name = cursor.getString(cursor.getColumnIndex(CallLog.Calls.CACHED_NAME));
                String number = cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER));
                int type = cursor.getInt(cursor.getColumnIndex(CallLog.Calls.TYPE));//1:incoming calls; 2:outgoing calls; 3:missed calls.
                long lDate = cursor.getLong(cursor.getColumnIndex(CallLog.Calls.DATE));
                long duration = cursor.getLong(cursor.getColumnIndex(CallLog.Calls.DURATION));//seconds
                String addr = cursor.getString(cursor.getColumnIndex(CallLog.Calls.GEOCODED_LOCATION));

                RecordsModel model = new RecordsModel();
                model.name = name;
                model.phone = number;
                model.date = sdf.format(new Date(lDate));
                model.addr = addr;
                model.duration = "" + duration;
                model.type = "" + type;

                numList.add(model);
            }
        } catch (SecurityException e) {

        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return numList;
    }

    private ArrayList<String> getContactsPhoneNum(Context context) {
        ArrayList<String> numList = new ArrayList<String>();

        ContentResolver cr = context.getContentResolver();

        Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, ContactsContract.Contacts.SORT_KEY_ALTERNATIVE + " ASC");

        while (cursor.moveToNext()) {
            String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
            Cursor phone = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, null, null);

            while (phone.moveToNext()) {
                String strPhoneNumber = phone.getString(phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                String strPhoneName = phone.getString(phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                numList.add(strPhoneNumber + "--" + strPhoneName);
                Log.v("tag", "strPhoneNumber:" + strPhoneNumber);
            }

            phone.close();
        }
        cursor.close();
        return numList;
    }
}
