package one.microproject.iamservice.serviceclient;

import one.microproject.iamservice.core.dto.CreateUser;
import one.microproject.iamservice.core.dto.UserCredentialsChangeRequest;
import one.microproject.iamservice.core.model.RoleId;
import one.microproject.iamservice.core.model.UserId;
import one.microproject.iamservice.core.services.dto.UserInfo;
import one.microproject.iamservice.serviceclient.impl.AuthenticationException;
import one.microproject.iamservice.serviceclient.impl.ProjectInfoProvider;

import java.io.IOException;

public interface IAMServiceUserManagerClient extends ProjectInfoProvider  {

    UserInfo getUserInfo(UserId userId) throws IOException;

    void createUser(CreateUser createUser) throws AuthenticationException;

    void deleteUser(UserId userId) throws AuthenticationException;

    void addRoleToUser(UserId userId, RoleId roleId) throws AuthenticationException;

    void removeRoleFromUser(UserId userId, RoleId roleId) throws AuthenticationException;

    void changeUserCredentials(UserId userId, String userAccessToken, UserCredentialsChangeRequest request) throws AuthenticationException;

}
