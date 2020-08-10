package itx.iamservice.core.services.impl.caches;

import itx.iamservice.core.model.ClientId;
import itx.iamservice.core.model.OrganizationId;
import itx.iamservice.core.model.ProjectId;
import itx.iamservice.core.model.RoleId;
import itx.iamservice.core.model.UserId;
import itx.iamservice.core.services.caches.AuthorizationCodeCache;
import itx.iamservice.core.services.dto.AuthorizationCode;
import itx.iamservice.core.services.dto.AuthorizationCodeContext;
import itx.iamservice.core.services.dto.Code;
import itx.iamservice.core.services.dto.Scope;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class AuthorizationCodeCacheImpl implements AuthorizationCodeCache {

    private final Long maxDuration;
    private final TimeUnit timeUnit;
    private Map<Code, AuthorizationCodeContext> codes;

    public AuthorizationCodeCacheImpl(Long maxDuration, TimeUnit timeUnit) {
        this.maxDuration = maxDuration;
        this.timeUnit = timeUnit;
        this.codes = new ConcurrentHashMap<>();
    }

    @Override
    public AuthorizationCode issue(OrganizationId organizationId, ProjectId projectId, ClientId clientId, UserId userId, String state, Scope scope, Set<String> audience) {
        Code code = Code.from(UUID.randomUUID().toString());
        AuthorizationCode authorizationCode = new AuthorizationCode(code, state, scope);
        codes.put(code, new AuthorizationCodeContext(organizationId, projectId, clientId, userId, state, new Date(), scope, audience));
        return authorizationCode;
    }

    @Override
    public int purgeCodes() {
        Map<Code, AuthorizationCodeContext> purgedCodes = new HashMap<>();
        codes.forEach((code, date) -> {
            Optional<AuthorizationCodeContext> verifiedCode = verifyAndRemove(code);
            if (verifiedCode.isPresent()) {
                purgedCodes.put(code, date);
            }
        });
        int purged = codes.size() - purgedCodes.size();
        codes = purgedCodes;
        return purged;
    }

    @Override
    public Optional<AuthorizationCodeContext> verifyAndRemove(Code code) {
        AuthorizationCodeContext context = codes.remove(code);
        if (context != null) {
            return verify(context);
        }
        return Optional.empty();
    }

    private Optional<AuthorizationCodeContext> verify(AuthorizationCodeContext context) {
        long expirationTime = context.getIssued().getTime() + timeUnit.toMillis(maxDuration);
        long nowTime = new Date().getTime();
        if (nowTime <= expirationTime) {
            return Optional.of(context);
        }
        return Optional.empty();
    }

}
