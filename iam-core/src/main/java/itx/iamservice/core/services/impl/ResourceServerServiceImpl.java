package itx.iamservice.core.services.impl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.impl.DefaultClaims;
import itx.iamservice.core.model.Client;
import itx.iamservice.core.model.ClientId;
import itx.iamservice.core.model.Model;
import itx.iamservice.core.model.TokenCache;
import itx.iamservice.core.model.TokenUtils;
import itx.iamservice.core.services.ResourceServerService;
import itx.iamservice.core.services.dto.JWToken;

import java.util.Optional;

public class ResourceServerServiceImpl implements ResourceServerService {

    private final Model model;
    private final TokenCache tokenCache;

    public ResourceServerServiceImpl(Model model, TokenCache tokenCache) {
        this.model = model;
        this.tokenCache = tokenCache;
    }

    @Override
    public boolean verify(JWToken token) {
        boolean isRevoked = tokenCache.isRevoked(token);
        if (!isRevoked) {
            DefaultClaims defaultClaims = TokenUtils.extractClaims(token);
            ClientId clientId = ClientId.from(defaultClaims.getSubject());
            Optional<Client> client = model.getClient(clientId);
            if (client.isPresent()) {
                Optional<Jws<Claims>> claimsJws = TokenUtils.verify(token, client.get().getKeyPair());
                return claimsJws.isPresent();
            }
        }
        return false;
    }

}
