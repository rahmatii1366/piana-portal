package ir.piana.boot.utils.endpointlimiter;

import java.util.List;

public record RestClientLimiterCollectionModel (
    String name,
    List<RestClientLimiterModel> restClientLimiterModels) {
}
