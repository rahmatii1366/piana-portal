package ir.piana.dev.openidc.core.service.udr;

import ir.piana.dev.openidc.core.dto.*;
import ir.piana.dev.openidc.core.service.udr.dto.UserDomainsAndDomainRolesResponseDto;
import ir.piana.dev.openidc.core.service.udr.dto.UsersByRolesAddToDomainRequestDto;

public interface UsersDomainsRolesService {
    ApiResponse<?> addUsersByRolesIntoDomain(UsersByRolesAddToDomainRequestDto dto);
    ApiResponse<UserDomainsAndDomainRolesResponseDto> getUserDomainsAndDomainRoles(long userId);
}
