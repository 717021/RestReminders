package com.dreamfish.restreminders.database;

public class MainDbSchema {


    public static final class ReminderConfigTable {
        public static final String NAME = "config";
        public static final String[] COL_KEYS = {
                "set_key", "set_value"
        };
        public static final class COLS {
            public static final String KEY = "set_key";
            public static final String VALUE = "set_value";
        }
    }

}
