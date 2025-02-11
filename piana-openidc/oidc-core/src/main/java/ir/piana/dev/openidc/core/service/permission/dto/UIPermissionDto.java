package ir.piana.dev.openidc.core.service.permission.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class UIPermissionDto {
    private Long id;
    private String name;

    public UIPermissionDto(Long id, String name, Boolean disable) {
        this.id = id;
        this.name = name;
    }

    public UIPermissionDto(
            Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
