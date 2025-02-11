package ir.piana.dev.openidc.gateway.controller.users.dto;

import ir.piana.dev.openidc.core.dto.PaginationRequest;

public class UserPermissionsRequestDto extends PaginationRequest {
    private Long userId;
    private Long domainId;
    private Long roleId;

    public UserPermissionsRequestDto() {
        super();
    }

    public UserPermissionsRequestDto(int size, int offset, Long userId) {
        this(size, offset, userId, null, null);
    }

    public UserPermissionsRequestDto(int size, int offset, Long userId, Long domainId) {
        this(size, offset, userId, domainId, null);
    }

    public UserPermissionsRequestDto(int size, int offset, Long userId, Long domainId, Long roleId) {
        super(size, offset);
        this.userId = userId;
        this.domainId = domainId;
        this.roleId = roleId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
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
}
