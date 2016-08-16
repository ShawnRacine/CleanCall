package com.racine.cleancalls;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.android.internal.telephony.ITelephony;
import com.racine.cleancalls.db.CallBlocker;
import com.racine.cleancalls.db.CallBlockerDAO;
import com.racine.cleancalls.db.SMSBlockerDAO;
import com.racine.cleancalls.utils.StringUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;

public class PhoneStatReceiver extends BroadcastReceiver {

    private String TAG = "PhoneStatReceiver";
    private TelephonyManager telMgr;
    private CallBlockerDAO callBlockerDAO;
    private SMSBlockerDAO smsBlockerDAO;

    public PhoneStatReceiver() {
        callBlockerDAO = new CallBlockerDAO(IApplication.getInstance());
        smsBlockerDAO = new SMSBlockerDAO(IApplication.getInstance());
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        telMgr = (TelephonyManager) context.getSystemService(Service.TELEPHONY_SERVICE);
        switch (telMgr.getCallState()) {
            case TelephonyManager.CALL_STATE_RINGING:
                String income = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
                Log.v(TAG, "number:" + income);
                if (isHarassedPhone(income)) {
                    endCall();
                    //save harassed phone number.
                    CallBlocker model = new CallBlocker();
                    model.phone = income;
                    model.type = "1";
                    model.date = "" + System.currentTimeMillis();
                    model.remark = "Beijing China Mobile";
                    callBlockerDAO.insert(model);
                }
                break;
            case TelephonyManager.CALL_STATE_OFFHOOK:
                break;
            case TelephonyManager.CALL_STATE_IDLE:
                break;
        }
    }

    private boolean isHarassedPhone(String income) {
        if (StringUtils.isNullOrEmpty(income))
            return false;
        return true;
    }

    private void endCall() {
        Class<TelephonyManager> c = TelephonyManager.class;
        try {
            Method getITelephonyMethod = c.getDeclaredMethod("getITelephony", (Class[]) null);
            getITelephonyMethod.setAccessible(true);
            ITelephony iTelephony = null;
            Log.e(TAG, "End call.");
            iTelephony = (ITelephony) getITelephonyMethod.invoke(telMgr, (Object[]) null);
            iTelephony.endCall();
        } catch (Exception e) {
            Log.e(TAG, "Fail to answer ring call.", e);
        }
    }

    private ArrayList<String> getPhoneNum(Context context) {
        ArrayList<String> numList = new ArrayList<String>();

        ContentResolver cr = context.getContentResolver();

        Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        while (cursor.moveToNext()) {
            String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
            Cursor phone = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, null, null);

            while (phone.moveToNext()) {
                String strPhoneNumber = phone.getString(phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                numList.add(strPhoneNumber);
                Log.v("tag", "strPhoneNumber:" + strPhoneNumber);
            }

            phone.close();
        }
        cursor.close();
        return numList;
    }
}