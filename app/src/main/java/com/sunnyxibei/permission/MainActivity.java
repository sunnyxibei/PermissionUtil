package com.sunnyxibei.permission;

import android.annotation.TargetApi;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private List<String> deniedPermissions;
    private int request = 100;
    private boolean mHasStartSetting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mHasStartSetting) {
            initData();
            mHasStartSetting = false;
        }
    }

    private void initData() {
        deniedPermissions = getDeniedPermission(Permission.PHONE, Permission.STORAGE);
        if (deniedPermissions.size() <= 0) {
            //正常初始化
            initSelf();
        } else {
            getPermission();
        }
    }

    private List<String> getDeniedPermission(String[] phone, String[] storage) {
        List<String> permissions = new ArrayList<>();
        checkDeniedPermission(phone, permissions);
        checkDeniedPermission(storage, permissions);
        return permissions;
    }

    private void checkDeniedPermission(String[] pers, List<String> permissions) {
        try {
            for (String per : pers) {
                if (ContextCompat.checkSelfPermission(this, per) ==
                        PackageManager.PERMISSION_DENIED) {
                    permissions.add(per);
                }
            }
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void getPermission() {
        //请求动态权限
        if (deniedPermissions.size() > 0) {
            request++;
            requestPermissions(new String[]{deniedPermissions.get(0)}, request);
        } else {
            initSelf();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == request) {
            //判断权限弹框的结果
            boolean grant = true;
            for (int result : grantResults) {
                if (result == PackageManager.PERMISSION_DENIED) {
                    grant = false;
                    break;
                }
            }
            if (grant) {
                //用户已经授权
                Toast.makeText(this, "用户已经授权，循环进行下一个权限申请", Toast.LENGTH_SHORT).show();
                deniedPermissions.remove(permissions[0]);
                Toast.makeText(this, permissions[0] + "已经弹出队列", Toast.LENGTH_SHORT).show();
                getPermission();
            } else {
                //用户拒绝
                try {
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(
                            MainActivity.this, permissions[0])) {
                        Toast.makeText(this, permissions[0] + "不再弹框", Toast.LENGTH_SHORT).show();
                        //引导用户去设置页面
                        showSettingDialog();
                    } else {
                        //重新申请权限
                        showTipsDialog();
                    }
                } catch (Exception e) {
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void showTipsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("重新发起授权");
        builder.setTitle("授权该项权限可以让我们更好地为您服务");
        builder.setCancelable(false);
        builder.setPositiveButton("重新授权", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getPermission();
            }
        });
        builder.setNegativeButton("去消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.create().show();
    }

    private void showSettingDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("您已经点击不再弹框");
        builder.setMessage("请跳转到设置界面，手动打开权限");
        builder.setCancelable(false);
        builder.setPositiveButton("去设置", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startAppSetting();
                mHasStartSetting = true;
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                MainActivity.this.finish();
            }
        });
        builder.create().show();
    }

    private void startAppSetting() {
        Intent intent = new Intent();
        intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
        intent.setData(Uri.fromParts("package", getPackageName(), null));
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void initSelf() {
        Toast.makeText(this, "应用已经获取权限，并开始初始化", Toast.LENGTH_SHORT).show();
    }
}
