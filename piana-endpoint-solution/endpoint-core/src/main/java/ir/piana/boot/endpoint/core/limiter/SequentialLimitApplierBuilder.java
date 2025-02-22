package ir.piana.boot.endpoint.core.limiter;

import io.github.bucket4j.Bucket;
import ir.piana.boot.endpoint.core.dto.EndpointLimitationDto;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * this class create {@link #ir.piana.boot.endpoint.core.limiter.SequentialLimitApplier}
 * based on tps, minute, hour, day, week, month, year limitation
 */
public final class SequentialLimitApplierBuilder {
    public static SequentialLimitApplier build (EndpointLimitationDto limitationDto) {
        List<Bucket> bucketSequentialList = new ArrayList<>();
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

        return new SequentialLimitApplier(bucketSequentialList);
    }
}
