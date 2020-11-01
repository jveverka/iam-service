package one.microproject.iamservice.serviceclient;

import one.microproject.iamservice.core.dto.IntrospectResponse;
import one.microproject.iamservice.core.dto.JWKResponse;
import one.microproject.iamservice.core.dto.ProviderConfigurationResponse;
import one.microproject.iamservice.core.services.dto.UserInfoResponse;
import one.microproject.iamservice.serviceclient.impl.ProjectInfoProvider;

import java.io.IOException;

public interface IAMServiceStatusClient extends ProjectInfoProvider {

    /**
     * https://openid.net/specs/openid-connect-discovery-1_0.html
     * @return configuration of this provides.
     * @throws IOException in case network connection fails.
     */
    ProviderConfigurationResponse getProviderConfiguration() throws IOException;

    /**
     * https://tools.ietf.org/html/rfc7517
     * @return JSON Web Key.
     * @throws IOException in case network connection fails.
     */
    JWKResponse getJWK() throws IOException;

    /**
     * https://tools.ietf.org/html/rfc7662
     * @param accessToken valid access_token.
     * @param tokenTypeHint - may be null (optional)
     * @return introspection for provided token.
     * @throws IOException in case network connection fails.
     */
    IntrospectResponse tokenIntrospection(String accessToken, String tokenTypeHint) throws IOException;

    /**
     * https://tools.ietf.org/html/rfc7662
     * @param accessToken valid access_token.
     * @return introspection for provided token.
     * @throws IOException in case network connection fails.
     */
    IntrospectResponse tokenIntrospection(String accessToken) throws IOException;

    /**
     * https://openid.net/specs/openid-connect-core-1_0.html#UserInfoRequest
     * @param accessToken valid access_token.
     * @return user info for provided token.
     * @throws IOException in case network connection fails.
     */
    UserInfoResponse getUserInfo(String accessToken) throws IOException;

    /**
     * https://tools.ietf.org/html/rfc7009
     * @param accessToken valid access_token.
     * @param tokenTypeHint - may be null (optional)
     * @throws IOException in case network connection fails.
     */
    void revokeToken(String accessToken, String tokenTypeHint) throws IOException;

    /**
     * https://tools.ietf.org/html/rfc7009
     * @param accessToken valid access_token.
     * @throws IOException in case network connection fails.
     */
    void revokeToken(String accessToken) throws IOException;

}
