package ir.piana.boot.utils.endpointlimiter.operation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class BaseServicePointResponse {
    private long status;
    private String responseBody;
}
