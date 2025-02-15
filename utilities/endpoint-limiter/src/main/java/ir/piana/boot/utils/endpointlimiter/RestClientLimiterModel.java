package ir.piana.boot.utils.endpointlimiter;

import lombok.*;
import org.springframework.web.client.RestClient;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class RestClientLimiterModel {
    private int order;
    private RestClient restClient;
    private Long tpsLimit;
    private Long minuteLimit;
    private Long hourLimit;
    private Long dayLimit;
    private Long weekLimit;
    private Long monthLimit;
    private Long yearLimit;
    private Long periodLimit;
    private LocalDateTime startPeriod;
    private LocalDateTime endPeriod;
}
