package con.fire.smsdemo;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.CallLog;
import android.provider.Settings;
import android.telecom.PhoneAccount;
import android.telecom.PhoneAccountHandle;
import android.telecom.TelecomManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.util.Calendar;

import con.fire.smsdemo.databinding.ActivityCalllogBinding;
import con.fire.smsdemo.utils.PermissionUtils;

public class CallLogActivity extends AppCompatActivity {


    private static final int PERMISSION_REQUEST_CODE = 100;
    final String[] arrs = new String[]{Manifest.permission.WRITE_CALL_LOG, Manifest.permission.READ_PHONE_STATE};
    ActivityCalllogBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCalllogBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.btnInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkAndRequestPermissions();
            }
        });
    }

    private void checkAndRequestPermissions() {
        if (PermissionUtils.checkPermissionRationale(this, arrs)) {
            addDemo();
        } else {
            ActivityCompat.requestPermissions(this, arrs, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 权限被授予
                addDemo();

            } else {
                // 权限被拒绝
                showPermissionDile();
            }
        }
    }


    private void showPermissionDile() {
        Toast.makeText(this, "没有权限", Toast.LENGTH_SHORT).show();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("");
        builder.setMessage("打开设置页面设置?").setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                dialogInterface.dismiss();
            }
        }).setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                openAppSettings();
            }
        }).show();

    }

    private void openAppSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            // 有些设备可能不支持这个Intent，尝试更通用的设置
            intent = new Intent(Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS);
            startActivity(intent);
        }
    }


    private void addDemo() {

        String trim = binding.edtPhone.getText().toString().trim();
        if (TextUtils.isEmpty(trim)) {
            Toast.makeText(this, "请输入手机号", Toast.LENGTH_SHORT).show();
            return;
        }

        Calendar calendar = Calendar.getInstance();
        addCallLogEntry(trim, CallLog.Calls.INCOMING_TYPE, 1, calendar.getTimeInMillis());
    }

    private void addCallLogEntry(String number, int type, long duration, long date) {
        ContentValues values = new ContentValues();

        // 设置通话记录的基本信息
        values.put(CallLog.Calls.NUMBER, number);
        values.put(CallLog.Calls.TYPE, type);
        values.put(CallLog.Calls.DURATION, duration);
        values.put(CallLog.Calls.DATE, date);
        values.put(CallLog.Calls.NEW, 1); // 标记为新记录

        // 对于Android 10及以上版本，需要设置电话号码的归属地
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            values.put(CallLog.Calls.PHONE_ACCOUNT_ID, getDefaultPhoneAccountHandle());
        }

        try {
            // 插入通话记录
            getContentResolver().insert(CallLog.Calls.CONTENT_URI, values);
            Toast.makeText(this, "插入记录成功", Toast.LENGTH_SHORT).show();
        } catch (SecurityException e) {
            Toast.makeText(this, "插入记录失败", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            // 处理权限不足的情况
        }


    }

    // 获取默认的电话账户(Android 10+需要)
    @TargetApi(Build.VERSION_CODES.Q)
    private String getDefaultPhoneAccountHandle() {
        TelecomManager telecomManager = (TelecomManager) getSystemService(Context.TELECOM_SERVICE);
        if (telecomManager != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "没有权限", Toast.LENGTH_SHORT).show();
                return "";
            }
            PhoneAccountHandle handle = telecomManager.getDefaultOutgoingPhoneAccount(PhoneAccount.SCHEME_TEL);
            if (handle != null) {
                return handle.getId();
            }
        }
        return null;
    }
}
