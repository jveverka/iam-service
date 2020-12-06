package one.microproject.iamservice.core.services.impl.caches;

import one.microproject.iamservice.core.services.caches.AuthorizationCodeCache;
import one.microproject.iamservice.core.services.dto.AuthorizationCode;
import one.microproject.iamservice.core.services.dto.AuthorizationCodeContext;
import one.microproject.iamservice.core.dto.Code;
import one.microproject.iamservice.core.services.dto.Scope;

import java.util.Date;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class AuthorizationCodeCacheImpl implements AuthorizationCodeCache {

    private final Long maxDuration;
    private final TimeUnit timeUnit;
    private CacheHolder<AuthorizationCodeContext> codes;

    public AuthorizationCodeCacheImpl(Long maxDuration, TimeUnit timeUnit, CacheHolder<AuthorizationCodeContext> codes) {
        this.maxDuration = maxDuration;
        this.timeUnit = timeUnit;
        this.codes = codes;
    }

    @Override
    public AuthorizationCode save(Code code, AuthorizationCodeContext authorizationCodeContext) {
        AuthorizationCode authorizationCode = new AuthorizationCode(code, authorizationCodeContext.getState(), authorizationCodeContext.getScope());
        codes.put(code.getCodeValue(), authorizationCodeContext);
        return authorizationCode;
    }

    @Override
    public int purgeCodes() {
        int purged = 0;
        for (String key: codes.keys()) {
            Optional<AuthorizationCodeContext> verifiedCode = verifyAndRemove(Code.from(key));
            if (verifiedCode.isPresent()) {
                codes.remove(key);
                purged++;
            }
        }
        return purged;
    }

    @Override
    public Optional<AuthorizationCodeContext> setScope(Code code, Scope scope) {
        AuthorizationCodeContext context = codes.get(code.getCodeValue());
        if (context != null) {
            Set<String> filteredScopes = new HashSet<>();
            context.getScope().getValues().forEach(s->{
                if (scope.getValues().contains(s)) {
                    filteredScopes.add(s);
                }
            });
            AuthorizationCodeContext updatedContext = new AuthorizationCodeContext(code, context.getIssuerUri(),
                    context.getOrganizationId(), context.getProjectId(), context.getClientId(), context.getUserId(),
                    context.getState(), context.getIssued(), new Scope(filteredScopes), context.getAudience(), context.getRedirectURI());
            codes.put(code.getCodeValue(), updatedContext);
            return Optional.of(context);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public Optional<AuthorizationCodeContext> verifyAndRemove(Code code) {
        AuthorizationCodeContext context = codes.remove(code.getCodeValue());
        if (context != null) {
            return verify(context);
        }
        return Optional.empty();
    }

    @Override
    public Optional<AuthorizationCodeContext> get(Code code) {
        AuthorizationCodeContext context = codes.get(code.getCodeValue());
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
