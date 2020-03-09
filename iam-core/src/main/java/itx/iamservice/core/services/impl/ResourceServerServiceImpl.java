package itx.iamservice.core.services.impl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.impl.DefaultClaims;
import itx.iamservice.core.model.Client;
import itx.iamservice.core.model.ClientId;
import itx.iamservice.core.model.Model;
import itx.iamservice.core.model.OrganizationId;
import itx.iamservice.core.model.ProjectId;
import itx.iamservice.core.model.TokenCache;
import itx.iamservice.core.model.TokenUtils;
import itx.iamservice.core.services.ResourceServerService;
import itx.iamservice.core.services.dto.JWToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class ResourceServerServiceImpl implements ResourceServerService {

    private static final Logger LOG = LoggerFactory.getLogger(ResourceServerServiceImpl.class);

    private final Model model;
    private final TokenCache tokenCache;

    public ResourceServerServiceImpl(Model model, TokenCache tokenCache) {
        this.model = model;
        this.tokenCache = tokenCache;
    }

    @Override
    public boolean verify(OrganizationId organizationId, ProjectId projectId, JWToken token) {
        boolean isRevoked = this.tokenCache.isRevoked(token);
        if (!isRevoked) {
            DefaultClaims defaultClaims = TokenUtils.extractClaims(token);
            ClientId clientId = ClientId.from(defaultClaims.getSubject());
            Optional<Client> client = this.model.getClient(organizationId, projectId, clientId);
            if (client.isPresent()) {
                Optional<Jws<Claims>> claimsJws = TokenUtils.verify(token, client.get().getKeyPair());
                LOG.info("JWT verified={}", claimsJws.isPresent());
                return claimsJws.isPresent();
            } else {
                LOG.info("JWT subject {} not found", clientId);
            }
        } else {
            LOG.info("JWT is revoked: {}", token);
        }
        return false;
    }

}
