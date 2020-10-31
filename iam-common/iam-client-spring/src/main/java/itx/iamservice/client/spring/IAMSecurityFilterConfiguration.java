package itx.iamservice.client.spring;

import itx.iamservice.client.IAMClient;

import java.util.Set;

public class IAMSecurityFilterConfiguration {

    private final IAMClient iamClient;
    private final Set<String> excludeEndpoints;

    public IAMSecurityFilterConfiguration(IAMClient iamClient, Set<String> excludeEndpoints) {
        this.iamClient = iamClient;
        this.excludeEndpoints = excludeEndpoints;
    }

    public IAMClient getIamClient() {
        return iamClient;
    }

    public Set<String> getExcludeEndpoints() {
        return excludeEndpoints;
    }

}
