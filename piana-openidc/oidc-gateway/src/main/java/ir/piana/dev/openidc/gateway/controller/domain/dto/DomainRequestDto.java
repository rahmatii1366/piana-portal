package ir.piana.dev.openidc.gateway.controller.domain.dto;

import ir.piana.dev.openidc.core.dto.PaginationRequest;
import jakarta.validation.constraints.NotNull;

public class DomainRequestDto extends PaginationRequest {
    @NotNull
    private long domainId;

    public DomainRequestDto() {
    }

    public long getDomainId() {
        return domainId;
    }

    public void setDomainId(long domainId) {
        this.domainId = domainId;
    }
}
