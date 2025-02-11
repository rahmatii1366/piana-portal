package ir.piana.dev.openidc.core.service.drp;

import ir.piana.dev.openidc.core.dto.ApiResponse;
import ir.piana.dev.openidc.core.service.drp.dto.RoleByPermissionsAddToDomainRequestDto;

public interface DomainsRolesPermissionsService {
    ApiResponse<?> addRoleByPermissionsIntoDomain(RoleByPermissionsAddToDomainRequestDto dto);
}
