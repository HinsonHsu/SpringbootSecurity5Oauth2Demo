package cn.hinson.controller;

import cn.hinson.service.imp.FacebookServiceImp;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Oauth2相关请求接口
 *
 * @author H1nson
 * @version 2018/10/7 17:21:39
 */
@RestController
@RequestMapping(value = "/")
public class Oauth2Controller {

    @Autowired
    FacebookServiceImp facebookServiceImp;

    protected final Log logger = LogFactory.getLog(this.getClass());

//    @RequestMapping(value = "/", method = RequestMethod.GET)
//    public String index() {
//        return "index.html";
//    }

    @RequestMapping(value = "/code/facebook", method = RequestMethod.GET)
    public String getCode(@RequestParam("code") String code) {
        return facebookServiceImp.getAccessToken(code);
    }

    @RequestMapping(value = "/user/facebook", method = RequestMethod.GET)
    public String getUserInfoByFacebook(@RequestParam("access_token") String accessToken) {
        Set<String> requireFields = new HashSet<>();
        requireFields.add("id");
        requireFields.add("name");
        Map res = facebookServiceImp.getUserInfo(accessToken, requireFields);
        logger.info(res);
        return res.toString();
    }



    @RequestMapping(value = "/test", method = RequestMethod.GET)
    public String test() {
        return "This is a test!";
    }
}
