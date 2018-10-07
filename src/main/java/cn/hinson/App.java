package cn.hinson;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 启动类
 *
 */
@SpringBootApplication
public class App 
{
    public static void main( String[] args )
    {
        /**
         * 由于facebook、twitter被墙（能进行直接访问twitter、facebook的请注释下面代码，保留最后一行即可）
         * 浏览器和springboot程序（也就是服务器，oauth安全考虑，很多请求是从服务器端访问facebook、twitter的，也需要代理）都需要代理
         * 1.浏览器代理方法自行上网查询方法（推荐ss来代理，比较方便）
         * 2.下面使用ss（shadowsocks）来进行springboot程序代理
         */
        String proxyHost = "127.0.0.1";
        String proxyPort = "1080";
        System.setProperty("http.proxyHost", proxyHost);
        System.setProperty("http.proxyPort", proxyPort);
        // 对https也开启代理
        System.setProperty("https.proxyHost", proxyHost);
        System.setProperty("https.proxyPort", proxyPort);

        SpringApplication.run(App.class, args);
    }
}
