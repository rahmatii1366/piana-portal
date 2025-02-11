package ir.piana.dev.openidc.core.service.permission.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class PermissionDto {
    private Long id;
    private String name;
    private String description;
    private String startHours;
    private String endHours;
    private Boolean disable;
    private String persianDateTime;

    public PermissionDto(Long id, String name, Boolean disable) {
        this.id = id;
        this.name = name;
        this.disable = disable;
    }

    public PermissionDto(
            Long id, String name, String description,
            String startHours, String endHours,
            Boolean disable, String persianDateTime) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.startHours = startHours;
        this.endHours = endHours;
        this.disable = disable;
        this.persianDateTime = persianDateTime;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStartHours() {
        return startHours;
    }

    public void setStartHours(String startHours) {
        this.startHours = startHours;
    }

    public String getEndHours() {
        return endHours;
    }

    public void setEndHours(String endHours) {
        this.endHours = endHours;
    }

    public Boolean getDisable() {
        return disable;
    }

    public void setDisable(Boolean disable) {
        this.disable = disable;
    }

    public String getPersianDateTime() {
        return persianDateTime;
    }

    public void setPersianDateTime(String persianDateTime) {
        this.persianDateTime = persianDateTime;
    }
}
