package cn.hinson.service.imp;

import cn.hinson.util.HmacSha1Util;
import cn.hinson.util.HttpUtil;
import cn.hinson.util.UrlEncodeUtil;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.security.SignatureException;
import java.util.*;

/**
 * twitter 相关api
 *
 * @author H1nson
 * @version 2018/10/7 21:31:07
 */
public class TwitterServiceImp {
    private String oauthToken;
    private String oauthTokenSecret;

    protected final Log logger = LogFactory.getLog(this.getClass());

    public String getAuthString(String httpMethod, String baseUrl, Map<String, String> bodyParams, Map<String, String> headerParams) {
        String oauthConsumerKey = "fsbFHibUYg7eOWEwCwCFTFpM9";
        String oauthSignatureMethod = "HMAC-SHA1";
        String oauthNonce = getOauthNonce();
        String oauthTimestamp = String.valueOf(System.currentTimeMillis() / 1000);
        String oauthVersion = "1.0";
        String consumerSecret = "RICjgnsk1tOIXki5w2jTw6txf0NYdVYbgzYJd5MioW8fFuZBw9";

        Map<String, String> authParams = new HashMap<>();
        authParams.put("oauth_consumer_key", oauthConsumerKey);
        authParams.put("oauth_nonce", oauthNonce);
        authParams.put("oauth_signature_method", oauthSignatureMethod);
        authParams.put("oauth_timestamp", oauthTimestamp);
        authParams.put("oauth_version", oauthVersion);
        if (this.oauthToken != null) {
            authParams.put("oauth_token", this.oauthToken);
        }

        Map<String, String> signatureParams = new HashMap<>();
        signatureParams.putAll(authParams);
        if (bodyParams != null) {
            signatureParams.putAll(bodyParams);
        }
        if (headerParams != null) {
            signatureParams.putAll(headerParams);
        }

        String signature = getSignature(httpMethod, baseUrl, signatureParams, consumerSecret, this.oauthTokenSecret);

        authParams.put("oauth_signature", signature);
        String authString = getAuthStringByParams(authParams);

        return authString;
    }

    public Map<String, String> getOauthToken(String authString) {
        String requestTokenUrl = "https://api.twitter.com/oauth/request_token";
        Map<String, String> header = new HashMap<String, String>();
        header.put("Authorization", authString);
        String httpResult = HttpUtil.post(requestTokenUrl, null, header, "UTF-8");
        logger.info("WLL's log: OauthToken: " + httpResult);

        String[] authResults = httpResult.split("&");
        if (authResults[0].substring(0, authResults[0].indexOf('=')).equals("oauth_token")){
            String oauthToken = authResults[0].substring(authResults[0].indexOf('=') + 1);
            String oauthTokenSecret = authResults[1].substring(authResults[1].indexOf('=') + 1);
            String oauthCallbackConfirmed = authResults[2].substring(authResults[2].indexOf('=') + 1);

            Map<String, String> authResultMap = new HashMap<String, String>();
            authResultMap.put("oauthToken", oauthToken);
            authResultMap.put("oauthTokenSecret", oauthTokenSecret);
            authResultMap.put("oauthCallbackConfirmed", oauthCallbackConfirmed);

            this.oauthToken = oauthToken;
            this.oauthTokenSecret = oauthTokenSecret;

            return authResultMap;
        } else {
            return null;
        }
    }

    public String getOauthVerifierToRedirect(String oauthToken) {
        String requestUrl = "https://api.twitter.com/oauth/authenticate?oauth_token=" + oauthToken;
        String result = HttpUtil.get(requestUrl);
        return result;
    }

    public Map<String, String> getAccessTokenInTwitter(String oauthVerifier) {
        logger.info("WLL's log: oauthVerifier: " + oauthVerifier);

        String httpMethod = "POST";
        String baseUrl = "https://api.twitter.com/oauth/access_token";
        Map<String, String> bodyParams = new HashMap<>();
        bodyParams.put("oauth_verifier", oauthVerifier);
        String authString = getAuthString(httpMethod, baseUrl, bodyParams, null);

        Map<String, String> header = new HashMap<String, String>();
        header.put("Authorization", authString);
        Map<String, String> body = new HashMap<String, String>();
        body.put("oauth_verifier", oauthVerifier);
        String httpResult = HttpUtil.post(baseUrl, body, header, "UTF-8");
        logger.info("WLL's log: accessToken: " + httpResult);

        String[] accessTokenResults = httpResult.split("&");
        if (accessTokenResults[0].substring(0, accessTokenResults[0].indexOf('=')).equals("oauth_token")){
            String oauthToken = accessTokenResults[0].substring(accessTokenResults[0].indexOf('=') + 1);
            String oauthTokenSecret = accessTokenResults[1].substring(accessTokenResults[1].indexOf('=') + 1);

            Map<String, String> authResultMap = new HashMap<String, String>();
            authResultMap.put("oauthToken", oauthToken);
            authResultMap.put("oauthTokenSecret", oauthTokenSecret);

            this.oauthToken = oauthToken;
            this.oauthTokenSecret = oauthTokenSecret;

            return authResultMap;
        } else {
            return null;
        }
    }


    /**
     * The value was generated by base64 encoding 32 bytes of random data, and stripping out all non-word characters,
     * but any approach which produces a relatively random alphanumeric string should be OK here.
     * @return
     */
    public String getOauthNonce() {
        String result = RandomStringUtils.randomAlphanumeric(32);
        logger.info("WLL's log: OauthNonce: " + result);
        return result;
    }

    public String getSignature(String httpMethod, String baseUrl, Map params, String consumerSecret, String oauthTokenSecret) {
        String signature = null;
        try {
            String signatureParamString = getParameterString(params);
            String baseString = getBaseString(httpMethod, baseUrl, signatureParamString);
            String signingKey = getSigningKey(consumerSecret, oauthTokenSecret);
            signature = calculateSignatue(baseString, signingKey);
        } catch (SignatureException e) {
            e.printStackTrace();
        }
        return signature;
    }

    private String getBaseString(String httpMethod, String baseUrl, String params) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(UrlEncodeUtil.encode(httpMethod) + '&');
        stringBuilder.append(UrlEncodeUtil.encode(baseUrl) + '&');
        stringBuilder.append(UrlEncodeUtil.encode(params));

        String result = stringBuilder.toString();
        logger.info("WLL's log: SignatureBaseString: " + result);
        return result;
    }

    private String getSigningKey(String consumerSecret, String oAuthTokenSecret) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(UrlEncodeUtil.encode(consumerSecret) + '&');
        if (this.oauthTokenSecret != null) {
            stringBuilder.append(UrlEncodeUtil.encode(oAuthTokenSecret));
        }

        String result = stringBuilder.toString();
        logger.info("WLL's log: SigningKey: " + result);
        return result;
    }

    private String calculateSignatue(String baseString, String signingKey) throws SignatureException {
        String result = HmacSha1Util.calculateRFC2104HMAC(baseString, signingKey);
        logger.info("WLL's log: Signatue: " + result);
        return result;
    }

    public String getParameterString(Map params) {
        Map<String, String> sortedParams = new TreeMap<String, String>(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return  o1.compareTo(o2);
            }
        });

        Iterator<Map.Entry<String, String>> entries = params.entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry<String, String> entry = entries.next();
            sortedParams.put(UrlEncodeUtil.encode(entry.getKey()), UrlEncodeUtil.encode(entry.getValue()));

        }

        StringBuilder stringBuilder = new StringBuilder();
        Iterator<Map.Entry<String, String>> sortedEntries = sortedParams.entrySet().iterator();
        while (sortedEntries.hasNext()) {
            Map.Entry<String, String> sortedEntry = sortedEntries.next();
            stringBuilder.append(sortedEntry.getKey() + '=' + sortedEntry.getValue() + '&');
        }
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);

        String result = stringBuilder.toString();
        logger.info("WLL's log: ParameterString: " + result);
        return result;
    }

    public String getAuthStringByParams(Map params) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("OAuth ");

        Iterator<Map.Entry<String, String>> entryIterator = params.entrySet().iterator();
        while (entryIterator.hasNext()) {
            Map.Entry<String, String> entry = entryIterator.next();
            stringBuilder.append(UrlEncodeUtil.encode(entry.getKey()) + "=\"" + UrlEncodeUtil.encode(entry.getValue()) + "\", ");
        }
        //删掉最后的逗号和空格
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);

        String result = stringBuilder.toString();
        logger.info("WLL's log: AuthString: " + result);
        return result;
    }
}
