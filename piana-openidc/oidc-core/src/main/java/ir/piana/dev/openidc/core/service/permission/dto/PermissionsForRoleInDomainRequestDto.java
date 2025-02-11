package ir.piana.dev.openidc.core.service.permission.dto;

import ir.piana.dev.openidc.core.dto.PaginationRequest;

public class PermissionsForRoleInDomainRequestDto extends PaginationRequest {
    private String name;

    public PermissionsForRoleInDomainRequestDto() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
