package ir.piana.boot.utils.endpointlimiter.operation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class BaseServicePointRequest {
    private long merchantId;
    private long requesterId;
    private String referenceId;
}
