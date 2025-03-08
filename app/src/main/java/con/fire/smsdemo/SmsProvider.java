package con.fire.smsdemo;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

public class SmsProvider extends ContentProvider {
    // 定义 URI 匹配规则
    private static final String AUTHORITY = "com.example.smsapp.sms"; // 与 AndroidManifest.xml 中的 authorities 一致
    private static final String PATH_SMS = "sms";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + PATH_SMS);

    private static final int SMS_ALL = 1;  // 匹配所有短信
    private static final int SMS_ID = 2;   // 匹配单条短信

    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        uriMatcher.addURI(AUTHORITY, PATH_SMS, SMS_ALL);        // content://com.example.smsapp.sms/sms
        uriMatcher.addURI(AUTHORITY, PATH_SMS + "/#", SMS_ID);  // content://com.example.smsapp.sms/sms/1
    }

    private SmsDatabaseHelper dbHelper;

    @Override
    public boolean onCreate() {
        dbHelper = new SmsDatabaseHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor;

        switch (uriMatcher.match(uri)) {
            case SMS_ALL:
                cursor = db.query(SmsDatabaseHelper.TABLE_SMS, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case SMS_ID:
                String id = uri.getLastPathSegment();
                selection = SmsDatabaseHelper.COLUMN_ID + "=?";
                selectionArgs = new String[]{id};
                cursor = db.query(SmsDatabaseHelper.TABLE_SMS, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("未知 URI: " + uri);
        }

        // 通知内容观察者数据变化
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long rowId;

        switch (uriMatcher.match(uri)) {
            case SMS_ALL:
                rowId = db.insert(SmsDatabaseHelper.TABLE_SMS, null, values);
                if (rowId > 0) {
                    Uri newUri = ContentUris.withAppendedId(CONTENT_URI, rowId);
                    getContext().getContentResolver().notifyChange(newUri, null);
                    return newUri;
                }
                break;
            default:
                throw new IllegalArgumentException("未知 URI: " + uri);
        }
        return null;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rowsAffected;

        switch (uriMatcher.match(uri)) {
            case SMS_ALL:
                rowsAffected = db.update(SmsDatabaseHelper.TABLE_SMS, values, selection, selectionArgs);
                break;
            case SMS_ID:
                String id = uri.getLastPathSegment();
                selection = SmsDatabaseHelper.COLUMN_ID + "=?" + (selection == null ? "" : " AND (" + selection + ")");
                selectionArgs = appendIdToSelectionArgs(id, selectionArgs);
                rowsAffected = db.update(SmsDatabaseHelper.TABLE_SMS, values, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("未知 URI: " + uri);
        }

        if (rowsAffected > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsAffected;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rowsDeleted;

        switch (uriMatcher.match(uri)) {
            case SMS_ALL:
                rowsDeleted = db.delete(SmsDatabaseHelper.TABLE_SMS, selection, selectionArgs);
                break;
            case SMS_ID:
                String id = uri.getLastPathSegment();
                selection = SmsDatabaseHelper.COLUMN_ID + "=?" + (selection == null ? "" : " AND (" + selection + ")");
                selectionArgs = appendIdToSelectionArgs(id, selectionArgs);
                rowsDeleted = db.delete(SmsDatabaseHelper.TABLE_SMS, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("未知 URI: " + uri);
        }

        if (rowsDeleted > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)) {
            case SMS_ALL:
                return "vnd.android.cursor.dir/vnd." + AUTHORITY + ".sms";
            case SMS_ID:
                return "vnd.android.cursor.item/vnd." + AUTHORITY + ".sms";
            default:
                throw new IllegalArgumentException("未知 URI: " + uri);
        }
    }

    // 辅助方法：将 ID 添加到 selectionArgs 中
    private String[] appendIdToSelectionArgs(String id, String[] selectionArgs) {
        if (selectionArgs == null) {
            return new String[]{id};
        }
        String[] newArgs = new String[selectionArgs.length + 1];
        newArgs[0] = id;
        System.arraycopy(selectionArgs, 0, newArgs, 1, selectionArgs.length);
        return newArgs;
    }
}