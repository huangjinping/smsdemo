package con.fire.smsdemo;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SmsDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "SmsDatabase.db";
    private static final int DATABASE_VERSION = 1;

    // 表名和字段
    public static final String TABLE_SMS = "sms";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_ADDRESS = "address";
    public static final String COLUMN_BODY = "body";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_TYPE = "type"; // 1=收件箱, 2=发件箱

    private static final String CREATE_TABLE_SMS = "CREATE TABLE " + TABLE_SMS + " ("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_ADDRESS + " TEXT, "
            + COLUMN_BODY + " TEXT, "
            + COLUMN_DATE + " INTEGER, "
            + COLUMN_TYPE + " INTEGER)";

    public SmsDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_SMS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SMS);
        onCreate(db);
    }
}