package com.racine.cleancalls.net;

import android.os.Environment;

import com.racine.cleancalls.net.ThreadTask.VoidThreadTask;
import com.racine.cleancalls.utils.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Shawn Racine.
 */
public class HttpCache {
    private static final String ROOT_DIR_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/houseliker";
    private static final String IMAGE_CACHE_DIR = ROOT_DIR_PATH + "/cache/imageloader";
    private static final String FILE_CACHE_DIR = ROOT_DIR_PATH + "/cache/list_cache";
    private static int lapsedTime;

    public static File getRootCacheDir() {
        File filePath = new File(ROOT_DIR_PATH);
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            if (!filePath.exists()) {
                filePath.mkdirs();
            }
        }
        return filePath;
    }

    public static File getCustomFile(String path) throws IOException {
        File filePath = new File(ROOT_DIR_PATH + File.separator + path);
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            if (!filePath.getParentFile().exists()) {
                filePath.getParentFile().mkdir();
            }
            if (!filePath.exists()) {
                filePath.createNewFile();
            }
        }
        return filePath;
    }

    public static File getCustomCacheDir(String path) {
        File filePath = new File(ROOT_DIR_PATH + File.separator + path);
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            if (!filePath.exists()) {
                filePath.mkdirs();
            }
        }
        return filePath;
    }

    public static File getImageCacheDir() {
        File filePath = new File(IMAGE_CACHE_DIR);
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            if (!filePath.exists()) {
                filePath.mkdirs();
            }
        }
        return filePath;
    }


    public static boolean validCache(long cookieLastTime, String url) {

        List<String> cachelist = new ArrayList<>();
        cachelist.add("/api/login/checkExists");

        if (cachelist.contains(url)) {
            return true;
        }
        if (System.currentTimeMillis() - cookieLastTime < lapsedTime * 1000) {
            return true;
        }
        return false;
    }

    /**
     * @param time
     */
    public static void setLapsedTime(String time) {
        if (StringUtils.canParseInt(time) && Integer.parseInt(time) > 0) {
            lapsedTime = Integer.parseInt(time) - 1;
        } else {
            lapsedTime = 29 * 60;
        }
    }

    public static String getCacheResponse(String hashcodeUrl, long cacheTime) {
        if (isExistDataCache(hashcodeUrl) && !isCacheDataFailure(hashcodeUrl, cacheTime)) {
            return readObject(hashcodeUrl);
        }
        return null;
    }


    private static boolean isExistDataCache(String cachefile) {
        boolean exist = false;
        File data = new File(FILE_CACHE_DIR, cachefile);
        if (data.exists())
            exist = true;
        return exist;
    }

    public static String readObject(String file) {
        if (!isExistDataCache(file))
            return null;
        FileInputStream fis = null;
        ObjectInputStream ois = null;
        File temp = null;
        try {
            temp = new File(FILE_CACHE_DIR, file);
            fis = new FileInputStream(temp);
            ois = new ObjectInputStream(fis);
            return (String) ois.readObject();
        } catch (Exception e) {
            if (null != temp) {
                temp.delete();
            }
        } finally {
            try {
                ois.close();
                fis.close();
            } catch (Exception e) {
            }
        }
        return null;
    }

    public static void saveObject(final String response, final String file) {
        new VoidThreadTask() {
            @Override
            protected Void doInBackground(Void params) {
                FileOutputStream fos = null;
                ObjectOutputStream oos = null;
                File filePath = null;
                try {
                    filePath = new File(FILE_CACHE_DIR);
                    if (!filePath.exists()) {
                        filePath.mkdirs();
                    }
                    filePath = new File(FILE_CACHE_DIR, file);
                    fos = new FileOutputStream(filePath, false);
                    oos = new ObjectOutputStream(fos);
                    oos.writeObject(response);
                    oos.flush();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        oos.close();
                    } catch (Exception e) {
                    }
                    try {
                        fos.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return null;
            }
        }.execute();
    }

    public static String getHashCodeUrl(String url, Object params) {
        String hashcodeUrl = (url + params).hashCode() + "";
        return hashcodeUrl;
    }

    public static long getCacheValidTime(String httpUrl) {
        if (StringUtils.isNullOrEmpty(httpUrl)) {
            return -1;
        }

        Map<String, Long> timeMap = new HashMap<String, Long>();

        timeMap.put("/finance/calc/test", 30 * 60 * 1000l);

        if (null != timeMap.get(httpUrl)) {
            return timeMap.get(httpUrl);
        } else {
            return -1;
        }
    }

    public static boolean isCacheDataFailure(String cachefile, long cacheFail) {
        boolean failure = false;
        File data = new File(FILE_CACHE_DIR, cachefile);
        if (data.exists() && (System.currentTimeMillis() - data.lastModified()) > cacheFail)
            failure = true;
        return failure;
    }
}
