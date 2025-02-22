package ir.piana.boot.endpoint.servicepoint;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public final class ServicePointRequest<T> {
    private long merchantId;
    private Long requesterId;
    private String referenceId;
    private T requestDto;
}
