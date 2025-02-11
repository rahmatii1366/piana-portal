package ir.piana.dev.openidc.core.service.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DomainDto {
    private Long id;
    private String name;
    private String description;
    private String persianDateTime;

    public DomainDto(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
