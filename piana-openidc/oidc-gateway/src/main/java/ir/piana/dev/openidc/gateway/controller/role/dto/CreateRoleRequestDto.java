package ir.piana.dev.openidc.gateway.controller.role.dto;

import lombok.Data;

@Data
public class CreateRoleRequestDto {
    private String name;
    private String description;
}
