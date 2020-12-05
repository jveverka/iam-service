package one.microproject.iamservice.client;

import one.microproject.iamservice.client.impl.IAMClientImpl;
import one.microproject.iamservice.client.impl.IAMServiceHttpProxyImpl;
import one.microproject.iamservice.client.impl.IAMServiceProxy;
import one.microproject.iamservice.client.impl.TokenValidatorImpl;
import one.microproject.iamservice.core.TokenValidator;
import one.microproject.iamservice.core.model.OrganizationId;
import one.microproject.iamservice.core.model.ProjectId;

import java.net.URL;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class IAMClientBuilder {

    private IAMServiceProxy iamServiceProxy;
    private OrganizationId organizationId;
    private ProjectId projectId;
    private TokenValidator tokenValidator;

    public IAMClientBuilder setOrganizationId(String organizationId) {
        this.organizationId = OrganizationId.from(organizationId);
        return this;
    }

    public IAMClientBuilder setProjectId(String projectId) {
        this.projectId = ProjectId.from(projectId);
        return this;
    }

    public IAMClientBuilder withHttpProxy(URL baseUrl, Long pollingInterval, TimeUnit timeUnit) {
        Objects.requireNonNull(baseUrl);
        Objects.requireNonNull(organizationId);
        Objects.requireNonNull(projectId);
        this.iamServiceProxy = new IAMServiceHttpProxyImpl(baseUrl, organizationId, projectId,
                pollingInterval, timeUnit);
        return this;
    }

    public IAMClientBuilder withIAMServiceProxy(IAMServiceProxy iamServiceProxy) {
        this.iamServiceProxy = iamServiceProxy;
        return this;
    }

    public IAMClientBuilder withTokenValidator(TokenValidator tokenValidator) {
        this.tokenValidator = tokenValidator;
        return this;
    }

    public IAMClient build() {
        if (tokenValidator == null) {
            tokenValidator = new TokenValidatorImpl();
        }
        Objects.requireNonNull(organizationId);
        Objects.requireNonNull(projectId);
        Objects.requireNonNull(iamServiceProxy);
        return new IAMClientImpl(tokenValidator, iamServiceProxy, organizationId, projectId);
    }

    public static IAMClientBuilder builder() {
        return new IAMClientBuilder();
    }

}
