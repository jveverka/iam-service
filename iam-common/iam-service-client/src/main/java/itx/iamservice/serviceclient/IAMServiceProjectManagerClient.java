package itx.iamservice.serviceclient;

import itx.iamservice.core.dto.CreateClient;
import itx.iamservice.core.dto.CreateRole;
import itx.iamservice.core.dto.PermissionInfo;
import itx.iamservice.core.dto.RoleInfo;
import itx.iamservice.core.model.ClientId;
import itx.iamservice.core.model.PermissionId;
import itx.iamservice.core.model.RoleId;
import itx.iamservice.core.model.UserId;
import itx.iamservice.core.services.dto.ClientInfo;
import itx.iamservice.core.services.dto.ProjectInfo;
import itx.iamservice.core.services.dto.UserInfo;
import itx.iamservice.serviceclient.impl.AuthenticationException;
import itx.iamservice.serviceclient.impl.ProjectInfoProvider;

import java.io.IOException;
import java.util.Collection;
import java.util.Set;

public interface IAMServiceProjectManagerClient extends ProjectInfoProvider {

    void createRole(CreateRole createRole) throws AuthenticationException;

    Collection<RoleInfo> getRoles() throws AuthenticationException;

    Set<PermissionInfo> getPermissions() throws AuthenticationException;

    void deletePermission(PermissionId permissionId) throws AuthenticationException;

    void deleteRole(RoleId roleId) throws AuthenticationException;

    ProjectInfo getInfo() throws IOException;

    UserInfo getUserInfo(UserId userId) throws IOException;

    ClientInfo getClientInfo(ClientId clientId) throws IOException;

    void createClient(CreateClient createClient) throws AuthenticationException;

    void addRoleToClient(ClientId clientId, RoleId roleId) throws AuthenticationException;

    void removeRoleFromClient(ClientId clientId, RoleId roleId) throws AuthenticationException;

    void deleteClient(ClientId clientId) throws AuthenticationException;

}
