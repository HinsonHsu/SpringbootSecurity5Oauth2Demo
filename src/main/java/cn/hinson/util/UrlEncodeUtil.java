package cn.hinson.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by WLL on 10/6/18 7:48 PM
 */
public class UrlEncodeUtil {
    public static String encode(String before) {
        String after = null;
        try {
            String tmp = URLEncoder.encode(before, "utf-8");
            after = tmp.replaceAll("\\+", "%20");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return after;
    }
}
