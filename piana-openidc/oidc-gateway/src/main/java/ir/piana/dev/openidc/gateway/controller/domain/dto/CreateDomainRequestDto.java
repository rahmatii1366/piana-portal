package ir.piana.dev.openidc.gateway.controller.domain.dto;

import lombok.Data;

@Data
public class CreateDomainRequestDto {
    private String name;
    private String description;
}
