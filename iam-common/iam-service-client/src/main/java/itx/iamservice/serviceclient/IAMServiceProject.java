package itx.iamservice.serviceclient;

import itx.iamservice.core.dto.CreateRole;
import itx.iamservice.core.dto.PermissionInfo;
import itx.iamservice.core.dto.RoleInfo;
import itx.iamservice.core.model.PermissionId;
import itx.iamservice.core.model.RoleId;
import itx.iamservice.serviceclient.impl.AuthenticationException;

import java.util.Collection;
import java.util.Set;

public interface IAMServiceProject {

    void createRole(CreateRole createRole) throws AuthenticationException;

    Collection<RoleInfo> getRoles() throws AuthenticationException;

    Set<PermissionInfo> getPermissions() throws AuthenticationException;

    void deletePermission(PermissionId permissionId) throws AuthenticationException;

    void deleteRole(RoleId roleId) throws AuthenticationException;

}