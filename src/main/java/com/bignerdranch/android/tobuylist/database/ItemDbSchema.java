package com.bignerdranch.android.tobuylist.database;

public class ItemDbSchema {
    public static final class ItemTable {
        public static final String TABLE_NAME = "items";

        // table columns
        public static final class Cols {
            public static final String UUID = "uuid";
            public static final String NAME = "name";
            public static final String DATE = "date";
            public static final String TIME = "time";
            public static final String QUANTITY = "quantity";
            public static final String BOUGHT = "bought";
            public static final String HELPER = "helper";
        }
    }
}
