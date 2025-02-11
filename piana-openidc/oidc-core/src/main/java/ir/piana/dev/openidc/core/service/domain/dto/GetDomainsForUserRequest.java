package ir.piana.dev.openidc.core.service.domain.dto;

import ir.piana.dev.openidc.core.dto.PaginationRequest;
import jakarta.validation.constraints.NotNull;

public class GetDomainsForUserRequest extends PaginationRequest {
    @NotNull
    private long userId;

    public GetDomainsForUserRequest() {
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }
}
