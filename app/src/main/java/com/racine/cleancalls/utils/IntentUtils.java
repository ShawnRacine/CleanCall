package com.racine.cleancalls.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Environment;
import android.os.StatFs;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import java.io.File;
import java.util.List;

public class IntentUtils {

    public static Intent createAlbumIntent() {
        if (android.os.Build.VERSION.SDK_INT >= 19) {
            Intent intent = new Intent("android.intent.action.PICK", null);
            intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
            return intent;
        } else {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            Intent chooseIntent = Intent.createChooser(intent, null);
            return chooseIntent;
        }
    }

    public static Intent createShotIntent(File tempFile) {
        if (isCameraCanUse()) {
            Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
            Uri uri = Uri.fromFile(tempFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            return intent;

        } else {
            return null;
        }
    }

    public static boolean isCameraCanUse() {
        boolean canUse = true;
        Camera mCamera = null;
        try {
            mCamera = Camera.open();
            Log.e("camera", "can open");
        } catch (Exception e) {
            Log.e("camera", "can't open");
            canUse = false;
        }
        if (canUse) {
            if (null != mCamera) {
                mCamera.release();
                mCamera = null;
            }
        }
        return canUse;
    }

    public static void sendSMS(Context mContext, String phone, String content) {
        if (StringUtils.isNullOrEmpty(phone)) {
            return;
        }
        Intent sendIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + phone));
        if (mContext.getPackageManager().resolveActivity(sendIntent, 0) == null) {
            Toast.makeText(mContext, "nonsupport", Toast.LENGTH_SHORT).show();
            return;
        }
        sendIntent.putExtra("sms_body", content);
        mContext.startActivity(sendIntent);
    }

    public static void sendEmail(Context mContext, String mail, String content, String subject) {
        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + mail));

        if (mContext.getPackageManager().resolveActivity(intent, 0) == null) {
            Toast.makeText(mContext, "nonsupport", Toast.LENGTH_SHORT).show();
            return;
        }
        intent.putExtra(Intent.EXTRA_TEXT, content);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        // intent.setType("text/plain");
        mContext.startActivity(intent);
    }

    public static boolean isInstall(Context context, String packageName) {
        PackageManager pckMan;
        pckMan = context.getPackageManager();
        List<PackageInfo> packs = pckMan.getInstalledPackages(0);
        for (int i = 0; i < packs.size(); i++) {
            PackageInfo p = packs.get(i);
            if (packageName.equals(p.packageName)) {
                return true;
            }
        }
        return false;
    }

    public static void setupApk(Context context, String installName) {
        String fileName = context.getApplicationContext().getFilesDir() + "/" + installName.trim();
        Uri uri = Uri.fromFile(new File(fileName));
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "application/vnd.android.package-archive");
        context.startActivity(intent);
    }

    public static void hideSoftKeyBoard(Activity activity) {
        final View v = activity.getWindow().peekDecorView();
        if (v != null && v.getWindowToken() != null) {
            try {
                ((InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE))
                        .hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(),
                                InputMethodManager.HIDE_NOT_ALWAYS);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void openApk(Context context, String name, String url) {
        if (isInstall(context, name)) {
            PackageManager pckMan = context.getPackageManager();
            Intent intent = pckMan.getLaunchIntentForPackage(name);
            context.startActivity(intent);
        } else {

        }

    }

    public static long getAvailableInternalMemorySize() {
        long availableSpace = -1l;
        try {
            String path = Environment.getDataDirectory().getPath();
            StatFs stat = new StatFs(path);
            long blockSize = stat.getBlockSize();
            long availableBlocks = stat.getAvailableBlocks();
            availableSpace = availableBlocks * blockSize;
        } catch (Exception e) {
            Log.e("TAG", "getAvailableInternalMemorySize: " + e.toString());
        }
        return availableSpace;
    }
}
