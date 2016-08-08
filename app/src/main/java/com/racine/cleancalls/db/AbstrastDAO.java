package com.racine.cleancalls.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.List;

/**
 * @author Shawn Racine.
 */
public abstract class AbstrastDAO<T> {
    protected DBHelper helper;
    protected SQLiteDatabase db;
    protected String TABLE_NAME;
    protected String PRIMARY_KEY;

    public AbstrastDAO(Context context) {
        helper = new DBHelper(context);
        db = helper.getWritableDatabase();
    }

    protected abstract ContentValues insertContent(T model);

    public long insert(T model) {
        long rowId;

        ContentValues initialValues = insertContent(model);
        int size = (initialValues != null && initialValues.size() > 0)
                ? initialValues.size() : 0;
        if (size > 0) {
            rowId = db.insert(TABLE_NAME, null, initialValues);
        } else {
            return -1;
        }

        return rowId;
    }

    public int insertAll(List<T> models) {
        db.beginTransaction();
        try {
            for (int i = 0; i < models.size(); i++) {
                if (insert(models.get(i)) < 0) {
                    return 0;
                }
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        return 1;
    }

    protected abstract List<T> CursorToList(Cursor cursor);

    public List<T> queryAll() {
        return queryAll(PRIMARY_KEY + " desc");
    }

    protected List<T> queryAll(String orderBy) {
        Cursor cursor = db.query(false, TABLE_NAME, null, null, null, null, null, orderBy, null);
        List<T> models = CursorToList(cursor);
        cursor.close();
        return models;
    }

    /**
     * if it exist where clause,guarantee return the String[] object of which lenght must greater than 1.
     * if not,return null.
     *
     * @param model the object prepared to remove
     * @return String[0] indicate whereClause; String[1/2...] indicate whereArgs. if not,return null.
     */
    protected abstract String[] deleteWhere(T model);

    public int delete(T model) {
        String whereClause = null;
        String[] whereArgs = null;

        String[] where = deleteWhere(model);
        int size = 0;
        if (null != where) {
            size = where.length;
        }
        if (size > 1) {
            whereClause = where[0];
            whereArgs = new String[size - 1];
            for (int i = 0; i < size - 1; i++) {
                whereArgs[i] = where[i + 1];
            }
        }
        return db.delete(TABLE_NAME, whereClause, whereArgs);
    }

    protected abstract ContentValues updateContent(T model);

    public int update(T model, String whereClause, String[] whereArgs) {
        return db.update(TABLE_NAME, updateContent(model), whereClause, whereArgs);
    }
}
