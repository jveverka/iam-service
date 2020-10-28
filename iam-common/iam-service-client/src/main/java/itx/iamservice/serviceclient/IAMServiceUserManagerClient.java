package itx.iamservice.serviceclient;

import itx.iamservice.core.dto.CreateUser;
import itx.iamservice.core.model.RoleId;
import itx.iamservice.core.model.UserId;
import itx.iamservice.core.services.dto.UserInfo;
import itx.iamservice.serviceclient.impl.AuthenticationException;
import itx.iamservice.serviceclient.impl.ProjectInfoProvider;

import java.io.IOException;

public interface IAMServiceUserManagerClient extends ProjectInfoProvider  {

    UserInfo getUserInfo(UserId userId) throws IOException;

    void createUser(CreateUser createUser) throws AuthenticationException;

    void deleteUser(UserId userId) throws AuthenticationException;

    void addRoleToUser(UserId userId, RoleId roleId) throws AuthenticationException;

    void removeRoleFromUser(UserId userId, RoleId roleId) throws AuthenticationException;

}
