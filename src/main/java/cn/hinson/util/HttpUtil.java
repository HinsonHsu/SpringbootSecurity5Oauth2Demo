package cn.hinson.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.utils.HttpClientUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
/**
 * Http get 和 post方法请求，借鉴自wll
 * @author H1nson
 * @version 2018/10/7 17:15:12
 */
public class HttpUtil {

    private static Logger LOGGER = LoggerFactory.getLogger(HttpClientUtils.class);

    private static int CONNECT_TIME_OUT = 40000;

    private static int READ_TIME_OUT = 40000;

    private static byte[] BUFFER = new byte[1024];

    public static final String DEFAULT_CHARSET = "UTF-8";

    public static JSONObject getJsonObject(String url) {
        String result = get(url);
        if (StringUtils.isEmpty(result)) {
            return null;
        }
        return JSONObject.parseObject(result);
    }

    public static String get(String url) {
        return get(url, null, DEFAULT_CHARSET);
    }

    public static String get(String url, String charset) {
        return get(url, null, charset);
    }

    public static String get(String url, Map<String, String> header, String charset) {
        return get(url, header, charset, CONNECT_TIME_OUT, READ_TIME_OUT);
    }

    public static String get(String url, Map<String, String> header,  String charset,
                             int connectTimeout, int readTimeout) {
        String result = "";
        try {
//            InetSocketAddress address = new InetSocketAddress("127.0.0.1", 1080);
//            Proxy proxy = new Proxy(Type.SOCKS, address);
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setUseCaches(false);
            connection.setConnectTimeout(connectTimeout);
            connection.setReadTimeout(readTimeout);

            if (header != null) {
                for (Map.Entry<String, String> entry : header.entrySet()) {
                    connection.setRequestProperty(entry.getKey(), entry.getValue());
                }
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int responseCode = connection.getResponseCode();
            if (responseCode < HttpURLConnection.HTTP_BAD_REQUEST) {
                InputStream is = connection.getInputStream();
                int readCount;
                while ((readCount = is.read(BUFFER)) > 0) {
                    out.write(BUFFER, 0, readCount);
                }
                is.close();
            } else {
                LOGGER.warn("{} http response code is {}", url, responseCode);
            }
            connection.disconnect();
            result = out.toString();
        } catch (IOException e) {
            LOGGER.error("{}", e.getMessage(), e);
        }
        return result;
    }

    public static JSONObject postJsonObject(String url, Map<String, String> params) {
        String result = post(url, params);
        if (result == null) {
            return null;
        }
        return JSONObject.parseObject(result);
    }

    public static String post(String url, Map<String, String> params) {
        return post(url, params, DEFAULT_CHARSET);
    }

    public static String post(String url, Map<String, String> params, String charset) {
        return post(url, params, null, charset);
    }

    public static String post(String url, Map<String, String> params, Map<String, String> header, String charset) {
        return post(url, params, header, charset, CONNECT_TIME_OUT, READ_TIME_OUT);
    }

    public static String post(String url, Map<String, String> params, Map<String, String> header,
                              String charset, int connectTimeout, int readTimeout) {
        String result = "";
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setUseCaches(false);
            connection.setConnectTimeout(connectTimeout);
            connection.setReadTimeout(readTimeout);

            if (header != null) {
                for (Map.Entry<String, String> entry : header.entrySet()) {
                    connection.setRequestProperty(entry.getKey(), entry.getValue());
                }
            }

            StringBuilder builder = new StringBuilder();
            if (params != null) {
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    builder.append(entry.getKey());
                    builder.append("=");
                    builder.append(entry.getValue());
                    builder.append("&");
                }
            }

            OutputStream out = connection.getOutputStream();
            out.write(builder.toString().getBytes(charset));
            out.flush();

            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            if (connection.getResponseCode() < HttpURLConnection.HTTP_BAD_REQUEST) {
                InputStream is = connection.getInputStream();
                int readCount;
                while ((readCount = is.read(BUFFER)) > 0) {
                    bout.write(BUFFER, 0, readCount);
                }
                is.close();
            }else {
                InputStream is = connection.getErrorStream();
                int readCount;
                while ((readCount = is.read(BUFFER)) > 0) {
                    bout.write(BUFFER, 0, readCount);
                }
                is.close();
            }
            connection.disconnect();
            result = bout.toString();
        } catch (IOException e) {
            LOGGER.error("{}", e.getMessage(), e);
        }
        return result;
    }
}
