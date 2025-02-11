package ir.piana.dev.openidc.core.service.domain;

import ir.piana.dev.openidc.core.dto.*;
import ir.piana.dev.openidc.core.service.domain.dto.DomainDto;
import ir.piana.dev.openidc.core.service.domain.dto.DomainForUserDto;
import ir.piana.dev.openidc.core.service.domain.dto.GetDomainsForUserRequest;
import ir.piana.dev.openidc.core.service.permission.dto.PermissionDto;
import ir.piana.dev.openidc.core.service.role.dto.RoleDto;

import java.util.List;

public interface DomainService {
    PaginationResponse<DomainDto> domains(int offset, int size);
    PaginationResponse<DomainForUserDto> domainsForUser(GetDomainsForUserRequest request);
    PaginationResponse<RoleDto> domainRoles(int offset, int size, long domainId);
    PaginationResponse<PermissionDto> domainRolePermissions(
            int offset, int size, Long domainId, Long roleId);

    List<Long> userDomainPermissionsByType(
            Long userId, Long domainId, String permissionTypeName);

    PaginationResponse<PermissionDto> domainPermissions(
            int offset, int size, Long domainId);
    ApiResponse<?> create(String name, String description);
}
