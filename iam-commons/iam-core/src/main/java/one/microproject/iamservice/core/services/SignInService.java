package one.microproject.iamservice.core.services;

import one.microproject.iamservice.core.services.dto.UserSignInConfirmationRequest;
import one.microproject.iamservice.core.services.dto.UserSignInRequest;

public interface SignInService {

    boolean signIn(String organizationId, String projectId, UserSignInRequest userSignInRequest);

    boolean confirm(String organizationId, String projectId, UserSignInConfirmationRequest userSignInConfirmationRequest);

}
