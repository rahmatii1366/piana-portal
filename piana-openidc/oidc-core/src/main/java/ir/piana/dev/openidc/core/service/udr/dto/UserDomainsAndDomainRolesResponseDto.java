package ir.piana.dev.openidc.core.service.udr.dto;

import java.util.List;

public class UserDomainsAndDomainRolesResponseDto {
    private long userId;
    private List<SimpleDomainAndItsRolesDto> domains;

    public UserDomainsAndDomainRolesResponseDto(long userId, List<SimpleDomainAndItsRolesDto> domains) {
        this.userId = userId;
        this.domains = domains;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public List<SimpleDomainAndItsRolesDto> getDomains() {
        return domains;
    }

    public void setDomains(List<SimpleDomainAndItsRolesDto> domains) {
        this.domains = domains;
    }
}
