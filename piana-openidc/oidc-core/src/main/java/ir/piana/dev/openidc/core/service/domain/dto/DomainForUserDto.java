package ir.piana.dev.openidc.core.service.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DomainForUserDto {
    private Long id;
    private String name;
    private String description;
    private boolean assigned;
}
