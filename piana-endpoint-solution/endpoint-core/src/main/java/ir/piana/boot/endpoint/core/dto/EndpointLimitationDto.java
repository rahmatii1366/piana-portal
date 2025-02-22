package ir.piana.boot.endpoint.core.dto;

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
    @Override
    public int hashCode() {
        int prime = 17;
        int hash = prime;
        hash = prime * hash + (startAt != null ? startAt.hashCode() : 0);
        hash = prime * hash + (expireAt != null ? expireAt.hashCode() : 0);
        hash = prime * hash + (tpsLimit != null ? tpsLimit : 0);
        hash = prime * hash + (minuteLimit != null ? minuteLimit : 0);
        hash = prime * hash + (hourLimit != null ? hourLimit : 0);
        hash = prime * hash + (dayLimit != null ? dayLimit : 0);
        hash = prime * hash + (weekLimit != null ? weekLimit : 0);
        hash = prime * hash + (monthLimit != null ? monthLimit : 0);
        hash = prime * hash + (yearLimit != null ? yearLimit : 0);
        hash = prime * hash + (periodLimit != null ? periodLimit : 0);
        return hash;
    }
}
