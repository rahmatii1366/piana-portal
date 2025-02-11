package ir.piana.dev.openidc.core.service.drp.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class RoleByPermissionsAddToDomainRequestDto {
    @NotNull
    private Long domainId;
    @NotNull
    private Long roleId;
    @NotNull
    @NotEmpty
    private List<Long> PermissionIds;

    public RoleByPermissionsAddToDomainRequestDto() {
    }

    public Long getDomainId() {
        return domainId;
    }

    public void setDomainId(Long domainId) {
        this.domainId = domainId;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public List<Long> getPermissionIds() {
        return PermissionIds;
    }

    public void setPermissionIds(List<Long> permissionIds) {
        PermissionIds = permissionIds;
    }
}
