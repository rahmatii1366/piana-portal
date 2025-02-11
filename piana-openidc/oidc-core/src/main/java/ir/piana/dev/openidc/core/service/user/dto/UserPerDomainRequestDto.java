package ir.piana.dev.openidc.core.service.user.dto;

import ir.piana.dev.openidc.core.dto.PaginationRequest;
import jakarta.validation.constraints.NotNull;

public class UserPerDomainRequestDto extends PaginationRequest {
    @NotNull
    private Long domainId;
    private String firstName;
    private String lastName;
    private String username;
    private boolean noMemberOnly;

    public UserPerDomainRequestDto() {
    }

    public Long getDomainId() {
        return domainId;
    }

    public void setDomainId(Long domainId) {
        this.domainId = domainId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isNoMemberOnly() {
        return noMemberOnly;
    }

    public void setNoMemberOnly(boolean noMemberOnly) {
        this.noMemberOnly = noMemberOnly;
    }
}
