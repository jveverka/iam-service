package one.microproject.iamservice.serviceclient.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import one.microproject.iamservice.core.model.ClientId;
import one.microproject.iamservice.core.model.OrganizationId;
import one.microproject.iamservice.core.model.ProjectId;
import one.microproject.iamservice.core.services.dto.AuthorizationCode;
import one.microproject.iamservice.core.services.dto.AuthorizationCodeGrantRequest;
import one.microproject.iamservice.core.dto.Code;
import one.microproject.iamservice.core.services.dto.ConsentRequest;
import one.microproject.iamservice.core.dto.TokenResponse;
import one.microproject.iamservice.serviceclient.IAMAuthorizerClient;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;
import java.net.URL;
import java.util.Set;

public class IAMAuthorizerClientImpl implements IAMAuthorizerClient {

    private final URL baseURL;
    private final OkHttpClient client;
    private final ObjectMapper mapper;
    private final OrganizationId organizationId;
    private final ProjectId projectId;

    public IAMAuthorizerClientImpl(URL baseURL, OkHttpClient client, ObjectMapper mapper, OrganizationId organizationId, ProjectId projectId) {
        this.baseURL = baseURL;
        this.client = client;
        this.mapper = mapper;
        this.organizationId = organizationId;
        this.projectId = projectId;
    }

    @Override
    public TokenResponse refreshTokens(String refreshToken, ClientId clientId, String clientSecret) throws AuthenticationException {
        try {
            Request request = new Request.Builder()
                    .url(baseURL + "/services/authentication/" + organizationId.getId() + "/" + projectId.getId() + "/token" +
                            "?grant_type=refresh_token" +
                            "&refresh_token=" + refreshToken +
                            "&scope=" +
                            "&client_id=" + clientId.getId() +
                            "&client_secret=" + clientSecret)
                    .post(RequestBody.create("{}", MediaType.parse(IAMServiceManagerClientImpl.APPLICATION_JSON)))
                    .build();
            Response response = client.newCall(request).execute();
            if (response.code() == 200) {
                return mapper.readValue(response.body().string(), TokenResponse.class);
            }
            throw new AuthenticationException("Authentication failed: " + response.code());
        } catch (IOException e) {
            throw new AuthenticationException(e);
        }
    }

    @Override
    public AuthorizationCode getAuthorizationCodeOAuth2AuthorizationCodeGrant(String userName, String password, ClientId clientId, Set<String> scopes, URL redirectUri, String state) throws AuthenticationException {
        try {
            //1. Get AuthorizationCode
            AuthorizationCodeGrantRequest authorizationCodeGrantRequest =
                    new AuthorizationCodeGrantRequest(userName, password, clientId.getId(), scopes, state, redirectUri.toString());
            Request request = new Request.Builder()
                    .url(baseURL + "/services/authentication/" + organizationId.getId() + "/" + projectId.getId() + "/authorize")
                    .post(RequestBody.create(mapper.writeValueAsString(authorizationCodeGrantRequest), MediaType.parse(IAMServiceManagerClientImpl.APPLICATION_JSON)))
                    .build();
            Response response = client.newCall(request).execute();
            if (response.code() != 200) {
                throw new AuthenticationException("Authentication failed: " + response.code());
            }
            return mapper.readValue(response.body().string(), AuthorizationCode.class);
        } catch (IOException e) {
            throw new AuthenticationException(e);
        }
    }

    @Override
    public void setOAuth2AuthorizationCodeGrantConsent(AuthorizationCode authorizationCode) throws AuthenticationException {
        try {
            //2. provide consent
            ConsentRequest consentRequest = new ConsentRequest(authorizationCode.getCode(), authorizationCode.getAvailableScopes().getValues());
            Request request = new Request.Builder()
                    .url(baseURL + "/services/authentication/" + organizationId.getId() + "/" + projectId.getId() + "/consent")
                    .post(RequestBody.create(mapper.writeValueAsString(consentRequest), MediaType.parse(IAMServiceManagerClientImpl.APPLICATION_JSON)))
                    .build();
            Response response = client.newCall(request).execute();
            if (response.code() != 200) {
                throw new AuthenticationException("Authentication failed: " + response.code());
            }
        } catch (IOException e) {
            throw new AuthenticationException(e);
        }
    }

    @Override
    public TokenResponse getAccessTokensOAuth2AuthorizationCodeGrant(Code code) throws AuthenticationException {
        try {
            //3. get access tokens
            Request request = new Request.Builder()
                    .url(baseURL + "/services/authentication/" + organizationId.getId() + "/" + projectId.getId() + "/token" +
                            "?grant_type=authorization_code" +
                            "&code=" + code.getCodeValue())
                    .post(RequestBody.create("{}", MediaType.parse(IAMServiceManagerClientImpl.APPLICATION_JSON)))
                    .build();
            Response response = client.newCall(request).execute();
            if (response.code() == 200) {
                return mapper.readValue(response.body().string(), TokenResponse.class);
            }
            throw new AuthenticationException("Authentication failed: " + response.code());
        } catch (IOException e) {
            throw new AuthenticationException(e);
        }
    }

    @Override
    public TokenResponse getAccessTokensOAuth2UsernamePassword(String userName, String password, ClientId clientId, String clientSecret) throws AuthenticationException {
        try {
            Request request = new Request.Builder()
                    .url(baseURL + "/services/authentication/" + organizationId.getId() + "/" + projectId.getId() + "/token" +
                            "?grant_type=password" +
                            "&username=" + userName +
                            "&scope=" +
                            "&password=" + password +
                            "&client_id=" + clientId.getId() +
                            "&client_secret=" + clientSecret)
                    .post(RequestBody.create("{}", MediaType.parse(IAMServiceManagerClientImpl.APPLICATION_JSON)))
                    .build();
            Response response = client.newCall(request).execute();
            if (response.code() == 200) {
                return mapper.readValue(response.body().string(), TokenResponse.class);
            }
            throw new AuthenticationException("Authentication failed: " + response.code());
        } catch (IOException e) {
            throw new AuthenticationException(e);
        }
    }

    @Override
    public TokenResponse getAccessTokensOAuth2ClientCredentials(ClientId clientId, String clientSecret) throws AuthenticationException {
        try {
            Request request = new Request.Builder()
                    .url(baseURL + "/services/authentication/" + organizationId.getId() + "/" + projectId.getId() + "/token" +
                            "?grant_type=client_credentials" +
                            "&scope=" +
                            "&client_id=" + clientId.getId() +
                            "&client_secret=" + clientSecret)
                    .post(RequestBody.create("{}", MediaType.parse(IAMServiceManagerClientImpl.APPLICATION_JSON)))
                    .build();
            Response response = client.newCall(request).execute();
            if (response.code() == 200) {
                return mapper.readValue(response.body().string(), TokenResponse.class);
            }
            throw new AuthenticationException("Authentication failed: " + response.code());
        } catch (IOException e) {
            throw new AuthenticationException(e);
        }

    }

    @Override
    public OrganizationId getOrganizationId() {
        return organizationId;
    }

    @Override
    public ProjectId getProjectId() {
        return projectId;
    }

}
