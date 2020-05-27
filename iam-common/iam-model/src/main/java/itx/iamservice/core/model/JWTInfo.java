package itx.iamservice.core.model;

import java.util.Set;

public class JWTInfo {

    private final UserId userId;
    private final Set<RoleId> roles;

    public JWTInfo(UserId userId, Set<RoleId> roles) {
        this.userId = userId;
        this.roles = roles;
    }

    public UserId getUserId() {
        return userId;
    }

    public Set<RoleId> getRoles() {
        return roles;
    }

}
