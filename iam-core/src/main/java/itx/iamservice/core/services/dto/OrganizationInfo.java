package itx.iamservice.core.services.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import itx.iamservice.core.model.KeyPairData;
import itx.iamservice.core.model.OrganizationId;
import itx.iamservice.core.model.ProjectId;

import java.security.cert.CertificateEncodingException;
import java.util.Base64;
import java.util.Set;

public class OrganizationInfo {

    private final String id;
    private final String name;
    private final Set<String> projects;
    private final CertificateInfo x509Certificate;

    public OrganizationInfo(String id, String name, Set<String> projects,
                            KeyPairData keyPairData) throws CertificateEncodingException {
        this.id = id;
        this.name = name;
        this.projects = projects;
        this.x509Certificate = new CertificateInfo(keyPairData.getId().getId(),
                Base64.getEncoder().encodeToString(keyPairData.getX509Certificate().getEncoded()));
    }

    @JsonCreator
    public OrganizationInfo(@JsonProperty("id") String id,
                            @JsonProperty("name") String name,
                            @JsonProperty("projects") Set<String> projects,
                            @JsonProperty("x509Certificate") CertificateInfo x509Certificate) {
        this.id = id;
        this.name = name;
        this.projects = projects;
        this.x509Certificate = x509Certificate;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Set<String> getProjects() {
        return projects;
    }

    public CertificateInfo getX509Certificate() {
        return x509Certificate;
    }

}
