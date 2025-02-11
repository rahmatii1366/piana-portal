package ir.piana.dev.openidc.core.service.user;

import ir.piana.dev.openidc.core.dto.*;
import ir.piana.dev.openidc.core.service.permission.dto.PermissionDto;
import ir.piana.dev.openidc.core.service.user.dto.CreateUserRequestDto;
import ir.piana.dev.openidc.core.service.user.dto.UserDto;
import ir.piana.dev.openidc.core.service.user.dto.UserPerDomainRequestDto;

public interface UserService {
    PaginationResponse<UserDto> users(int offset, int size);
    PaginationResponse<UserDto> usersPerDomain(UserPerDomainRequestDto userPerDomainRequestDto);
    ApiResponse<?> create(CreateUserRequestDto dto);
    PaginationResponse<PermissionDto> userPermissions(Long userId, Long domainId, Long roleId, int size, int offset);
}
