package one.microproject.iamservice.server.config;

import one.microproject.iamservice.caches.redis.RedisCacheHolderImpl;
import one.microproject.iamservice.caches.redis.RedisConfiguration;
import one.microproject.iamservice.core.model.JWToken;
import one.microproject.iamservice.core.services.dto.AuthorizationCodeContext;
import one.microproject.iamservice.core.services.impl.caches.CacheHolder;
import one.microproject.iamservice.core.services.impl.caches.CacheHolderImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import javax.annotation.PostConstruct;

@Configuration
@ConfigurationProperties(prefix="iam-service.cache-type")
public class CacheConfig {

    private static final Logger LOG = LoggerFactory.getLogger(CacheConfig.class);

    private String type;
    private String host;
    private Integer port;

    @PostConstruct
    public void init() {
        LOG.info("#CONFIG iam-service.cache-type:type: {}", type);
    }

    @Bean
    @Scope("singleton")
    public CacheHolder<JWToken> createJWTCache() {
        if ("redis".equals(type)) {
            LOG.info("  Create REDIS backed JWToken cache {}:{}", host, port);
            return new RedisCacheHolderImpl<>(new RedisConfiguration(host, port), JWToken.class);
        } else {
            LOG.info("  Create in-memory JWToken cache ...");
            return new CacheHolderImpl<>();
        }
    }

    @Bean
    @Scope("singleton")
    public CacheHolder<AuthorizationCodeContext> createAuthorizationCodeContextCache() {
        if ("redis".equals(type)) {
            LOG.info("  Create REDIS backed AuthorizationCodeContext cache {}:{}", host, port);
            return new RedisCacheHolderImpl<>(new RedisConfiguration(host, port), AuthorizationCodeContext.class);
        } else {
            LOG.info("  Create in-memory AuthorizationCodeContext cache ...");
            return new CacheHolderImpl<>();
        }
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

}
