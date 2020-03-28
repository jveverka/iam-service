package itx.iamservice.services;

import itx.iamservice.core.model.RoleId;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Deprecated
public final class Utils {

    private Utils() {
    }

    @Deprecated
    public static Set<RoleId> getScopes(String scope) {
        Set<RoleId> scopes = new HashSet<>();
        if (scope == null) {
            scopes = Collections.emptySet();
        } else {
            scopes = new HashSet<>();
            String[] rawScopes = scope.trim().split(" ");
            for (String s: rawScopes) {
                if (!s.isEmpty()) {
                    scopes.add(RoleId.from(s));
                }
            }
        }
        return scopes;
    }

}
