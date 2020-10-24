package itx.iamservice.serviceclient;

import itx.iamservice.serviceclient.impl.IAMServiceClientImpl;

public class IAMServiceClientBuilder {

    private String baseURL;

    public IAMServiceClientBuilder withBaseUrl(String baseURL) {
        this.baseURL = baseURL;
        return this;
    }

    public static IAMServiceClientBuilder builder() {
        return new IAMServiceClientBuilder();
    }

    public IAMServiceClient build() {
        return new IAMServiceClientImpl(baseURL);
    }

}
