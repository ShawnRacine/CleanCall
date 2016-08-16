package com.racine.cleancalls.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Shawn Racine.
 */
public class CallBlockerDAO extends AbstrastDAO<CallBlocker> {
    public CallBlockerDAO(Context context) {
        super(context);
        TABLE_NAME = "CallBlocker";
        PRIMARY_KEY = "_id";
    }

    @Override
    protected ContentValues insertContent(CallBlocker model) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("phone", model.phone);
        contentValues.put("type", model.type);
        contentValues.put("remark", model.remark);
        return contentValues;
    }

    @Override
    protected List<CallBlocker> CursorToList(Cursor cursor) {
        List<CallBlocker> list = new ArrayList<>();
        while (cursor.moveToNext()) {
            CallBlocker item = new CallBlocker();
            item.phone = cursor.getString(cursor.getColumnIndex("phone"));
            item.type = cursor.getString(cursor.getColumnIndex("type"));
            item.remark = cursor.getString(cursor.getColumnIndex("remark"));
            list.add(item);
        }
        return list;
    }

    @Override
    protected String[] deleteWhere(CallBlocker model) {
        String[] where = new String[2];
        where[0] = "phone = ?";
        where[1] = "" + model.phone;
        return where;
    }

    @Override
    protected ContentValues updateContent(CallBlocker model) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("phone", model.phone);
        contentValues.put("type", model.type);
        contentValues.put("remark", model.remark);
        return contentValues;
    }
}
