package com.racine.cleancalls.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Shawn Racine.
 */
public class SMSBlockerDAO extends AbstrastDAO<SMSBlocker> {
    public SMSBlockerDAO(Context context) {
        super(context);
        TABLE_NAME = "SMSBlocker";
        PRIMARY_KEY = "_id";
    }

    @Override
    protected ContentValues insertContent(SMSBlocker model) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("phone", model.phone);
        contentValues.put("type", model.type);
        contentValues.put("content", model.content);
        return contentValues;
    }

    @Override
    protected List<SMSBlocker> CursorToList(Cursor cursor) {
        List<SMSBlocker> list = new ArrayList<>();
        while (cursor.moveToNext()) {
            SMSBlocker item = new SMSBlocker();
            item.phone = cursor.getString(cursor.getColumnIndex("phone"));
            item.type = cursor.getString(cursor.getColumnIndex("type"));
            item.content = cursor.getString(cursor.getColumnIndex("content"));
            list.add(item);
        }
        return list;
    }

    @Override
    protected String[] deleteWhere(SMSBlocker model) {
        String[] where = new String[2];
        where[0] = "phone = ?";
        where[1] = "" + model.phone;
        return where;
    }

    @Override
    protected ContentValues updateContent(SMSBlocker model) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("phone", model.phone);
        contentValues.put("type", model.type);
        contentValues.put("content", model.content);
        return contentValues;
    }
}
