package ir.piana.dev.openidc.core.service.role.dto;

import ir.piana.dev.openidc.core.dto.PaginationRequest;
import jakarta.validation.constraints.NotNull;

public class RolePerUserOnDomainRequestDto extends PaginationRequest {
    @NotNull
    private String name;

    public RolePerUserOnDomainRequestDto() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
