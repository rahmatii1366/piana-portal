package ir.piana.dev.openidc.core.service.udr.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class UsersByRolesAddToDomainRequestDto {
    @NotNull
    private Long domainId;
    @NotNull
    @NotEmpty
    private List<Long> userIds;
    /*@NotNull
    @NotEmpty*/
    private List<Long> roleIds;

    public UsersByRolesAddToDomainRequestDto() {
    }

    public Long getDomainId() {
        return domainId;
    }

    public void setDomainId(Long domainId) {
        this.domainId = domainId;
    }

    public List<Long> getUserIds() {
        return userIds;
    }

    public void setUserIds(List<Long> userIds) {
        this.userIds = userIds;
    }

    public List<Long> getRoleIds() {
        return roleIds;
    }

    public void setRoleIds(List<Long> roleIds) {
        this.roleIds = roleIds;
    }
}
