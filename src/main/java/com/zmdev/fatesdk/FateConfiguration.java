
package com.zmdev.fatesdk;

import com.zmdev.fatesdk.grpc_insterceptor.AccessTokenInterceptor;
import com.zmdev.fatesdk.spring_insterceptor.AuthInterceptor;
import net.sf.ehcache.CacheManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:fate.properties")
public class FateConfiguration {
    @Value("${fate.rpc_host}")
    private String rpcHost;
    @Value("${fate.rpc_port}")
    private int rpcPort;
    @Value("${fate.app_id}")
    private int appId;
    @Value("${fate.app_secret}")
    private String appSecret;
    @Value("${fate.access_token_key}")
    private String accessTokenKey;
    @Value("${fate.url}")
    private String fateURL;
    @Value("${fate.ticket_id_cookie_key}")
    private String ticketIdCookieKey;
    @Value("${fate.user_id_cookie_key}")
    private String userIdCookieKey;
    @Value("${fate.timeout}")
    private int timeout;

    public FateConfiguration() {
    }

    public FateConfiguration(String rpcHost, int rpcPort, int appId, String appSecret, String accessTokenKey, String fateURL, String ticketIdCookieKey, String userIdCookieKey) {
        this.rpcHost = rpcHost;
        this.rpcPort = rpcPort;
        this.appId = appId;
        this.appSecret = appSecret;
        this.accessTokenKey = accessTokenKey;
        this.fateURL = fateURL;
        this.ticketIdCookieKey = ticketIdCookieKey;
        this.userIdCookieKey = userIdCookieKey;
    }

    public String getTicketIdCookieKey() {
        return this.ticketIdCookieKey;
    }

    public void setTicketIdCookieKey(String ticketIdCookieKey) {
        this.ticketIdCookieKey = ticketIdCookieKey;
    }

    public String getUserIdCookieKey() {
        return this.userIdCookieKey;
    }

    public void setUserIdCookieKey(String userIdCookieKey) {
        this.userIdCookieKey = userIdCookieKey;
    }

    public String getRpcHost() {
        return this.rpcHost;
    }

    public void setRpcHost(String rpcHost) {
        this.rpcHost = rpcHost;
    }

    public int getRpcPort() {
        return this.rpcPort;
    }

    public void setRpcPort(int rpcPort) {
        this.rpcPort = rpcPort;
    }

    public int getAppId() {
        return this.appId;
    }

    public void setAppId(int appId) {
        this.appId = appId;
    }

    public String getAppSecret() {
        return this.appSecret;
    }

    public void setAppSecret(String appSecret) {
        this.appSecret = appSecret;
    }

    public String getAccessTokenKey() {
        return this.accessTokenKey;
    }

    public void setAccessTokenKey(String accessTokenKey) {
        this.accessTokenKey = accessTokenKey;
    }

    public String getFateURL() {
        return this.fateURL;
    }

    public void setFateURL(String fateURL) {
        this.fateURL = fateURL;
    }

    @Bean
    public AccessToken getAccessToken(@Autowired CacheManager cacheManager) {
        return new AccessToken(this.rpcHost, this.rpcPort, this.appId, this.appSecret, timeout, cacheManager);
    }

    @Bean
    public AccessTokenInterceptor getAccessTokenInterceptor(@Autowired AccessToken accessToken) {
        return new AccessTokenInterceptor(this.accessTokenKey, accessToken);
    }

    @Bean
    public LoginChecker getLoginChecker(@Autowired AccessTokenInterceptor accessTokenInterceptor) {
        return new LoginChecker(this.rpcHost, this.rpcPort, timeout, accessTokenInterceptor);
    }

    @Bean
    public UserService getUserService(@Autowired AccessTokenInterceptor accessTokenInterceptor) {
        return new UserService(this.rpcHost, this.rpcPort, timeout, accessTokenInterceptor);
    }

    @Bean
    public Fate getFate() {
        return new Fate(this.fateURL, this.appId);
    }

    @Bean
    public AuthInterceptor getAuthInterceptor(@Autowired LoginChecker loginChecker, @Autowired Fate fate) {
        return new AuthInterceptor(loginChecker, fate, this.ticketIdCookieKey, this.userIdCookieKey);
    }

    @Bean
    public CacheManager getCacheManager() {
        return CacheManager.create();
    }

}
