package itx.iamservice.client;

import itx.iamservice.client.impl.IAMClientImpl;
import itx.iamservice.client.impl.IAMServiceHttpProxyImpl;
import itx.iamservice.client.impl.IAMServiceProxy;
import itx.iamservice.core.model.OrganizationId;
import itx.iamservice.core.model.ProjectId;

import java.net.URL;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class IAMClientBuilder {

    private URL baseUrl;
    private IAMServiceProxy iamServiceProxy;
    private OrganizationId organizationId;
    private ProjectId projectId;

    public IAMClientBuilder setBaseUrl(URL baseUrl) {
        this.baseUrl = baseUrl;
        return this;
    }

    public IAMClientBuilder setOrganizationId(String organizationId) {
        this.organizationId = OrganizationId.from(organizationId);
        return this;
    }

    public IAMClientBuilder setProjectId(String projectId) {
        this.projectId = ProjectId.from(projectId);
        return this;
    }

    public IAMClientBuilder withHttpProxy(Long pollingInterval, TimeUnit timeUnit) {
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

    public IAMClient build() {
        Objects.requireNonNull(organizationId);
        Objects.requireNonNull(projectId);
        Objects.requireNonNull(iamServiceProxy);
        return new IAMClientImpl(iamServiceProxy, organizationId, projectId);
    }

    public static IAMClientBuilder builder() {
        return new IAMClientBuilder();
    }

}
