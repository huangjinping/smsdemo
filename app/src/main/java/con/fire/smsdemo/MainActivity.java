package con.fire.smsdemo;

import android.app.role.RoleManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import con.fire.smsdemo.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_ROLE_SMS = 2;

    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        binding.edtInsertNumber.setText("123456789");
        binding.edtCount.setText("10");
        binding.edtDeleteNumber.setText("123456789");
        checkAndRequestSmsRole();
//        checkDefaultSmsApp2();

        binding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        try {
                            String count = binding.edtCount.getText().toString().trim();
                            Integer cou = Integer.parseInt(count);
                            String phone = binding.edtInsertNumber.getText().toString().trim();

                            for (int i = 0; i < cou; i++) {

                                int finalI = i + 1;
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        binding.txtLog.setText("当前写入条目为" + finalI + "; 还有" + (cou - finalI) + "条未写入");

                                    }
                                });

                                String message = System.currentTimeMillis() + "Hello, this is a test mHello, this is a test message!Hello, this is a test message!Hello, this is a test message!Hello, this is a test message!Hello, this is a test message!essage!" + i;

                                writeSmsToSentBox(phone, message);

                                writeSmsToSentBoxWithDate(phone, message, "2025-03-01 14:30:00");

//                    writeSmsToSentBox("1234567890", System.currentTimeMillis() + "Hello, this is a test message!" + i);
                            }

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(MainActivity.this, "写入电话号码:" + phone + "写入条数" + count, Toast.LENGTH_SHORT).show();

                                }
                            });
                        } catch (Exception e) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(MainActivity.this, "请检查输入内容", Toast.LENGTH_SHORT).show();

                                }
                            });
                            e.printStackTrace();
                        }
                    }
                }.start();
            }
        });

        binding.buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phone = binding.edtDeleteNumber.getText().toString().trim();

                if (TextUtils.isEmpty(phone)) {
                    Toast.makeText(MainActivity.this, "请输入删除手机号", Toast.LENGTH_SHORT).show();
                    return;
                }
                deleteSmsByPhoneNumber(phone);
            }
        });
    }

//    private void writeSmsToInbox(String phoneNumber, String messageBody, long timestamp) {
//        ContentValues values = new ContentValues();
//        values.put(SmsDatabaseHelper.COLUMN_ADDRESS, "1234567890");
//        values.put(SmsDatabaseHelper.COLUMN_BODY, "Hello from SmsProvider");
//        values.put(SmsDatabaseHelper.COLUMN_DATE, System.currentTimeMillis());
//        values.put(SmsDatabaseHelper.COLUMN_TYPE, 2); // 2 表示发件箱
//        Uri newUri = getContentResolver().insert(SmsProvider.CONTENT_URI, values);
//        Log.d("SMS", "插入短信成功: " + newUri);
//    }

    private void deleteSmsByPhoneNumber(String phoneNumber) {
        try {
            ContentResolver contentResolver = getContentResolver();

            // 定义要删除的 URI，可以是所有短信或特定类型（如发件箱）
            Uri uri = Uri.parse("content://sms/"); // 所有短信
            // Uri uri = Uri.parse("content://sms/sent"); // 仅发件箱
            // Uri uri = Uri.parse("content://sms/inbox"); // 仅收件箱

            // 设置筛选条件：匹配指定电话号码
            String where = Telephony.Sms.ADDRESS + " = ?";
            String[] whereArgs = new String[]{phoneNumber};

            // 执行删除操作
            int rowsDeleted = contentResolver.delete(uri, where, whereArgs);

            if (rowsDeleted > 0) {
                Log.d("SMS", "成功删除 " + rowsDeleted + " 条短信，号码: " + phoneNumber);
                Toast.makeText(this, "成功删除 " + rowsDeleted + " 条短信，号码: " + phoneNumber, Toast.LENGTH_SHORT).show();
            } else {
                Log.d("SMS", "未找到匹配的短信，号码: " + phoneNumber);
                Toast.makeText(this, "未找到匹配的短信，号码: ", Toast.LENGTH_SHORT).show();
            }
        } catch (SecurityException e) {
            Log.e("SMS", "权限不足，无法删除短信: " + e.getMessage());
            Toast.makeText(this, "权限不足，无法删除短信:", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e("SMS", "删除短信时出错: " + e.getMessage());
            Toast.makeText(this, "删除短信时出错: ", Toast.LENGTH_SHORT).show();
        }
    }

    private void writeSmsToSentBoxWithDate(String phoneNumber, String message, String dateString) {
        try {
            ContentResolver contentResolver = getContentResolver();
            ContentValues values = new ContentValues();

            // 将指定日期字符串转换为时间戳
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            Date date = sdf.parse(dateString); // 解析日期字符串
            long timestamp = date.getTime();   // 获取毫秒时间戳

            // 设置短信字段
            values.put(Telephony.Sms.ADDRESS, phoneNumber);    // 接收者号码
            values.put(Telephony.Sms.BODY, message);           // 短信内容
            values.put(Telephony.Sms.DATE, timestamp);         // 指定时间戳
            values.put(Telephony.Sms.TYPE, Telephony.Sms.MESSAGE_TYPE_SENT); // 类型：已发送

            // 插入到发件箱
            Uri uri = Uri.parse("content://sms/sent");
            Uri insertedUri = contentResolver.insert(uri, values);

            if (insertedUri != null) {
                Log.d("SMS", "短信写入成功: " + insertedUri.toString() + ", 时间: " + dateString);
            } else {
                Log.e("SMS", "短信写入失败");
            }
        } catch (java.text.ParseException e) {
            Log.e("SMS", "日期解析错误: " + e.getMessage());
        } catch (SecurityException e) {
            Log.e("SMS", "权限不足: " + e.getMessage());
        } catch (Exception e) {
            Log.e("SMS", "写入短信时出错: " + e.getMessage());
        }
    }

    private void writeSmsToSentBox(String phoneNumber, String message) {
        try {
            ContentResolver contentResolver = getContentResolver();
            ContentValues values = new ContentValues();

            // 设置短信字段
            values.put(Telephony.Sms.ADDRESS, phoneNumber);    // 接收者号码
            values.put(Telephony.Sms.BODY, message);           // 短信内容
            values.put(Telephony.Sms.DATE, System.currentTimeMillis()); // 时间戳
            values.put(Telephony.Sms.TYPE, Telephony.Sms.MESSAGE_TYPE_SENT); // 类型：已发送

            // 插入到发件箱
            Uri uri = Uri.parse("content://sms/sent");
            Uri insertedUri = contentResolver.insert(uri, values);

            if (insertedUri != null) {
                Log.d("SMS", "短信写入成功: " + insertedUri.toString());
            } else {
                Log.e("SMS", "短信写入失败");
            }
        } catch (Exception e) {
            Log.e("SMS", "写入短信时出错: " + e.getMessage());
        }
    }

    private void checkDefaultSmsApp2() {
//        String myPackageName = getPackageName();
//        String defaultSmsApp = Telephony.Sms.getDefaultSmsPackage(this);
//
//        if (myPackageName.equals(defaultSmsApp)) {
//            Log.d("SMS", "本应用已是默认短信应用");
//            // 在这里可以执行写入短信等操作
//        } else {
//            Log.d("SMS", "本应用不是默认短信应用，请求用户设置");
//            requestDefaultSmsRole();
//        }
    }

    private void requestDefaultSmsRole() {
        Intent intent = new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
        intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, getPackageName());
        startActivityForResult(intent, REQUEST_ROLE_SMS);
    }


    private void checkAndRequestSmsRole() {
//        RoleManager roleManager = null;
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//            roleManager = getSystemService(RoleManager.class);
//            if (roleManager.isRoleHeld(RoleManager.ROLE_SMS)) {
//                Log.d("SMS", "本应用已是默认短信应用");
//                Toast.makeText(this, "本应用已是默认短信应用", Toast.LENGTH_SHORT).show();
//            } else {
//                Log.d("SMS", "请求用户设置为默认短信应用");
//                Toast.makeText(this, "请求用户设置为默认短信应用", Toast.LENGTH_SHORT).show();
//
//                Intent intent = roleManager.createRequestRoleIntent(RoleManager.ROLE_SMS);
//                startActivityForResult(intent, REQUEST_ROLE_SMS);
//            }
//        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            RoleManager roleManager = getSystemService(RoleManager.class);
            if (!roleManager.isRoleHeld(RoleManager.ROLE_SMS)) {
                Intent intent = roleManager.createRequestRoleIntent(RoleManager.ROLE_SMS);
                startActivityForResult(intent, REQUEST_ROLE_SMS);
            }
        } else {
            Intent intent = new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
            intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, getPackageName());
            startActivityForResult(intent, REQUEST_ROLE_SMS);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ROLE_SMS) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "用户已将本应用设置为默认短信应用", Toast.LENGTH_SHORT).show();

                Log.d("SMS", "用户已将本应用设置为默认短信应用");
            } else {
                Toast.makeText(this, "用户拒绝设置为默认短信应用", Toast.LENGTH_SHORT).show();

                Log.d("SMS", "用户拒绝设置为默认短信应用");
            }
        }
    }
}