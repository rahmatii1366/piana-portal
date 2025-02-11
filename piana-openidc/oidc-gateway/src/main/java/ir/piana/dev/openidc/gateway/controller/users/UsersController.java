package ir.piana.dev.openidc.gateway.controller.users;

import ir.piana.dev.openidc.core.dto.*;
import ir.piana.dev.openidc.core.service.permission.dto.PermissionDto;
import ir.piana.dev.openidc.core.service.udr.dto.UserDomainsAndDomainRolesResponseDto;
import ir.piana.dev.openidc.core.service.user.UserService;
import ir.piana.dev.openidc.core.service.udr.UsersDomainsRolesService;
import ir.piana.dev.openidc.core.service.user.dto.CreateUserRequestDto;
import ir.piana.dev.openidc.core.service.user.dto.UserDto;
import ir.piana.dev.openidc.gateway.controller.users.dto.UserPermissionsRequestDto;
import ir.piana.dev.openidc.core.service.user.dto.UserPerDomainRequestDto;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${oidc-ui.controller.base-url:oidc-ui}/api/v1/piana/oidc/user")
@RequiredArgsConstructor
public class UsersController {
    private final UserService userService;
    private final UsersDomainsRolesService usersDomainsRolesService;

    @PostConstruct
    public void init() {
        System.out.println();
    }

    @GetMapping(path = "all")
    public ResponseEntity<PaginationResponse<UserDto>> getRoles(
            HttpServletRequest request, @ModelAttribute PaginationRequest paginationRequest) {
        return ResponseEntity.ok(userService.users(
                paginationRequest.getOffset(), paginationRequest.getSize()));
    }

    @GetMapping(path = "per-domain")
    public ResponseEntity<PaginationResponse<UserDto>> getUsersPerDomain(
            @Valid @ModelAttribute UserPerDomainRequestDto userPerDomainRequestDto) {
        return ResponseEntity.ok(userService.usersPerDomain(userPerDomainRequestDto));
    }

    @PostMapping(path = "create")
    public ResponseEntity<ApiResponse<?>> createUser(
            HttpServletRequest request,
            @RequestBody CreateUserRequestDto createRoleRequestDto) {
        ApiResponse<?> apiResponse = userService.create(createRoleRequestDto);
        return ResponseEntity.ok(apiResponse);
    }

    @PreAuthorize("hasAuthority('PERM_READ_USER_PERMISSIONS')")
    @PostMapping(path = "user-permissions")
    public ResponseEntity<PaginationResponse<PermissionDto>> userPermissions(
            @ModelAttribute UserPermissionsRequestDto dto
            ) {
        return ResponseEntity.ok(userService.userPermissions(
                dto.getUserId(),
                dto.getDomainId(),
                dto.getRoleId(),
                dto.getSize(),
                dto.getOffset()));
    }

    @PreAuthorize("hasAuthority('PERM_READ_USER_PERMISSIONS')")
    @GetMapping(path = "domains-and-its-roles")
    public ResponseEntity<ApiResponse<UserDomainsAndDomainRolesResponseDto>> getDomainsAndItsRoles(
            @RequestParam("userId") long userId
    ) {
        return ResponseEntity.ok(usersDomainsRolesService.getUserDomainsAndDomainRoles(userId));
    }
}
