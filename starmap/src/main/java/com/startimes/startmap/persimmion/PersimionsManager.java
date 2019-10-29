package com.startimes.startmap.persimmion;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;

import java.util.ArrayList;

/**
 * 地图权限管理类
 *
 * @author jack
 * @version 1.0
 * @since 2019/9/3 13:42
 */
public class PersimionsManager {
    /**
     * 定位权限回调{@link Activity#onRequestPermissionsResult(int, String[], int[])}中的requestCode
     */
    public static final int SDK_PERMISSION_REQUEST = 127;

    /**
     * 判断是否具备权限
     */
    @TargetApi(Build.VERSION_CODES.M)
    public static boolean isPersimmions(@NonNull Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
            if (context.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    /**
     * 申请权限
     */
    @TargetApi(Build.VERSION_CODES.M)
    public static void getPersimmions(@NonNull Activity context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ArrayList<String> permissions = new ArrayList<>();
            // 定位权限为必须权限，用户如果禁止，则每次进入都会申请
            // 定位精确位置
            if (context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
            }
            if (context.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
            }
            // 读写权限非必要权限(建议授予)只会申请一次，用户同意或者禁止，只会弹一次
            if (addPermission(context, permissions, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
//                String permissionInfo = "Manifest.permission.WRITE_EXTERNAL_STORAGE Deny \n";
            }
            if (permissions.size() > 0) {
                String[] strings = new String[permissions.size()];
                for (int i = 0; i < permissions.size(); i++) {
                    strings[i] = permissions.get(i);
                }
                context.requestPermissions(strings, SDK_PERMISSION_REQUEST);
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private static boolean addPermission(@NonNull Activity context, @NonNull ArrayList<String> permissionsList, @NonNull String permission) {
        // 如果应用没有获得对应权限,则添加到列表中,准备批量申请
        if (context.checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
            if (context.shouldShowRequestPermissionRationale(permission)) {
                return true;
            } else {
                permissionsList.add(permission);
                return false;
            }
        } else {
            return true;
        }
    }

}
