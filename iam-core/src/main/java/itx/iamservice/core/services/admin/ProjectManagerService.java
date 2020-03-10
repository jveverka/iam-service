package itx.iamservice.core.services.admin;

import itx.iamservice.core.model.OrganizationId;
import itx.iamservice.core.model.PKIException;
import itx.iamservice.core.model.Project;
import itx.iamservice.core.model.ProjectId;

import java.util.Collection;
import java.util.Optional;

public interface ProjectManagerService {

    boolean create(OrganizationId id, ProjectId projectId, String name) throws PKIException;

    Collection<Project> getAll(OrganizationId id);

    Optional<Project> get(OrganizationId id, ProjectId projectId);

    boolean remove(OrganizationId id, ProjectId projectId);

}
