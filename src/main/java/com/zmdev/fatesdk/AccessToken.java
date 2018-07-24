package com.zmdev.fatesdk;


import com.zmdev.fatesdk.pb.AccessTokenOrBuilder;
import com.zmdev.fatesdk.pb.AccessTokenServiceGrpc;
import com.zmdev.fatesdk.pb.Credential;
import io.grpc.Deadline;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import java.util.concurrent.TimeUnit;

public class AccessToken {

    private final ManagedChannel channel;
    private final AccessTokenServiceGrpc.AccessTokenServiceBlockingStub blockingStub;
    // private static final Logger logger = Logger.getLogger(AccessToken.class.getName());

    private int appId;
    private String appSecret;
    private long timeout;

    private CacheManager singletonManager;
    private final static String ACCESS_TOKEN_CACHE_NAME = "access_token";
    private final static String ACCESS_TOKEN_CACHE_KEY = "access_token";

    public AccessToken(String host, int port, int appId, String appSecret, CacheManager singletonManager) {
        this(host, port, appId, appSecret, 5000, singletonManager);
    }

    public AccessToken(String host, int port, int appId, String appSecret, long timeout, CacheManager singletonManager) {

        this(ManagedChannelBuilder.forAddress(host, port)
                // Channels are secure by default (via SSL/TLS). For the example we disable TLS to avoid
                // needing certificates.
                .usePlaintext()
                .build(), appId, appSecret, timeout, singletonManager);
    }

    AccessToken(ManagedChannel channel, int appId, String appSecret, long timeout, CacheManager singletonManager) {
        this.appId = appId;
        this.appSecret = appSecret;
        this.timeout = timeout;
        this.channel = channel;
        blockingStub = AccessTokenServiceGrpc.newBlockingStub(channel);
        this.singletonManager = singletonManager;
        //建立一个缓存实例
        Cache cache = new Cache(ACCESS_TOKEN_CACHE_NAME, 100, true, false, 5, 2);
        //在内存管理器中添加缓存实例
        singletonManager.addCache(cache);
    }

    public void shutdown() throws InterruptedException {
        channel.shutdown().awaitTermination(this.timeout, TimeUnit.MILLISECONDS);
    }


    private AccessTokenOrBuilder requestToken() {
        Credential c = Credential.newBuilder().setAppId(appId).setAppSecret(appSecret).build();
        return blockingStub.withDeadline(Deadline.after(timeout, TimeUnit.MILLISECONDS)).token(c);
    }

    public String getToken() {
        Cache c = singletonManager.getCache(ACCESS_TOKEN_CACHE_NAME);

        if (c == null) {
            // ehcache 不存在
            throw new RuntimeException("ehcache 不存在");
        }
        Element accessTokenElement = c.get(ACCESS_TOKEN_CACHE_KEY);
        String accessToken;
        if (accessTokenElement == null) {
            // accessToken cache 不存在
            AccessTokenOrBuilder atBuilder = requestToken();
            Element e = new Element(ACCESS_TOKEN_CACHE_KEY, atBuilder.getToken());
            e.setTimeToLive(((Long) (atBuilder.getExpiredAt() - (System.currentTimeMillis() / 1000))).intValue());
            c.put(e);
            accessToken = atBuilder.getToken();
        } else {
            accessToken = (String) accessTokenElement.getObjectValue();
        }
        return accessToken;
    }
}
