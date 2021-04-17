package one.microproject.iamservice.core.services.admin;

import one.microproject.iamservice.core.model.Organization;
import one.microproject.iamservice.core.model.OrganizationId;
import one.microproject.iamservice.core.model.PKIException;
import one.microproject.iamservice.core.model.User;
import one.microproject.iamservice.core.services.dto.CreateOrganizationRequest;
import one.microproject.iamservice.core.services.dto.OrganizationInfo;

import java.security.cert.CertificateEncodingException;
import java.util.Collection;
import java.util.Optional;

public interface OrganizationManagerService {

    Optional<OrganizationId> create(CreateOrganizationRequest createOrganizationRequest) throws PKIException;

    Collection<Organization> getAll();

    Collection<OrganizationInfo> getAllInfo() throws CertificateEncodingException;

    Optional<Organization> get(OrganizationId id);

    Optional<OrganizationInfo> getInfo(OrganizationId id) throws CertificateEncodingException;

    boolean remove(OrganizationId id);

    boolean removeWithDependencies(OrganizationId organizationId);

    void setProperty(OrganizationId id, String key, String value);

    void removeProperty(OrganizationId id, String key);

    Collection<User> getAllUsers(OrganizationId id);

}
