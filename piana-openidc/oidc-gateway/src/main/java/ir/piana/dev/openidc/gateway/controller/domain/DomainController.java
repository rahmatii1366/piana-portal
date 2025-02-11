package ir.piana.dev.openidc.gateway.controller.domain;

import ir.piana.dev.openidc.core.dto.*;
import ir.piana.dev.openidc.core.service.domain.DomainService;
import ir.piana.dev.openidc.core.service.drp.DomainsRolesPermissionsService;
import ir.piana.dev.openidc.core.service.drp.dto.RoleByPermissionsAddToDomainRequestDto;
import ir.piana.dev.openidc.core.service.udr.UsersDomainsRolesService;
import ir.piana.dev.openidc.core.service.domain.dto.DomainDto;
import ir.piana.dev.openidc.core.service.domain.dto.DomainForUserDto;
import ir.piana.dev.openidc.core.service.permission.dto.PermissionDto;
import ir.piana.dev.openidc.core.service.role.dto.RoleDto;
import ir.piana.dev.openidc.core.service.udr.dto.UsersByRolesAddToDomainRequestDto;
import ir.piana.dev.openidc.gateway.controller.domain.dto.CreateDomainRequestDto;
import ir.piana.dev.openidc.gateway.controller.domain.dto.DomainRequestDto;
import ir.piana.dev.openidc.core.service.domain.dto.GetDomainsForUserRequest;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${oidc-ui.controller.base-url:oidc-ui}/api/v1/piana/oidc/domain")
@RequiredArgsConstructor
public class DomainController {
    private final DomainService domainService;
    private final UsersDomainsRolesService usersDomainsRolesService;
    private final DomainsRolesPermissionsService domainsRolesPermissionsService;

    @PostConstruct
    public void init() {
        System.out.println();
    }

    @GetMapping(path = "all")
    public ResponseEntity<PaginationResponse<DomainDto>> getDomains(
            HttpServletRequest request, @ModelAttribute PaginationRequest paginationRequest) {
        return ResponseEntity.ok(domainService.domains(
                paginationRequest.getOffset(), paginationRequest.getSize()));
    }

    @GetMapping(path = "for-user")
    public ResponseEntity<PaginationResponse<DomainForUserDto>> getDomainsForUser(
            @Valid @ModelAttribute GetDomainsForUserRequest request) {
        return ResponseEntity.ok(domainService.domainsForUser(request));
    }


    @GetMapping(path = "roles")
    public ResponseEntity<PaginationResponse<RoleDto>> getDomainRoles(
            @Valid @ModelAttribute DomainRequestDto dto) {
        return ResponseEntity.ok(domainService.domainRoles(
                dto.getOffset(), dto.getSize(), dto.getDomainId()));
    }

    @GetMapping(path = "permissions")
    public ResponseEntity<PaginationResponse<PermissionDto>> getDomainPermission(
            @Valid @ModelAttribute DomainRequestDto dto) {
        return ResponseEntity.ok(domainService.domainPermissions(
                dto.getOffset(), dto.getSize(), dto.getDomainId()));
    }

    @PostMapping(path = "create")
    public ResponseEntity<ApiResponse<?>> createDomain(
            HttpServletRequest request,
            @RequestBody CreateDomainRequestDto createDomainRequestDto) {
        ApiResponse<?> apiResponse = domainService.create(
                createDomainRequestDto.getName(), createDomainRequestDto.getDescription());
        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping(path = "add-users-by-roles")
    public ResponseEntity<ApiResponse<?>> addUsersByRoles(
            @Valid @RequestBody UsersByRolesAddToDomainRequestDto dto) {
        ApiResponse<?> apiResponse = usersDomainsRolesService.addUsersByRolesIntoDomain(dto);
        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping(path = "add-role-by-permissions")
    public ResponseEntity<ApiResponse<?>> addRoleByPermissions(
            @Valid @RequestBody RoleByPermissionsAddToDomainRequestDto dto) {
        ApiResponse<?> apiResponse = domainsRolesPermissionsService.addRoleByPermissionsIntoDomain(dto);
        return ResponseEntity.ok(apiResponse);
    }
}
