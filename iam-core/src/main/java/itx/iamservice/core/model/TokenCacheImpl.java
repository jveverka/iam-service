package itx.iamservice.core.model;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.impl.DefaultClaims;
import itx.iamservice.core.services.dto.JWToken;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class TokenCacheImpl implements TokenCache {

    private final Model model;
    private Set<JWToken> revokedJWTokens;

    public TokenCacheImpl(Model model) {
        this.revokedJWTokens = new HashSet<>();
        this.model = model;
    }

    @Override
    public void addRevokedToken(JWToken jwToken) {
        this.revokedJWTokens.add(jwToken);
    }

    @Override
    public int purgeRevokedTokens() {
        int size = this.revokedJWTokens.size();
        this.revokedJWTokens = this.revokedJWTokens.stream()
                .filter(this::validateToken)
                .collect(Collectors.toSet());
        return size - this.revokedJWTokens.size();
    }

    @Override
    public boolean isRevoked(JWToken jwToken) {
        return this.revokedJWTokens.contains(jwToken);
    }

    @Override
    public int size() {
        return this.revokedJWTokens.size();
    }

    private boolean validateToken(JWToken jwToken) {
        DefaultClaims defaultClaims = TokenUtils.extractClaims(jwToken);
        OrganizationId organizationId = OrganizationId.from(defaultClaims.getIssuer());
        ProjectId projectId = ProjectId.from(defaultClaims.getAudience());
        ClientId clientId = ClientId.from(defaultClaims.getSubject());
        Optional<Client> client = this.model.getClient(organizationId, projectId, clientId);
        if (client.isPresent()) {
            Optional<Jws<Claims>> verify = TokenUtils.verify(jwToken, client.get().getCertificate().getPublicKey());
            return verify.isPresent();
        }
        return false;
    }

}
