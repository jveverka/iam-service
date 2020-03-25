package itx.iamservice.core.services.admin;

import itx.iamservice.core.model.Organization;
import itx.iamservice.core.model.OrganizationId;
import itx.iamservice.core.model.PKIException;
import itx.iamservice.core.services.dto.OrganizationInfo;

import java.security.cert.CertificateEncodingException;
import java.util.Collection;
import java.util.Optional;

public interface OrganizationManagerService {

    boolean create(OrganizationId id, String name) throws PKIException;

    Optional<OrganizationId> create(String name) throws PKIException;

    Collection<Organization> getAll();

    Collection<OrganizationInfo> getAllInfo() throws CertificateEncodingException;

    Optional<Organization> get(OrganizationId id);

    Optional<OrganizationInfo> getInfo(OrganizationId id) throws CertificateEncodingException;

    boolean remove(OrganizationId id);

}
