package ir.piana.boot.endpoint.dto;

public record EndpointLimitationDto(
        String startAt,
        String expireAt,
        Integer tpsLimit,
        Integer minuteLimit,
        Integer hourLimit,
        Integer dayLimit,
        Integer weekLimit,
        Integer monthLimit,
        Integer yearLimit,
        Integer periodLimit
) {
}
