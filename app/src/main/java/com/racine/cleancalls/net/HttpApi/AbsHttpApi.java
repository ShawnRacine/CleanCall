package com.racine.cleancalls.net.HttpApi;

import android.util.Log;

import com.racine.cleancalls.IApplication;
import com.racine.cleancalls.R;
import com.racine.cleancalls.net.HttpApi.bean.Request;
import com.racine.cleancalls.net.HttpApi.bean.RespHeader;
import com.racine.cleancalls.net.HttpApi.bean.Response;
import com.racine.cleancalls.net.HttpCache;
import com.racine.cleancalls.utils.NetUtils;
import com.racine.cleancalls.utils.StringUtils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author Shawn Racine.
 */
public abstract class AbsHttpApi<T> {
    private static final String TAG = "HttpApi";

    protected HttpURLConnection con = null;
    protected URL url = null;

    protected Request request;
    protected Response response;
    private long CookieLastTime;

    private static LinkedBlockingQueue cacheQueue;

    public AbsHttpApi(String url, T params) {
        request = new Request();

        request.url = url;

        request.params = params;

        request.header = requestHeader();

        if (cacheQueue == null) {
            cacheQueue = new LinkedBlockingQueue(10);
        }
    }

    public Response POST() {

        response = new Response();

        if (!NetUtils.isConnected(IApplication.getInstance())) {
            response.resultCode = 400;
            response.errorMsg = IApplication.getInstance().getResources().getString(R.string.no_network);
            return response;
        }

        long cacheTime = HttpCache.getCacheValidTime(request.url);
        String hashcodeUrl = HttpCache.getHashCodeUrl(request.url, request.params);
        if (cacheTime != -1) {
            String result = HttpCache.getCacheResponse(hashcodeUrl, cacheTime);
            if (!StringUtils.isNullOrEmpty(result)) {
                response.result = result;
                return response;
            }
        }

        if (cacheQueue.contains(hashcodeUrl)) {
            response.resultCode = 444;
            response.errorMsg = IApplication.getInstance().getResources().getString(R.string.repeat_request);
            return response;
        }
        addToCacheQueue(hashcodeUrl);

        try {
            //if _url could not be parsed as a URL,throw MalformedURLException.
            url = new URL(request.url);

            con = openConnection("POST");

            addHeaders(request.header);

            DataOutputStream out = new DataOutputStream(con.getOutputStream());

            out.write(encodeParameters());

            InputStreamReader in = new InputStreamReader(con.getInputStream());
            BufferedReader buffer = new BufferedReader(in);

            StringBuffer resultBuffer = new StringBuffer();

            String inputLine;
            while (((inputLine = buffer.readLine()) != null)) {
                resultBuffer.append(inputLine);
            }
            response.result = resultBuffer.toString();

            getHeaderFields();

            in.close();
            out.flush();
            out.close();

            if (cacheTime != -1 && StringUtils.isNullOrEmpty(response.result)) {
                HttpCache.saveObject(response.result, hashcodeUrl);
            }

            removeFromCacheQueue(hashcodeUrl);

            return response;
        } catch (MalformedURLException e) {
            response.resultCode = 403;
            Log.e(TAG, "_url could not be parsed as a URL");
        } catch (SocketTimeoutException e) {
            response.resultCode = 408;
            Log.e(TAG, "SocketTimeoutException: " + e.toString());
        } catch (IOException e) {
            response.resultCode = 404;
            Log.e(TAG, "IOException: " + e.toString());
        }

        removeFromCacheQueue(hashcodeUrl);

        return response;
    }

    public Response GET() {
        response = new Response();

        if (!NetUtils.isConnected(IApplication.getInstance())) {
            response.resultCode = 400;
            response.errorMsg = IApplication.getInstance().getResources().getString(R.string.no_network);
            return response;
        }

        long cacheTime = HttpCache.getCacheValidTime(request.url);
        String hashcodeUrl = HttpCache.getHashCodeUrl(request.url, request.params);
        if (cacheTime != -1) {
            String result = HttpCache.getCacheResponse(hashcodeUrl, cacheTime);
            if (!StringUtils.isNullOrEmpty(result)) {
                response.result = result;
                return response;
            }
        }

        if (cacheQueue.contains(hashcodeUrl)) {
            response.resultCode = 444;
            response.errorMsg = IApplication.getInstance().getResources().getString(R.string.repeat_request);
            return response;
        }
        addToCacheQueue(hashcodeUrl);

        try {
            //if _url could not be parsed as a URL,throw MalformedURLException.
            url = new URL(request.url);

            con = openConnection("GET");

            addHeaders(request.header);

            InputStreamReader in = new InputStreamReader(con.getInputStream());
            BufferedReader buffer = new BufferedReader(in);

            StringBuffer resultBuffer = new StringBuffer();

            String inputLine;
            while (((inputLine = buffer.readLine()) != null)) {
                resultBuffer.append(inputLine);
            }
            response.result = resultBuffer.toString();

            getHeaderFields();

            in.close();

            if (cacheTime != -1 && StringUtils.isNullOrEmpty(response.result)) {
                HttpCache.saveObject(response.result, hashcodeUrl);
            }

            removeFromCacheQueue(hashcodeUrl);

            return response;
        } catch (MalformedURLException e) {
            response.resultCode = 403;
            Log.e(TAG, "_url could not be parsed as a URL");
        } catch (SocketTimeoutException e) {
            response.resultCode = 408;
            Log.e(TAG, "SocketTimeoutException: " + e.toString());
        } catch (IOException e) {
            response.resultCode = 404;
            Log.e(TAG, "IOException: " + e.toString());
        }

        removeFromCacheQueue(hashcodeUrl);

        return response;
    }

    private void addToCacheQueue(String hashcode) {
        if (!StringUtils.isNullOrEmpty(hashcode) && !cacheQueue.offer(hashcode)) {
            cacheQueue.poll();
            cacheQueue.offer(hashcode);
        }
    }

    private void removeFromCacheQueue(String hashcode) {
        if (cacheQueue.contains(hashcode)) {
            cacheQueue.remove(hashcode);
        }
    }

    private HttpURLConnection openConnection(String method) throws IOException {
        HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();

        urlConn.setDoOutput(true);
        urlConn.setDoInput(true);

        urlConn.setRequestMethod(method);
        urlConn.setUseCaches(false);
        urlConn.setConnectTimeout(3000);
        return urlConn;
    }

    protected abstract Map<String, String> requestHeader();

    private void addHeaders(Map<String, String> map) {
        for (Map.Entry<String, String> header : map.entrySet()) {
            con.addRequestProperty(header.getKey(), header.getValue());
        }
    }

    protected abstract byte[] encodeParameters() throws UnsupportedEncodingException;

    private void getHeaderFields() {
        response.header = new RespHeader();
        for (Map.Entry<String, List<String>> header : con.getHeaderFields().entrySet()) {
            if ("Set-Cookie".equals(header.getKey())) {
                response.header.Set_Cookie = header.getValue();
            } else if ("Content-Length".equals(header.getKey())) {
                response.header.Content_Length = header.getValue().get(0);
            } else if ("Server".equals(header.getKey())) {
                response.header.Server = header.getValue().get(0);
            } else if ("".equals(header.getKey())) {

            }
        }
        CookieLastTime = System.currentTimeMillis();
    }
}
