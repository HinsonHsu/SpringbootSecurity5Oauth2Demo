package cn.hinson.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

/**
 * Spring Security 配置
 *
 * @author H1nson
 * @version 2018/10/7 17:23:50
 */
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    /**
     * 用来验证用户的，用户登录，调用login接口时，传入username和password后，采用什么样的方式来验证用户是否有效
     * @param auth
     * @throws Exception
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        // @formatter:off
        //采用内存中的用户：用户名：user1 密码：password1来验证登录用户是否有效
        //security5 默认要求密码通过哈希算法进行加密，不允许明文的方式保存密码，用BCryptPasswordEncoder进行强哈希加密
        auth.inMemoryAuthentication()
            .passwordEncoder(new BCryptPasswordEncoder())
            .withUser("user1")
            .password(new BCryptPasswordEncoder().encode("password1"))
            .roles("USER");
        // @formatter:on
    }

    /**
     * 用来配置哪些url是无需登陆直接可以访问的，哪些url是需要登陆才能访问，哪些url需要特定ROLE才能访问
     * @param httpSecurity
     * @throws Exception
     */
    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        //authorizeRequests方法限定只对签名成功的用户请求
        //anyRequest方法限定所有请求
        //authenticated方法对所有签名成功的用户允许方法

        // @formatter:off
        //临时取消csrf，为了让postman访问
        httpSecurity.csrf().disable();
        httpSecurity
                .antMatcher("/**")  //1
                .authorizeRequests()
                .antMatchers("/**", "/login**", "/webjars/**", "/error**", "/test", "/register", "/code", "/oauth").permitAll()  //2
                .anyRequest().authenticated()  //3
                .and()
                    .exceptionHandling().authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/"))  //4
                .and()
                    .logout().logoutSuccessUrl("/").permitAll();
        // @formatter:on
    }
}
