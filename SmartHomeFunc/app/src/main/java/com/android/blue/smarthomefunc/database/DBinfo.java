package com.android.blue.smarthomefunc.database;

/**
 * Created by root on 1/4/18.
 */

public class DBinfo {
    public static class DB{
        public static final String DB_NAME = "devicesmart.db";
        public static final int DB_VERSION = 1;
    }

    public static class Table{
        public static String TABLE_NAME = "device";

        public static String TABLE_COLUMN_ID = "_id";
        public static String TABLE_COLUMN_MODE_NAME = "mode";
        public static String TABLE_COLUMN_DEVICE_NAME = "device";
        public static String TABLE_COLUMN_RSSI = "rssi";
        public static String TABLE_COLUMN_SWITCH_STATUS = "switch";
        public static String TABLE_COLUMN_DEVICE_ADDRESS = "address";

        public static final String DROP_TABLE = "DROP TABLE"+TABLE_NAME;
        //SQL语句写错，导致一直db文件失败
        public static final String CREAT_DEVICE_TABLE = "create table "+
                TABLE_NAME +"("+
                TABLE_COLUMN_ID+" integer primary key autoincrement,"+
                TABLE_COLUMN_MODE_NAME+" String not null,"+
                TABLE_COLUMN_DEVICE_NAME+ " String,"+
                TABLE_COLUMN_DEVICE_ADDRESS+ " string not null,"+
                TABLE_COLUMN_RSSI+ " integer not null,"+
                TABLE_COLUMN_SWITCH_STATUS+" integer not null);";
    }
}
