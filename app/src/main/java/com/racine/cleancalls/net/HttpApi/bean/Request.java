package com.racine.cleancalls.net.HttpApi.bean;

import java.util.Map;

/**
 * @author Shawn Racine.
 */
public class Request<T> {
    public String url;
    public Map<String, String> header;
    public T params;
}
