package itx.iamservice.client.spring;

import itx.iamservice.client.IAMClient;

import java.util.Set;

public class IAMSecurityFilterConfiguration {

    private final IAMClient iamClient;
    private final Set<String> excludeEndpoints;
    private final TokenClaimsMapper tokenClaimsMapper;

    public IAMSecurityFilterConfiguration(IAMClient iamClient, Set<String> excludeEndpoints) {
        this.iamClient = iamClient;
        this.excludeEndpoints = excludeEndpoints;
        this.tokenClaimsMapper = new TokenClaimsMapper(){};
    }

    public IAMSecurityFilterConfiguration(TokenClaimsMapper tokenClaimsMapper, IAMClient iamClient, Set<String> excludeEndpoints) {
        this.iamClient = iamClient;
        this.excludeEndpoints = excludeEndpoints;
        this.tokenClaimsMapper = tokenClaimsMapper;
    }

    public IAMClient getIamClient() {
        return iamClient;
    }

    public Set<String> getExcludeEndpoints() {
        return excludeEndpoints;
    }

    public TokenClaimsMapper getTokenClaimsMapper() {
        return tokenClaimsMapper;
    }
}
