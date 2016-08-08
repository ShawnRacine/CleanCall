package com.racine.cleancalls.net.HttpApi.bean;

/**
 * Created by sunrx on 2016/6/7.
 */
public class Response {
    public RespHeader header;
    /**
     * <li>0</li>
     * <li>400</li>
     * <li>403</li>
     * <li>404</li>
     * <li>408</li>
     */
    public int resultCode;
    public String result;
    public String errorMsg;
}
