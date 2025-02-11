package ir.piana.dev.openidc.core.service.role.dto;

public class RoleDto {
    private Long id;
    private String name;
    private String description;
    private String persianDateTime;

    public RoleDto() {
    }

    public RoleDto(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public RoleDto(Long id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
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

    public String getPersianDateTime() {
        return persianDateTime;
    }

    public void setPersianDateTime(String persianDateTime) {
        this.persianDateTime = persianDateTime;
    }
}
