package ir.piana.dev.openidc.core.service.role;

import ir.piana.dev.openidc.core.dto.*;
import ir.piana.dev.openidc.core.service.role.dto.RoleDto;
import ir.piana.dev.openidc.core.service.role.dto.RolePerUserOnDomainRequestDto;
import ir.piana.dev.openidc.core.service.role.dto.RolesForDomainDto;
import ir.piana.dev.openidc.core.service.role.dto.RolesForDomainRequestDto;

public interface RoleService {
    PaginationResponse<RoleDto> roles(int offset, int size);
    PaginationResponse<RoleDto> roles(RolePerUserOnDomainRequestDto dto);
    PaginationResponse<RolesForDomainDto> rolesForDomain(RolesForDomainRequestDto dto);
    ApiResponse create(String name, String description);
}
