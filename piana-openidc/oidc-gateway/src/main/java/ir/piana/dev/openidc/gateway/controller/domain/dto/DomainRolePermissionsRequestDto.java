package ir.piana.dev.openidc.gateway.controller.domain.dto;

import ir.piana.dev.openidc.core.dto.PaginationRequest;
import jakarta.validation.constraints.NotNull;

public class DomainRolePermissionsRequestDto extends PaginationRequest {
    private long domainId;
    private long roleId;

    public DomainRolePermissionsRequestDto() {
    }

    public long getDomainId() {
        return domainId;
    }

    public void setDomainId(long domainId) {
        this.domainId = domainId;
    }

    public long getRoleId() {
        return roleId;
    }

    public void setRoleId(long roleId) {
        this.roleId = roleId;
    }
}
