package ir.piana.boot.utils.endpointlimiter.operation;

import io.github.bucket4j.Bucket;
import ir.piana.boot.endpoint.dto.EndpointDto;
import ir.piana.boot.endpoint.dto.EndpointLimitationDto;
import ir.piana.boot.endpoint.dto.ServicePointCollectionDto;
import ir.piana.boot.endpoint.dto.ServicePointDto;
import org.springframework.context.ApplicationContext;
import org.springframework.web.client.RestClient;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public abstract class RestClientOperationHandleable<T, R> implements Function<T, R> {
    private final ApplicationContext applicationContext;
    private final List<Bucket> bucketSequentialList = new ArrayList<>();
    private int executionOrder = 0;

    public RestClientOperationHandleable(
            ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    protected RestClient getRestClient() {
        return applicationContext.getBean(
                servicePointName() + "_" + endpointName(), RestClient.class);
    }

    //ToDo It must be checked to make sure it works properly.
    final void refreshLimitation(ServicePointCollectionDto servicePointCollectionDto) {
        ServicePointDto servicePointDto = servicePointCollectionDto.servicePoints().stream().filter(
                        dto -> dto.name().equalsIgnoreCase(servicePointName()))
                .findFirst().orElseThrow(() -> new RuntimeException());
        EndpointDto endpointDto = servicePointDto.endpoints().stream().filter(
                dto -> dto.name().equalsIgnoreCase(endpointName())
        ).findFirst().orElseThrow(() -> new RuntimeException());

        EndpointLimitationDto limitationDto = endpointDto.limitationDto();

        this.executionOrder = endpointDto.executionOrder();

        this.bucketSequentialList.clear();
        /*this.limitationDto = limitationDto;*/
        if (limitationDto.tpsLimit() != null && limitationDto.tpsLimit() > 0) {
            bucketSequentialList.add(
                    Bucket.builder().addLimit(limit -> limit
                                    .capacity(limitationDto.tpsLimit())
                                    .refillGreedy(limitationDto.tpsLimit(), Duration.ofMillis(1000)))
                            .build());
        }
        if (limitationDto.minuteLimit() != null && limitationDto.minuteLimit() > 0) {
            bucketSequentialList.add(
                    Bucket.builder().addLimit(limit -> limit
                                    .capacity(limitationDto.minuteLimit())
                                    .refillGreedy(limitationDto.minuteLimit(), Duration.ofMinutes(1)))
                            .build());
        }
        //  ToDo at hour should be persisted in cache or db  later
        if (limitationDto.hourLimit() != null && limitationDto.hourLimit() > 0) {
            bucketSequentialList.add(
                    Bucket.builder().addLimit(limit -> limit
                                    .capacity(limitationDto.hourLimit())
                                    .refillGreedy(limitationDto.hourLimit(), Duration.ofHours(1)))
                            .build());
        }
        //  ToDo at day should be persisted in cache or db  later
        if (limitationDto.dayLimit() != null && limitationDto.dayLimit() > 0) {
            bucketSequentialList.add(
                    Bucket.builder().addLimit(limit -> limit
                                    .capacity(limitationDto.dayLimit())
                                    .refillGreedy(limitationDto.dayLimit(), Duration.ofDays(1)))
                            .build());
        }
        //  ToDo at month should be persisted in cache or db  later
        if (limitationDto.monthLimit() != null && limitationDto.monthLimit() > 0) {
            bucketSequentialList.add(
                    Bucket.builder().addLimit(limit -> limit
                                    .capacity(limitationDto.monthLimit())
                                    .refillGreedy(limitationDto.monthLimit(), Duration.ofMinutes(1)))
                            .build());
        }
        //  ToDo at year should be persisted in cache or db later
        if (limitationDto.yearLimit() != null && limitationDto.yearLimit() > 0) {
            bucketSequentialList.add(
                    Bucket.builder().addLimit(limit -> limit
                                    .capacity(limitationDto.yearLimit())
                                    .refillGreedy(limitationDto.yearLimit(), Duration.ofMinutes(12)))
                            .build());
        }

        // ToDo at period should be checked later
    }

    @Override
    public final R apply(T requestDto) {
        if (!checkLimitation())
            throw new LimitationException();
        return doRequest(requestDto);
    }

    private boolean checkLimitation() {
        //ToDo should be reviewed for complex scenarios
        //      for example, pass the tps limitation but does not pass the minute limitation
        long count = this.bucketSequentialList.stream()
                .map(bucket -> bucket.tryConsume(1))
                .takeWhile(isLimit -> isLimit)
                .filter(isLimit -> isLimit)
                .count();
        return count == this.bucketSequentialList.size();
    }

    protected abstract String servicePointName();

    protected abstract String endpointName();

    protected final int executionOrder() {
        return 1;
    }

    protected abstract R doRequest(T requestDto);
}
