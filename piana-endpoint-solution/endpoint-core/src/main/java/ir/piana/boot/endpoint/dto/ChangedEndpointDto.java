package ir.piana.boot.endpoint.dto;

public record ChangedEndpointDto(
        String name,
        boolean deleted,
        boolean added,
        boolean changedNetwork,
        boolean changedLimitation
) {
}
