package ir.piana.dev.openidc.core.service.role.dto;

import ir.piana.dev.openidc.core.dto.PaginationRequest;
import jakarta.validation.constraints.NotNull;

public class RolesForDomainRequestDto extends PaginationRequest {
    @NotNull
    private long domainId;
    private String name;
    private Boolean memberOnly;

    public RolesForDomainRequestDto() {
    }

    public long getDomainId() {
        return domainId;
    }

    public void setDomainId(long domainId) {
        this.domainId = domainId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getMemberOnly() {
        return memberOnly;
    }

    public void setMemberOnly(Boolean memberOnly) {
        this.memberOnly = memberOnly;
    }
}
