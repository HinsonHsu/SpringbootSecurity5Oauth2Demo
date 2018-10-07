package cn.hinson.service.imp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.security.Security;
import java.util.ArrayList;
import java.util.List;

/**
 * 用户登录，注册等
 *
 * @author H1nson
 * @version 2018/10/7 22:36:24
 */
public class UserServiceImp {
    @Autowired
    SecurityContext securityContext;
    public void login(String userId) {

        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("USER"));
        UserDetails userDetails = new User(userId, userId, authorities);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails.getUsername(), userDetails.getPassword(),
                userDetails.getAuthorities());
        securityContext.setAuthentication(authentication);
    }
}
