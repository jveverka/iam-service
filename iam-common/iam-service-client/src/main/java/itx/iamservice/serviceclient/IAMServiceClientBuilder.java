package itx.iamservice.serviceclient;

import itx.iamservice.serviceclient.impl.IAMServiceClientImpl;

import java.util.concurrent.TimeUnit;

public class IAMServiceClientBuilder {

    private String baseURL;
    private Long timeoutDuration = 5L;
    private TimeUnit timeUnit = TimeUnit.SECONDS;

    public IAMServiceClientBuilder withBaseUrl(String baseURL) {
        this.baseURL = baseURL;
        return this;
    }

    public IAMServiceClientBuilder withConnectionTimeout(Long timeoutDuration, TimeUnit timeUnit) {
        this.timeoutDuration = timeoutDuration;
        this.timeUnit = timeUnit;
        return this;
    }

    public static IAMServiceClientBuilder builder() {
        return new IAMServiceClientBuilder();
    }

    public IAMServiceClient build() {
        return new IAMServiceClientImpl(baseURL, timeoutDuration, timeUnit);
    }

}