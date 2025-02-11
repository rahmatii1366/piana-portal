package ir.piana.dev.openidc.core.service.permission.dto;

import lombok.Data;

@Data
public class CreatePermissionRequestDto {
    private String name;
    private String description;
}
