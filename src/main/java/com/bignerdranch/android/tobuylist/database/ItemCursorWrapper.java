package com.bignerdranch.android.tobuylist.database;

import android.database.Cursor;
import android.database.CursorWrapper;
import com.bignerdranch.android.tobuylist.database.ItemDbSchema.ItemTable;

import com.bignerdranch.android.tobuylist.Item;

import java.util.Date;
import java.util.UUID;

public class ItemCursorWrapper extends CursorWrapper {
    public ItemCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Item getItem() { // reading from database
        String uuidString = getString(getColumnIndex(ItemTable.Cols.UUID));
        String name = getString(getColumnIndex(ItemTable.Cols.NAME));
        long date = getLong(getColumnIndex(ItemTable.Cols.DATE));
        long time = getLong(getColumnIndex(ItemTable.Cols.TIME));
        int quantity = getInt(getColumnIndex(ItemTable.Cols.QUANTITY));
        int isBought = getInt(getColumnIndex(ItemTable.Cols.BOUGHT));
        String helper = getString(getColumnIndex(ItemTable.Cols.HELPER));

        Item item = new Item(UUID.fromString(uuidString));
        item.setName(name);
        item.setDate(new Date(date));
        item.setTime(new Date(time));
        item.setQuantity(quantity);
        item.setBought(isBought != 0); // if item is bought
        item.setHelper(helper);

        return item;
    }
}
