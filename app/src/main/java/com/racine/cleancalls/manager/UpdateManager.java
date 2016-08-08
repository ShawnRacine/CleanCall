package com.racine.cleancalls.manager;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.racine.cleancalls.R;
import com.racine.cleancalls.net.HttpCache;
import com.racine.cleancalls.net.ThreadTask.VoidThreadTask;
import com.racine.cleancalls.utils.IntentUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;

/**
 * @author Shawn Racine.
 */
public class UpdateManager {
    private Context context;

    private File apkFile;

    private NotificationManager notificationManager;
    private Notification notify;

    private boolean stopDownLoad;

    private static final int LOAD_ERROR = 1;
    private static final int LOADING = 2;
    private static final int LOAD_CANCEL = 3;
    private static final int LOAD_FINISH = 4;

    private static final int NOTIFICATION_FLAG = 1;
    private final int SDCardAvailable = 10 * 1024 * 1024;
    private final String apkName = "Ifeng.apk";

    private DecimalFormat df = new DecimalFormat("0.0");

    private int increment;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case LOAD_ERROR:
                    notify.contentView.setTextViewText(R.id.noti_tip, "Download Failed!");
                    notify.contentView.setTextViewText(R.id.noti_tv, "");
                    notify.contentView.setProgressBar(R.id.noti_pd, 100, 0, false);
                    notificationManager.notify(NOTIFICATION_FLAG, notify);
                    break;
                case LOADING:
                    int current = msg.arg1;
                    int total = msg.arg2;
                    notify.contentView.setTextViewText(R.id.noti_tip, "Downloading the latest version...");
                    notify.contentView.setTextViewText(R.id.noti_tv, "Completed " + df.format((double) current / (double) total * 100) + "%");
                    notify.contentView.setProgressBar(R.id.noti_pd, total, current, false);
                    notificationManager.notify(NOTIFICATION_FLAG, notify);
                    break;
                case LOAD_CANCEL:
                    notify.contentView.setTextViewText(R.id.noti_tip, "Download Failed!");
                    notify.contentView.setTextViewText(R.id.noti_tv, "");
                    notify.contentView.setProgressBar(R.id.noti_pd, 100, 0, false);
                    notificationManager.notify(NOTIFICATION_FLAG, notify);
                    break;
                case LOAD_FINISH:
                    notify.contentView.setTextViewText(R.id.noti_tip, "Download Finished!");
                    notify.contentView.setTextViewText(R.id.noti_tv, "Completed 100%");
                    notify.contentView.setProgressBar(R.id.noti_pd, 100, 100, false);
                    notificationManager.notify(NOTIFICATION_FLAG, notify);
                    break;
            }
            if (msg.what == LOAD_FINISH) {
                notificationManager.cancel(NOTIFICATION_FLAG);
                installApk(apkFile);
            }
        }
    };

    public UpdateManager(Context context) {
        this.context = context;
    }

    public void doUpdate(final String downUrl) {
        if (increment > 0 && increment < 100) {
            return;
        }
        increment = 0;

        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        notify = new Notification();
        //
        RemoteViews contentView = new RemoteViews(context.getPackageName(), R.layout.notify_item);
        contentView.setProgressBar(R.id.noti_pd, 100, 0, false);
        //
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, new Intent(), 0);
        //
        notify.contentView = contentView;
        notify.contentIntent = pendingIntent;
        notify.icon = R.mipmap.ic_launcher;
        notify.tickerText = "Downloading the latest version";
        notify.when = System.currentTimeMillis();
        notify.number = 1;
        notify.flags |= Notification.FLAG_NO_CLEAR;

        notificationManager.notify(NOTIFICATION_FLAG, notify);
        //Download APK
        new VoidThreadTask() {
            @Override
            protected Void doInBackground(Void params) {
                try {
                    apkFile = getFileFromServer(downUrl);
                } catch (Exception e) {
                    handler.obtainMessage(LOAD_ERROR).sendToTarget();
                    return null;
                }
                if (null != apkFile && apkFile.length() > 0) {
                    handler.obtainMessage(LOAD_FINISH).sendToTarget();
                } else {
                    handler.obtainMessage(LOAD_ERROR).sendToTarget();
                }
                return null;
            }
        }.execute();
    }

    private File getFileFromServer(String path) throws Exception {
        File file;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            if (IntentUtils.getAvailableInternalMemorySize() < SDCardAvailable) {
                Toast.makeText(context, "Not enough storage space!", Toast.LENGTH_SHORT).show();
                return null;
            }
            file = HttpCache.getCustomFile(apkName);
        } else {
            file = new File(context.getCacheDir().getAbsolutePath() + File.separator + apkName);
        }
        if (null == file) {
            return null;
        }
        URL url = new URL(path);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(5000);

        handler.obtainMessage(LOADING, 0, conn.getContentLength()).sendToTarget();
        InputStream is = conn.getInputStream();

        FileOutputStream fos = new FileOutputStream(file);
        BufferedInputStream bis = new BufferedInputStream(is);
        byte[] buffer = new byte[1024];
        int len;
        int sum = 0;
        while ((len = bis.read(buffer)) != -1) {
            if (!stopDownLoad) {
                fos.write(buffer, 0, len);
                sum += len;

                int total = conn.getContentLength();
                if ((double) sum / (double) total * 100 > increment) {
                    handler.obtainMessage(LOADING, sum, conn.getContentLength()).sendToTarget();
                    increment++;
                }
            } else {
                fos.close();
                bis.close();
                is.close();
                handler.obtainMessage(LOAD_CANCEL).sendToTarget();
                return null;
            }
        }
        fos.close();
        bis.close();
        is.close();
        return file;
    }

    private void installApk(File file) {
        Intent intent = new Intent();

        intent.setAction(Intent.ACTION_VIEW);

        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
