package com.racine.cleancalls.net.HttpApi;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Shawn Racine.
 */
public class JsonHttpApi extends AbsHttpApi<String> {
    private String paramsEncoding = "UTF-8";

    public JsonHttpApi(String url, String params) {
        super(url, params);
    }

    @Override
    protected Map<String, String> requestHeader() {
        Map<String, String> header = new HashMap<>();
//        header.put("Cookie", Cookie);
        header.put("Content-Type", "application/json");
        return header;
    }

    @Override
    protected byte[] encodeParameters() throws UnsupportedEncodingException {
        return ((String) request.params).getBytes(paramsEncoding);
    }
}
