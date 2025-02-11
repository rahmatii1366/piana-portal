package ir.piana.dev.openidc.core.service.permission;

import ir.piana.dev.openidc.core.dto.ApiResponse;
import ir.piana.dev.openidc.core.dto.PaginationResponse;
import ir.piana.dev.openidc.core.service.permission.dto.PermissionDto;
import ir.piana.dev.openidc.core.service.permission.dto.PermissionsForRoleInDomainRequestDto;
import ir.piana.dev.openidc.core.service.permission.dto.UIPermissionDto;

public interface PermissionService {
    PaginationResponse<PermissionDto> permissions(int offset, int size);
    PaginationResponse<UIPermissionDto> uiPermissions(int offset, int size);
    PaginationResponse<PermissionDto> permissionsForRoleInDomain(PermissionsForRoleInDomainRequestDto dto);
    ApiResponse create(String name, String description);
}
