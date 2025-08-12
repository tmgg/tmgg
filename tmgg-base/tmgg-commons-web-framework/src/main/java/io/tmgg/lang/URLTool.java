package io.tmgg.lang;

public class URLTool {



    // 在原有url上增加参数，自动判断添加符号
    public static String appendParam(String url, String key, String value) {
        char join = url.contains("?") ? '&' : '?';

        return url + join + key + "=" + value;
    }


    // 获取基本路径， 如 https://baidu.com/a/b?id=1123, 得到 https://baidu.com
    public static String getBaseUrl(String url) {
        int i = getBaseUrlEndIndex(url);
        return url.substring(0, i);
    }

    public static int getBaseUrlEndIndex(String url) {
        int pi = url.indexOf("//"); // 协议
        int ei = pi >= 0 ? url.indexOf("/", pi + 2) : url.indexOf("/");
        if (ei > 0) {
            return ei;
        }
        int paramIndex = url.indexOf("?");
        if (paramIndex > 0) {
            return paramIndex;
        }
        return url.length();
    }


    // 是否包含基础url
    public static boolean hasBaseUrl(String url) {
        return url.startsWith("https://") || url.startsWith("http://") || url.startsWith("//") || url.startsWith("ws://");
    }

    public static String getPath(String url) {
        int i = getBaseUrlEndIndex(url);

        return url.substring(i);
    }
}
