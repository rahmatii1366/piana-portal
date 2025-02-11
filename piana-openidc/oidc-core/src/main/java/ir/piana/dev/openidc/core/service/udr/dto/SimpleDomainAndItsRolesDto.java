package ir.piana.dev.openidc.core.service.udr.dto;

import java.util.List;

public class SimpleDomainAndItsRolesDto {
    private long domainId;
    private String domainName;
    private List<SimpleRoleDto> roles;

    public SimpleDomainAndItsRolesDto(long domainId, String domainName, List<SimpleRoleDto> roles) {
        this.domainId = domainId;
        this.domainName = domainName;
        this.roles = roles;
    }

    public long getDomainId() {
        return domainId;
    }

    public void setDomainId(long domainId) {
        this.domainId = domainId;
    }

    public String getDomainName() {
        return domainName;
    }

    public void setDomainName(String domainName) {
        this.domainName = domainName;
    }

    public List<SimpleRoleDto> getRoles() {
        return roles;
    }

    public void setRoles(List<SimpleRoleDto> roles) {
        this.roles = roles;
    }
}
