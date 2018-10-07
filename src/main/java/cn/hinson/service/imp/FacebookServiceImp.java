package cn.hinson.service.imp;

import cn.hinson.util.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Facebook相关请求服务器方法
 *
 * @author H1nson
 * @version 2018/10/7 20:11:44
 */
@Service
public class FacebookServiceImp {
    private final String clienId = "549957782114948";
    private final String clientSecret = "081a70850ac47341c3fd7bc466d5b827";
    private final String accessTokenUri = "https://graph.facebook.com/oauth/access_token";
//    private final String userAuthorizationUri = "https://www.facebook.com/dialog/oauth";
    private final String userInfoUri = "https://graph.facebook.com/me";
    private final String redirectUri = "https://localhost:8080/code";

    protected final Log logger = LogFactory.getLog(this.getClass());

    /**
     * 根据facebook返回的code，访问facebook获取access_token
     * @param code
     * @return
     */
    public String getAccessToken(String code) {
        String fullAccessTokenUrl = accessTokenUri + "?"
                + "client_id" + clienId
                + "&redirect_uri=" + redirectUri
                + "&client_secret=" + clientSecret
                + "&code=" + code;
        String res =  HttpUtil.get(fullAccessTokenUrl);
        JSONObject jsonObject = JSONObject.parseObject(res);
        return jsonObject.getString("access_token");
    }

    public Map<String, String> getUserInfo(String accessToken, Set<String> fields) {
        StringBuilder fullUserInfoUrl = new StringBuilder(userInfoUri);
        if (fields.size() > 0) {
            fullUserInfoUrl.append("?fields=");
            Iterator<String> iterator = fields.iterator();
            while (iterator.hasNext()) {
                fullUserInfoUrl.append(iterator.next() + ",");
            }
            fullUserInfoUrl.deleteCharAt(fullUserInfoUrl.length()-1);
        }
        logger.info(fullUserInfoUrl.toString());
        String response = HttpUtil.get(fullUserInfoUrl.toString());
        JSONObject jsonObject = JSONObject.parseObject(response);
        HashMap<String, String> map = new HashMap<>();
        for (Map.Entry<String, Object> entry : jsonObject.entrySet()) {
            map.put(entry.getKey(), entry.getValue().toString());
        }
        return map;
    }
}
