package itx.iamservice.serviceclient;

import itx.iamservice.core.dto.IntrospectResponse;
import itx.iamservice.core.dto.JWKResponse;
import itx.iamservice.core.dto.ProviderConfigurationResponse;
import itx.iamservice.core.services.dto.UserInfoResponse;
import itx.iamservice.serviceclient.impl.ProjectInfoProvider;

import java.io.IOException;

public interface IAMServiceStatusClient extends ProjectInfoProvider {

    /**
     * https://openid.net/specs/openid-connect-discovery-1_0.html
     * @return
     * @throws IOException
     */
    ProviderConfigurationResponse getProviderConfiguration() throws IOException;

    /**
     * https://tools.ietf.org/html/rfc7517
     * @return
     * @throws IOException
     */
    JWKResponse getJWK() throws IOException;

    /**
     * https://tools.ietf.org/html/rfc7662
     * @param accessToken
     * @param tokenTypeHint - may be null (optional)
     * @return
     * @throws IOException
     */
    IntrospectResponse tokenIntrospection(String accessToken, String tokenTypeHint) throws IOException;

    /**
     * https://tools.ietf.org/html/rfc7662
     * @param accessToken
     * @return
     * @throws IOException
     */
    IntrospectResponse tokenIntrospection(String accessToken) throws IOException;

    /**
     * https://openid.net/specs/openid-connect-core-1_0.html#UserInfoRequest
     * @param accessToken
     * @return
     */
    UserInfoResponse getUserInfo(String accessToken) throws IOException;

    /**
     * https://tools.ietf.org/html/rfc7009
     * @param accessToken
     * @param tokenTypeHint - may be null (optional)
     */
    void revokeToken(String accessToken, String tokenTypeHint) throws IOException;

    /**
     * https://tools.ietf.org/html/rfc7009
     * @param accessToken
     */
    void revokeToken(String accessToken) throws IOException;

}
