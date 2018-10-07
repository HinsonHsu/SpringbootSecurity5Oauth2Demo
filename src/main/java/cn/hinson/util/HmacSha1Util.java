package cn.hinson.util;

import sun.misc.BASE64Encoder;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.SignatureException;

public class HmacSha1Util {
    private static final String HMAC_SHA1_ALGORITHM = "HmacSHA1";
    private static final String ENCODING = "UTF-8";

    public static String calculateRFC2104HMAC(String data, String key) throws SignatureException {
        String result;
        try {

            // get an hmac_sha1 key from the raw key bytes
            SecretKeySpec signingKey = new SecretKeySpec(key.getBytes(), HMAC_SHA1_ALGORITHM);

            // get an hmac_sha1 Mac instance and initialize with the signing key
            Mac mac = Mac.getInstance(HMAC_SHA1_ALGORITHM);
            mac.init(signingKey);

            // compute the hmac on input data bytes
            byte[] rawHmac = mac.doFinal(data.getBytes());

            // base64-encode the hmac
            BASE64Encoder base64Encoder = new BASE64Encoder();
            result = base64Encoder.encode(rawHmac);

        } catch (Exception e) {
            throw new SignatureException("Failed to generate HMAC : "
                    + e.getMessage());
        }
        return result;
    }

    public static byte[] hmacSHA1(String data, String key) throws SignatureException {
        try {
            // get an hmac_sha1 key from the raw key bytes
            SecretKeySpec signingKey = new SecretKeySpec(key.getBytes(), HMAC_SHA1_ALGORITHM);

            // get an hmac_sha1 Mac instance and initialize with the signing key
            Mac mac = Mac.getInstance(HMAC_SHA1_ALGORITHM);
            mac.init(signingKey);

            // compute the hmac on input data bytes
            byte[] rawHmac = mac.doFinal(data.getBytes());

            return rawHmac;

        } catch (Exception e) {
            throw new SignatureException("Failed to generate HMAC : "
                    + e.getMessage());
        }
    }
}
