package com.racine.cleancalls.net.HttpApi.bean;

import java.util.List;

/**
 * @author Shawn Racine.
 */
public class RespHeader {
    /**
     * Response.
     */
    public String Accept_Ranges;//	表明服务器是否支持指定范围请求及哪种类型的分段请求	Accept_Ranges: bytes
    public String Age;//	从原始服务器到代理缓存形成的估算时间（以秒计，非负）	Age: 12
    public String Allow;//	对某网络资源的有效的请求行为，不允许则返回405	Allow: GET, HEAD
    public String Cache_Control;//	告诉所有的缓存机制是否可以缓存及哪种类型	Cache_Control: no_cache
    public String Content_Encoding;//	web服务器支持的返回内容压缩编码类型。	Content_Encoding: gzip
    public String Content_Language;//	响应体的语言	Content_Language: en,zh
    public String Content_Length;//	响应体的长度	Content_Length: 348
    public String Content_Location;// 请求资源可替代的备用的另一地址	Content_Location: /index.htm
    public String Content_MD5;//	返回资源的MD5校验值	Content_MD5: Q2hlY2sgSW50ZWdyaXR5IQ==
    public String Content_Range;//	在整个返回体中本部分的字节位置	Content_Range: bytes 21010_47021/47022
    public String Content_Type;//	返回内容的MIME类型	Content_Type: text/html; charset=utf_8
    public String Date;//	原始服务器消息发出的时间	Date: Tue, 15 Nov 2010 08:12:31 GMT
    public String ETag;//	请求变量的实体标签的当前值	ETag: “737060cd8c284d8af7ad3082f209582d”
    public String Expires;//	响应过期的日期和时间	Expires: Thu, 01 Dec 2010 16:00:00 GMT
    public String Last_Modified;//	请求资源的最后修改时间	Last_Modified: Tue, 15 Nov 2010 12:45:26 GMT
    public String Location;//	用来重定向接收方到非请求URL的位置来完成请求或标识新的资源	Location: http://www.zcmhi.com/archives/94.html
    public String Pragma;//	包括实现特定的指令，它可应用到响应链上的任何接收方	Pragma: no_cache
    public String Proxy_Authenticate;//	它指出认证方案和可应用到代理的该URL上的参数	Proxy_Authenticate: Basic
    public String refresh;//	应用于重定向或一个新的资源被创造，在5秒之后重定向（由网景提出，被大部分浏览器支持）
    public String Retry_After;//	如果实体暂时不可取，通知客户端在指定时间之后再次尝试	Retry_After: 120
    public String Server;//	web服务器软件名称	Server: Apache/1.3.27 (Unix) (Red_Hat/Linux)
    public List<String> Set_Cookie;//	设置Http Cookie	Set_Cookie: UserID=JohnDoe; Max_Age=3600; Version=1
    public String Trailer;//	指出头域在分块传输编码的尾部存在	Trailer: Max_Forwards
    public String Transfer_Encoding;//	文件传输编码	Transfer_Encoding:chunked
    public String Vary;//	告诉下游代理是使用缓存响应还是从原始服务器请求	Vary: *
    public String Via;//	告知代理客户端响应是通过哪里发送的	Via: 1.0 fred, 1.1 nowhere.com (Apache/1.1)
    public String Warning;//	警告实体可能存在的问题	Warning: 199 Miscellaneous warning
    public String WWW_Authenticate;//	表明客户端请求实体应该使用的授权方案	WWW_Authenticate: Basic
}
