package com.xwbing.shiro;

import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.filter.authc.AnonymousFilter;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.filter.DelegatingFilterProxy;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 项目名称: boot-module-demo
 * 创建时间: 2018/3/28 14:06
 * 作者: xiangwb
 * 说明: anon,authc
 */
@Configuration
@PropertySource("classpath:shiro.properties")
public class ShiroConfig {
    @Value("${rememberMeCookieMaxAge}")
    private Integer rememberMeCookieMaxAge;
    @Value("${globalSessionTimeout}")
    private Long globalSessionTimeout;
    @Value("${sessionValidationInterval}")
    private String sessionValidationInterval;
    @Value("${sessionsCacheName}")
    private String sessionsCacheName;
    @Value("${sessionIdCookieName}")
    private String sessionIdCookieName;

    /**
     * 此处核心,对多个filter进行管理，其它filer设置不自动加载，作为过滤链条使用
     *
     * @return
     */
    @Bean
    public FilterRegistrationBean filterRegistrationBean() {
        FilterRegistrationBean filterRegistration = new FilterRegistrationBean();
        filterRegistration.setFilter(new DelegatingFilterProxy("shiroFilter"));
        filterRegistration.setEnabled(true);
        filterRegistration.addUrlPatterns("/*");
        filterRegistration.setDispatcherTypes(DispatcherType.REQUEST);
        filterRegistration.setOrder(1);
        return filterRegistration;
    }

    @Bean(name = "shiroFilter")
    public ShiroFilterFactoryBean shiroFilter() {
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        shiroFilterFactoryBean.setSecurityManager(securityManager());
        //如果不设置默认会自动寻找Web工程根目录下的"/login.jsp"页面
        shiroFilterFactoryBean.setLoginUrl("/login.html");
        shiroFilterFactoryBean.setUnauthorizedUrl("/login.html");

        Map<String, Filter> filters = new HashMap<>();
        filters.put("authc", urlPermissionsFilter());
        filters.put("anon", new AnonymousFilter());
        shiroFilterFactoryBean.setFilters(filters);

        Map<String, String> chains = new LinkedHashMap<>();
        //静态资源
        chains.put("/META-INF/resources/**", "anon");
        chains.put("/resources/**", "anon");
        chains.put("/static/**", "anon");
        chains.put("/public/**", "anon");
        chains.put("/*.*", "anon");
        //验证码
        chains.put("/captcha", "anon");
        //swagger
        chains.put("/webjars/**", "anon");
        chains.put("/v2/api-docs", "anon");
        chains.put("/swagger-resources", "anon");
        chains.put("/configuration/ui", "anon");
        chains.put("/configuration/security", "anon");
        chains.put("/doc", "anon");
        //德鲁伊数据源
        chains.put("/druid/**", "anon");
        chains.put("/druid", "anon");
        //登录校验
        chains.put("/user/login", "anon");
        chains.put("/login", "anon");
        //登出
        chains.put("/user/logout", "anon");
        //必须通过验证或者rememberMe
        chains.put("/**", "user,authc");
        shiroFilterFactoryBean.setFilterChainDefinitionMap(chains);
        return shiroFilterFactoryBean;
    }

    /////////////////securityManager///////////////////
    @Bean
    public SecurityManager securityManager() {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealm(shiroRealm());
//        securityManager.setCacheManager(cacheManager());
//        securityManager.setSessionManager(sessionManager());
//        securityManager.setRememberMeManager(rememberMeManager());
        return securityManager;
    }

    ///////////////////shiroRealm/////////////////
    @Bean
    public MyShiroRealm shiroRealm() {
        MyShiroRealm myShiroRealm = new MyShiroRealm();
//        myShiroRealm.setCacheManager(cacheManager());
//        myShiroRealm.setAuthorizationCachingEnabled(false);
//        //启用身份验证缓存,即缓存AuthenticationInfo信息，默认false
//        myShiroRealm.setAuthenticationCachingEnabled(true);
//        //缓存AuthenticationInfo信息的缓存名称
//        myShiroRealm.setAuthenticationCacheName("authenticationCache");
//        //缓存AuthorizationInfo信息的缓存名称
//        myShiroRealm.setAuthorizationCacheName("authorizationCache");
        return myShiroRealm;
    }

    ///////////////////cacheManager////////////////
//    @Bean
//    public RedisCacheManager cacheManager() {
//        RedisCacheManager redisCacheManager = new RedisCacheManager();
//        redisCacheManager.setRedisManager(redisManager());
//        return redisCacheManager;
//    }

//    @Bean
//    public RedisManager redisManager() {
//        RedisManager redisManager = new RedisManager();
//        redisManager.setHost("192.168.11.202");
//        redisManager.setPort(6379);
//        redisManager.setExpire(1800);// 配置缓存过期时间
//        return redisManager;
//    }
    //////////////////////sessionManager//////////////////

//    @Bean
//    public SessionManager sessionManager() {
//        MySessionManager sessionManager = new MySessionManager();
//        sessionManager.setSessionDAO(redisSessionDAO());
//        sessionManager.setCacheManager(cacheManager());
//        sessionManager.setGlobalSessionTimeout(globalSessionTimeout);
//        sessionManager.setDeleteInvalidSessions(true);
//        sessionManager.setSessionValidationSchedulerEnabled(true);
//        sessionManager.setDeleteInvalidSessions(true);
//        sessionManager.setSessionIdCookie(sessionIdCookie());
//        sessionManager.setSessionIdCookieEnabled(true);
//        return sessionManager;
//    }
//
//    @Bean
//    public SimpleCookie sessionIdCookie() {
//        SimpleCookie simpleCookie = new SimpleCookie(sessionIdCookieName);
//        simpleCookie.setHttpOnly(true);
//        simpleCookie.setMaxAge(-1);//<!-- maxAge=-1表示浏览器关闭时失效此Cookie； -->
//        return simpleCookie;
//    }
//
//    @Bean
//    public RedisSessionDAO redisSessionDAO() {
//        RedisSessionDAO redisSessionDAO = new RedisSessionDAO();
//        redisSessionDAO.setRedisManager(redisManager());
//        return redisSessionDAO;
//    }

    //////////////rememberMeManager//////////////////

    /**
     * cookie管理对象;记住我功能
     *
     * @return
     */
//    public CookieRememberMeManager rememberMeManager() {
//        CookieRememberMeManager cookieRememberMeManager = new CookieRememberMeManager();
//        cookieRememberMeManager.setCookie(rememberMeCookie());
//        //rememberMe cookie加密的密钥 建议每个项目都不一样 默认AES算法 密钥长度(128 256 512 位)
//        cookieRememberMeManager.setCipherKey(Base64.getDecoder().decode("3AvVhmFLUs0KTA3Kprsdag=="));
//        return cookieRememberMeManager;
//    }
//
//    /**
//     * cookie对象;
//     *
//     * @return
//     */
//    public SimpleCookie rememberMeCookie() {
//        //这个参数是cookie的名称，对应前端的checkbox的name = rememberMe
//        SimpleCookie simpleCookie = new SimpleCookie("rememberMe");
//        //<!-- 记住我cookie生效时间30天 ,单位秒;-->
//        simpleCookie.setMaxAge(2592000);
//        return simpleCookie;
//    }
    @Bean
    public FilterRegistrationBean urlPermissionsFilterRegistrationBean(UrlPermissionsFilter urlPermissionsFilter) {
        FilterRegistrationBean registration = new FilterRegistrationBean(urlPermissionsFilter);
        //设置不自动在在配置时执行,默认是true
        registration.setEnabled(false);
        return registration;
    }

    @Bean(name = "urlPermissionsFilter")
    public UrlPermissionsFilter urlPermissionsFilter() {
        return new UrlPermissionsFilter();
    }

    /**
     * AOP式方法级权限检查
     *
     * @param securityManager
     * @return
     */
    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(SecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
        authorizationAttributeSourceAdvisor.setSecurityManager(securityManager);
        return authorizationAttributeSourceAdvisor;
    }
}
