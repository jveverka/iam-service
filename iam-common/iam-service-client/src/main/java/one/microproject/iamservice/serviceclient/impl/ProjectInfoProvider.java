package one.microproject.iamservice.serviceclient.impl;

import one.microproject.iamservice.core.model.OrganizationId;
import one.microproject.iamservice.core.model.ProjectId;

public interface ProjectInfoProvider {

    OrganizationId getOrganizationId();

    ProjectId getProjectId();

}
