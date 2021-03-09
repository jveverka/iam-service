package one.microproject.iamservice.examples.performance.tests.impl;

import java.net.URL;

public class GlobalAdminContext {

    private final String password;
    private final String clientSecret;

    public GlobalAdminContext(String password, String clientSecret) {
        this.password = password;
        this.clientSecret = clientSecret;
    }

    public String getPassword() {
        return password;
    }

    public String getClientSecret() {
        return clientSecret;
    }

}
