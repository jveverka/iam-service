package one.microproject.iamservice.examples.performance.tests.impl;

import java.net.URL;

public class GlobalAdminContext {

    private final URL baseUrl;
    private final String password;
    private final String clientSecret;

    public GlobalAdminContext(URL baseUrl, String password, String clientSecret) {
        this.baseUrl = baseUrl;
        this.password = password;
        this.clientSecret = clientSecret;
    }

    public URL getBaseUrl() {
        return baseUrl;
    }

    public String getPassword() {
        return password;
    }

    public String getClientSecret() {
        return clientSecret;
    }

}
