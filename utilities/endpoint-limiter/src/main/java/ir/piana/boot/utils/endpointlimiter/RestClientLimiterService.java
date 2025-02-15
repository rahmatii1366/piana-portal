package ir.piana.boot.utils.endpointlimiter;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class RestClientLimiterService {
    private final RestClientLimiterCollectionModel limiterCollectionModel;

    @PostConstruct
    public void onPostConstructor() {
        if (limiterCollectionModel == null) {
            throw new RuntimeException("one limiter is null");
        }
        if (limiterCollectionModel.restClientLimiterModels() == null ||
                limiterCollectionModel.restClientLimiterModels().isEmpty()) {
            log.error("limiterModels for {} is null.", limiterCollectionModel.name());
            return;
        }

        boolean noDuplicationOrder = limiterCollectionModel.restClientLimiterModels().size() == limiterCollectionModel.restClientLimiterModels()
                .stream().map(RestClientLimiterModel::getOrder).distinct().count();
        if (!noDuplicationOrder) {
            log.error("limiterModels for {} has duplicated order", limiterCollectionModel.name());
            return;
        }


    }
}
