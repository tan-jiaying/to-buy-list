package com.bignerdranch.android.tobuylist;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.bignerdranch.android.tobuylist.database.ItemCursorWrapper;
import com.bignerdranch.android.tobuylist.database.ItemDbSchema.ItemTable;
import com.bignerdranch.android.tobuylist.database.ItemBaseHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ItemLab {
    private static ItemLab sItemLab; // singleton

    private Context mContext;
    private SQLiteDatabase mDatabase;

    public static ItemLab get(Context context) {
        if (sItemLab == null) {
            sItemLab = new ItemLab(context); // call constructor to create singleton instance
        }
        return sItemLab; // return singleton instance
    }

    private ItemLab(Context context) {
        mContext = context.getApplicationContext();
        mDatabase = new ItemBaseHelper(mContext)
                .getWritableDatabase();
    }

    public void addItem(Item i) {
        ContentValues values = getContentValues(i);
        mDatabase.insert(ItemTable.TABLE_NAME, null, values);
    }

    public void deleteItem(Item item) {
        mDatabase.delete(ItemTable.TABLE_NAME, "NAME=?", new String[]{item.getName()});
    }

    public List<Item> getItems() {
        List<Item> items = new ArrayList<>();

        ItemCursorWrapper cursor = queryItems(null, null);

        try {
            cursor.moveToFirst(); // pull data out of cursor by calling moveToFirst(), then read in row data
            while (!cursor.isAfterLast()) { // pointer is not at the end of data set
                items.add(cursor.getItem());
                cursor.moveToNext(); // advance to a new row
            }
        } finally {
            cursor.close();
        }

        return items;
    }

    public Item getItem(UUID id) {
        ItemCursorWrapper cursor = queryItems(
                ItemTable.Cols.UUID + " = ?",
                new String[] { id.toString() }
        );

        try {
            if (cursor.getCount() == 0) {
                return null;
            }

            cursor.moveToFirst(); // pull first item if it is there
            return cursor.getItem();
        } finally {
            cursor.close();
        }
    }

    public File getPhotoFile(Item item) {
        File filesDir = mContext.getFilesDir();
        return new File(filesDir, item.getPhotoFilename());
    }

    public void updateItem(Item item) {
        String uuidString = item.getId().toString();
        ContentValues values = getContentValues(item);

        mDatabase.update(ItemTable.TABLE_NAME, values,
                ItemTable.Cols.UUID + " = ?",
                new String[] { uuidString });
    }

    private ItemCursorWrapper queryItems(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                ItemTable.TABLE_NAME,
                null, // columns - null selects all columns
                whereClause,
                whereArgs,
                null,
                null,
                null
        );

        return new ItemCursorWrapper(cursor);
    }

    private static ContentValues getContentValues(Item item) {
        ContentValues values = new ContentValues();
        values.put(ItemTable.Cols.UUID, item.getId().toString());
        values.put(ItemTable.Cols.NAME, item.getName());
        values.put(ItemTable.Cols.DATE, item.getDate().getTime());
        values.put(ItemTable.Cols.TIME, item.getTime().getTime());
        values.put(ItemTable.Cols.QUANTITY, item.getQuantity());
        values.put(ItemTable.Cols.BOUGHT, item.isBought() ? 1 : 0);
        values.put(ItemTable.Cols.HELPER, item.getHelper());

        return values;
    }
}
