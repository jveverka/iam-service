package itx.iamservice.serviceclient.impl;

import itx.iamservice.core.model.OrganizationId;
import itx.iamservice.core.model.ProjectId;

public interface ProjectInfoProvider {

    OrganizationId getOrganizationId();

    ProjectId getProjectId();

}
