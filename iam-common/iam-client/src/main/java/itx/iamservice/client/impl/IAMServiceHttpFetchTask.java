package itx.iamservice.client.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import itx.iamservice.core.dto.JWKResponse;
import itx.iamservice.core.model.OrganizationId;
import itx.iamservice.core.model.ProjectId;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;

public class IAMServiceHttpFetchTask implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(IAMServiceHttpFetchTask.class);

    private final URL baseUrl;
    private final OrganizationId organizationId;
    private final ProjectId projectId;
    private final OkHttpClient client;
    private final ObjectMapper mapper;
    private final IAMServiceHttpProxyImpl iamServiceHttpProxy;

    public IAMServiceHttpFetchTask(URL baseUrl, OrganizationId organizationId, ProjectId projectId,
                                   OkHttpClient client, ObjectMapper mapper, IAMServiceHttpProxyImpl iamServiceHttpProxy) {
        this.baseUrl = baseUrl;
        this.organizationId = organizationId;
        this.projectId = projectId;
        this.client = client;
        this.mapper = mapper;
        this.iamServiceHttpProxy = iamServiceHttpProxy;
    }

    @Override
    public void run() {
        String targetUrl = baseUrl.toString()  +  "/services/authentication/" + organizationId.getId() + "/" + projectId.getId() + "/.well-known/jwks.json";
        LOG.info("Fetching iam-server {}", targetUrl);
        Request request = new Request.Builder()
                .url(targetUrl)
                .method("GET", null)
                .build();
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                JWKResponse jwkResponse = mapper.readValue(response.body().string(), JWKResponse.class);
                iamServiceHttpProxy.setJwkResponse(jwkResponse);
            } else {
                LOG.info("HTTP response failed {}", response.code());
            }
        } catch (Exception e) {
            LOG.error("Error: ", e);
        }
    }

}
