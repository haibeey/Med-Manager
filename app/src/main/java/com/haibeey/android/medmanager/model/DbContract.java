package com.haibeey.android.medmanager.model;

import android.provider.BaseColumns;

/**
 * Created by haibeey on 3/28/2018.
 */

public class DbContract{

    public static final class MedicalEntry implements BaseColumns {

        public static final String TABLE_NAME = "med_manager";
        public static final String COLUMN_START_YEAR = "start_year";
        public static final String COLUMN_START_MONTH = "start_month";
        public static final String COLUMN_START_DAY = "start_day";
        public static final String COLUMN_START_HOUR = "start_hour";
        public static final String COLUMN_START_MINUTE = "start_minute";
        public static final String COLUMN_END_YEAR = "end_year";
        public static final String COLUMN_END_MONTH = "end_month";
        public static final String COLUMN_END_DAY = "end_day";
        public static final String COLUMN_END_HOUR = "end_hour";
        public static final String COLUMN_END_MINUTE = "end_minute";
        public static final String COLUMN_INTERVAL = "interval";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_DESCRIPTION = "description";


    }
}
