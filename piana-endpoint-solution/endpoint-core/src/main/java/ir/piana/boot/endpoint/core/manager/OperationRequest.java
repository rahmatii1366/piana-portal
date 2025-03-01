package ir.piana.boot.endpoint.core.manager;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public final class OperationRequest<T> {
    private long merchantId;
    private Long requesterId;
    private String referenceId;
    private T requestDto;
}
