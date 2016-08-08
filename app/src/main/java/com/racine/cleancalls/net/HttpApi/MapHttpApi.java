package com.racine.cleancalls.net.HttpApi;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Shawn Racine.
 */
public class MapHttpApi extends AbsHttpApi<Map<String, String>> {
    private String paramsEncoding = "UTF-8";

    public MapHttpApi(String url, Map<String, String> params) {
        super(url, params);
    }

    @Override
    protected Map<String, String> requestHeader() {
        Map<String, String> header = new HashMap<>();
//        header.put("Cookie", Cookie);
        header.put("Content-Type", "application/x-www-form-urlencoded");
        return header;
    }

    @Override
    protected byte[] encodeParameters() throws UnsupportedEncodingException {
        StringBuilder encodedParams = new StringBuilder();
        for (Map.Entry<String, String> entry : ((Map<String, String>) request.params).entrySet()) {
            encodedParams.append(URLEncoder.encode(entry.getKey(), paramsEncoding));
            encodedParams.append('=');
            encodedParams.append(URLEncoder.encode(entry.getValue(), paramsEncoding));
            encodedParams.append('&');
        }
        return encodedParams.toString().getBytes(paramsEncoding);
    }
}
