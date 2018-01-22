package com.medella.android.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.*;
import android.provider.BaseColumns;

import com.medella.android.listeners.OnDatabaseChangedListener;

public class DBHelper extends SQLiteOpenHelper {
    private Context mContext;

    private static OnDatabaseChangedListener mOnDatabaseChangedListener;

    public static final String DATABASE_NAME = "Medella.db";
    private static final int DATABASE_VERSION = 1;

    public static abstract class DBHelperItem implements BaseColumns {
        //Profile Table
        public static final String PROFILE_TABLE_NAME = "profile_table";
        public static final String COLUMN_NAME_PROFILE_ID = "profile_id";
        public static final String COLUMN_NAME_PROFILE_NAME = "profile_name";
        public static final String COLUMN_NAME_PROFILE_EMAIL = "email";
        public static final String COLUMN_NAME_PROFILE_PASSWORD = "password";
        public static final String COLUMN_NAME_PROFILE_SECURITY_QUESTION_1 = "security_question_1";
        public static final String COLUMN_NAME_PROFILE_SECURITY_QUESTION_2 = "security_question_2";
        public static final String COLUMN_NAME_PROFILE_SECURITY_QUESTION_3 = "security_question_3";
        public static final String COLUMN_NAME_PROFILE_SECURITY_ANSWER_1 = "security_answer_1";
        public static final String COLUMN_NAME_PROFILE_SECURITY_ANSWER_2 = "security_answer_2";
        public static final String COLUMN_NAME_PROFILE_SECURITY_ANSWER_3 = "security_answer_3";
        public static final String COLUMN_NAME_PROFILE_BIRTHDATE = "birthdate";
        public static final String COLUMN_NAME_HEIGHT = "height";

        //Health Activity Table
        public static final String HEALTH_ACTIVITY_TABLE_NAME = "health_activity_table";
        public static final String COLUMN_NAME_HEALTH_ACTIVITY_ID = "health_activity_id";
        //PROFILE_ID will be added to CREATE statement - will be foreign key
        public static final String COLUMN_NAME_HEALTH_ACTIVITY_TITLE = "health_activity_title";
        public static final String COLUMN_NAME_HEALTH_PAIN_INTENSITY = "pain_intensity";
        public static final String COLUMN_NAME_HEALTH_BODY_TEMPERATURE = "body_temperature";
        public static final String COLUMN_NAME_HEALTH_ACTIVITY_LOCATION = "activity_location";
        public static final String COLUMN_NAME_HEALTH_ACTIVITY_DESCRIPTION = "activity_description";
        public static final String COLUMN_NAME_HEALTH_ACTIVITY_DATE = "activity_date";
        public static final String COLUMN_NAME_WEIGHT = "weight";
        public static final String COLUMN_NAME_HEALTH_MEDICATION_BRAND = "medication_brand";
        public static final String COLUMN_NAME_HEALTH_MEDICATION_DOSAGE_AMOUNT = "medication_dosage_amount";
        public static final String COLUMN_NAME_HEALTH_MEDICATION_DOSAGE_UNIT = "medication_dosage_unit";
        public static final String COLUMN_NAME_HEALTH_SYSTOLIC = "systolic";
        public static final String COLUMN_NAME_HEALTH_DIASTOLIC = "diastolic";
        public static final String COLUMN_NAME_HEALTH_HEART_RATE = "heart_rate";

        //Photo Table
        public static final String PHOTO_TABLE_NAME = "photo_table";
        public static final String COLUMN_NAME_PHOTO_ID = "photo_id";
        //PROFILE_ID will be added to CREATE statement - will be foreign key
        //ACTIVITY_ID will be added to CREATE statement - will be foreign key
        public static final String COLUMN_NAME_PHOTO_NAME = "photo_name";
        public static final String COLUMN_NAME_PHOTO_DATE_ADDED = "photo_date_added";
        public static final String COLUMN_NAME_PHOTO_FILEPATH = "photo_filepath";

        //Voice Memo Table
        public static final String VOICE_MEMO_TABLE_NAME = "voice_memo_table";
        public static final String COLUMN_NAME_VOICE_MEMO_ID = "voice_memo_id"; //MemoId will be Integer Primary Key
        //PROFILE_ID will be added to CREATE statement - will be foreign key
        //ACTIVITY_ID will be added to CREATE statement - will be foreign key
        public static final String COLUMN_NAME_VOICE_MEMO_TITLE = "memo_title";
        public static final String COLUMN_NAME_VOICE_MEMO_FILEPATH = "memo_filepath"; //LENGTHY?
        public static final String COLUMN_NAME_VOICE_MEMO_DURATION = "memo_duration"; //MemoDuration will be integer? - check back SoundRecorder
        public static final String COLUMN_NAME_VOICE_MEMO_DATE_ADDED = "memo_date_added"; //DateCreated will be date
    }

    // CREATE TABLE statements
    // MISSING: security questions
    private static final String SQL_CREATE_PROFILE_TABLE =
            "CREATE TABLE " + DBHelperItem.PROFILE_TABLE_NAME + " (" +
                    DBHelperItem.COLUMN_NAME_PROFILE_ID + " INTEGER PRIMARY KEY, " + // auto-increment?
                    DBHelperItem.COLUMN_NAME_PROFILE_NAME + " TEXT, " +
                    DBHelperItem.COLUMN_NAME_PROFILE_EMAIL + " TEXT, " +
                    DBHelperItem.COLUMN_NAME_PROFILE_PASSWORD + " TEXT, " +
                    DBHelperItem.COLUMN_NAME_PROFILE_SECURITY_ANSWER_1 + " TEXT, " +
                    DBHelperItem.COLUMN_NAME_PROFILE_SECURITY_ANSWER_2 + " TEXT, " +
                    DBHelperItem.COLUMN_NAME_PROFILE_SECURITY_ANSWER_3 + " TEXT, " +
                    DBHelperItem.COLUMN_NAME_PROFILE_BIRTHDATE + " INTEGER, " +
                    DBHelperItem.COLUMN_NAME_HEIGHT + " FLOAT(3,2)" + ")"; // float is sketchy - may not work?

    // MISSING: foreign keys (profile id)
    private static final String SQL_CREATE_HEALTH_ACTIVITY_TABLE =
            "CREATE TABLE " + DBHelperItem.HEALTH_ACTIVITY_TABLE_NAME + " (" +
                    DBHelperItem.COLUMN_NAME_HEALTH_ACTIVITY_ID + " INTEGER PRIMARY KEY, " + // auto-increment?
                    DBHelperItem.COLUMN_NAME_HEALTH_ACTIVITY_TITLE + " TEXT, " +
                    DBHelperItem.COLUMN_NAME_HEALTH_PAIN_INTENSITY + " INTEGER, " +
                    DBHelperItem.COLUMN_NAME_HEALTH_BODY_TEMPERATURE + " FLOAT(2,1), " + // may not work?
                    DBHelperItem.COLUMN_NAME_HEALTH_ACTIVITY_LOCATION + " TEXT, " +
                    DBHelperItem.COLUMN_NAME_HEALTH_ACTIVITY_DESCRIPTION + " TEXT, " +
                    DBHelperItem.COLUMN_NAME_HEALTH_ACTIVITY_DATE + " INTEGER, " +
                    DBHelperItem.COLUMN_NAME_WEIGHT + " FLOAT(3,2), " + // may not work?
                    DBHelperItem.COLUMN_NAME_HEALTH_MEDICATION_BRAND + " TEXT, " +
                    DBHelperItem.COLUMN_NAME_HEALTH_MEDICATION_DOSAGE_AMOUNT  + " INTEGER, " +
                    DBHelperItem.COLUMN_NAME_HEALTH_MEDICATION_DOSAGE_UNIT + " TEXT, " +
                    DBHelperItem.COLUMN_NAME_HEALTH_SYSTOLIC + " INTEGER, " +
                    DBHelperItem.COLUMN_NAME_HEALTH_DIASTOLIC + " INTEGER, " +
                    DBHelperItem.COLUMN_NAME_HEALTH_HEART_RATE + " INTEGER" + ")";

    // MISSING: foreign keys (profile id, activity id)
    private static final String SQL_CREATE_PHOTO_TABLE =
            "CREATE TABLE " + DBHelperItem.PHOTO_TABLE_NAME + " (" +
                    DBHelperItem.COLUMN_NAME_PHOTO_ID + " INTEGER PRIMARY KEY, " + // auto-increment?
                    DBHelperItem.COLUMN_NAME_PHOTO_NAME + " TEXT, " +
                    DBHelperItem.COLUMN_NAME_PHOTO_DATE_ADDED + " INTEGER, " +
                    DBHelperItem.COLUMN_NAME_PHOTO_FILEPATH + " TEXT" + ")";

    // MISSING: foreign keys (activity id)
    private static final String SQL_CREATE_VOICE_MEMO_TABLE =
            "CREATE TABLE " + DBHelperItem.VOICE_MEMO_TABLE_NAME + " (" +
                    DBHelperItem.COLUMN_NAME_VOICE_MEMO_ID + " INTEGER PRIMARY KEY, " +
                    DBHelperItem.COLUMN_NAME_VOICE_MEMO_TITLE + "TEXT, " +
                    DBHelperItem.COLUMN_NAME_VOICE_MEMO_FILEPATH + "TEXT, " +
                    DBHelperItem.COLUMN_NAME_VOICE_MEMO_DURATION + " INTEGER ," +
                    DBHelperItem.COLUMN_NAME_VOICE_MEMO_DATE_ADDED + " INTEGER " + ")";

    @SuppressWarnings("unused")
    private static final String SQL_DELETE_PROFILE_TABLE = "DROP TABLE IF EXISTS " + DBHelperItem.PROFILE_TABLE_NAME;

    @SuppressWarnings("unused")
    private static final String SQL_DELETE_ACTIVITY_TABLE = "DROP TABLE IF EXISTS " + DBHelperItem.HEALTH_ACTIVITY_TABLE_NAME;

    @SuppressWarnings("unused")
    private static final String SQL_DELETE_PHOTO_TABLE = "DROP TABLE IF EXISTS " + DBHelperItem.PHOTO_TABLE_NAME;

    @SuppressWarnings("unused")
    private static final String SQL_DELETE_VOICE_MEMO_TABLE = "DROP TABLE IF EXISTS " + DBHelperItem.VOICE_MEMO_TABLE_NAME;

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_PROFILE_TABLE);
        db.execSQL(SQL_CREATE_HEALTH_ACTIVITY_TABLE);
        db.execSQL(SQL_CREATE_PHOTO_TABLE);
        db.execSQL(SQL_CREATE_VOICE_MEMO_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }

    public static void setOnDatabaseChangedListener(OnDatabaseChangedListener listener) {
        mOnDatabaseChangedListener = listener;
    }

    /*
    public VoiceMemoItem getVoiceMemoAt(int position) {
        SQLiteDatabase db = getReadableDatabase();
        String[] projection = {
                DBHelperItem._ID, //BEWARE BEWARE BEWARE
                DBHelperItem.COLUMN_NAME_VOICE_MEMO_TITLE,
                DBHelperItem.COLUMN_NAME_VOICE_MEMO_FILEPATH,
                DBHelperItem.COLUMN_NAME_VOICE_MEMO_DURATION,
                DBHelperItem.COLUMN_NAME_VOICE_MEMO_DATE_ADDED
        };
        Cursor c = db.query(DBHelperItem.VOICE_MEMO_TABLE_NAME, projection, null, null, null, null, null);
        if (c.moveToPosition(position)) {
            VoiceMemoItem item = new VoiceMemoItem();
            // MUST RECHECK METHODS ( setId, setName, etc...)
            item.setId(c.getInt(c.getColumnIndex(DBHelperItem._ID))); // BEWARE BEWARE BEWARE
            item.setName(c.getString(c.getColumnIndex(DBHelperItem.COLUMN_NAME_VOICE_MEMO_TITLE)));
            item.setFilePath(c.getString(c.getColumnIndex(DBHelperItem.COLUMN_NAME_VOICE_MEMO_FILEPATH)));
            item.setLength(c.getInt(c.getColumnIndex(DBHelperItem.COLUMN_NAME_VOICE_MEMO_DURATION)));
            item.setTime(c.getLong(c.getColumnIndex(DBHelperItem.COLUMN_NAME_VOICE_MEMO_DATE_ADDED)));
            c.close();
            return item;
        }
        return null;
    }

    public void removeVoiceMemoWithId(int id) {
        SQLiteDatabase db = getWritableDatabase();
        String[] whereArgs = { String.valueOf(id) };
        db.delete(DBHelperItem.VOICE_MEMO_TABLE_NAME, "_ID=?", whereArgs); //ID - BEWARE BEWARE BEWARE
    }

    public int getMemoCount() {
        SQLiteDatabase db = getReadableDatabase();
        String[] projection = { DBHelperItem._ID }; //BEWARE BEWARE BEWARE
        Cursor c = db.query(DBHelperItem.VOICE_MEMO_TABLE_NAME, projection, null, null, null, null, null);
        int count = c.getCount();
        c.close();
        return count;
    }

    public long addVoiceMemo(String recordingName, String filePath, long length) {

        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(DBHelperItem.COLUMN_NAME_VOICE_MEMO_TITLE, recordingName);
        cv.put(DBHelperItem.COLUMN_NAME_VOICE_MEMO_FILEPATH, filePath);
        cv.put(DBHelperItem.COLUMN_NAME_VOICE_MEMO_DURATION, length);
        cv.put(DBHelperItem.COLUMN_NAME_VOICE_MEMO_DATE_ADDED, System.currentTimeMillis());
        long rowId = db.insert(DBHelperItem.VOICE_MEMO_TABLE_NAME, null, cv);

        if (mOnDatabaseChangedListener != null) {
            mOnDatabaseChangedListener.onNewDatabaseEntryAdded();
        }

        return rowId;
    }

    public void renameVoiceMemo(VoiceMemoItem item, String memoName, String filePath) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(DBHelperItem.COLUMN_NAME_VOICE_MEMO_TITLE, memoName);
        cv.put(DBHelperItem.COLUMN_NAME_VOICE_MEMO_FILEPATH, filePath);
        db.update(DBHelperItem.VOICE_MEMO_TABLE_NAME, cv,
                DBHelperItem._ID + "=" + item.getId(), null); //ID - BEWARE BEWARE BEWARE

        if (mOnDatabaseChangedListener != null) {
            mOnDatabaseChangedListener.onDatabaseEntryRenamed();
        }
    }
    */
}
