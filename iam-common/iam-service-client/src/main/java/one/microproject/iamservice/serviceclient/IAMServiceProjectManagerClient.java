package one.microproject.iamservice.serviceclient;

import one.microproject.iamservice.core.dto.CreateClient;
import one.microproject.iamservice.core.dto.CreateRole;
import one.microproject.iamservice.core.dto.PermissionInfo;
import one.microproject.iamservice.core.dto.RoleInfo;
import one.microproject.iamservice.core.model.ClientId;
import one.microproject.iamservice.core.model.PermissionId;
import one.microproject.iamservice.core.model.RoleId;
import one.microproject.iamservice.core.services.dto.ClientInfo;
import one.microproject.iamservice.core.services.dto.ProjectInfo;
import one.microproject.iamservice.serviceclient.impl.AuthenticationException;
import one.microproject.iamservice.serviceclient.impl.ProjectInfoProvider;

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

    ClientInfo getClientInfo(ClientId clientId) throws IOException;

    void createClient(CreateClient createClient) throws AuthenticationException;

    void addRoleToClient(ClientId clientId, RoleId roleId) throws AuthenticationException;

    void removeRoleFromClient(ClientId clientId, RoleId roleId) throws AuthenticationException;

    void deleteClient(ClientId clientId) throws AuthenticationException;

}
