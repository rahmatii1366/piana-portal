package ir.piana.dev.openidc.core.service.role.dto;

public class RolesForDomainDto {
    private Long id;
    private String name;
    private String description;
    private boolean assigned;

    public RolesForDomainDto() {
    }

    public RolesForDomainDto(Long id, String name, String description, boolean assigned) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.assigned = assigned;
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

    public boolean isAssigned() {
        return assigned;
    }

    public void setAssigned(boolean assigned) {
        this.assigned = assigned;
    }
}
