package ir.piana.dev.openidc.gateway.controller.permission;

import ir.piana.dev.openidc.core.dto.ApiResponse;
import ir.piana.dev.openidc.core.dto.PaginationRequest;
import ir.piana.dev.openidc.core.dto.PaginationResponse;
import ir.piana.dev.openidc.core.service.permission.PermissionService;
import ir.piana.dev.openidc.core.service.permission.dto.CreatePermissionRequestDto;
import ir.piana.dev.openidc.core.service.permission.dto.PermissionDto;
import ir.piana.dev.openidc.core.service.permission.dto.PermissionsForRoleInDomainRequestDto;
import ir.piana.dev.openidc.core.service.permission.dto.UIPermissionDto;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${oidc-ui.controller.base-url:oidc-ui}/api/v1/piana/oidc/permission")
@RequiredArgsConstructor
public class PermissionController {
    private final PermissionService permissionService;

    @PostConstruct
    public void init() {
        System.out.println();
    }

    @GetMapping(path = "all")
    public ResponseEntity<PaginationResponse<PermissionDto>> getClientGroups(
            HttpServletRequest request, @ModelAttribute PaginationRequest paginationRequest) {
        return ResponseEntity.ok(permissionService.permissions(
                paginationRequest.getOffset(), paginationRequest.getSize()));
    }

    @GetMapping(path = "all-ui-permissions")
    public ResponseEntity<PaginationResponse<UIPermissionDto>> getUIPermissions() {
        return ResponseEntity.ok(permissionService.uiPermissions(
                0, Integer.MAX_VALUE));
    }

    @GetMapping(path = "for-role-in-domain")
    public ResponseEntity<PaginationResponse<PermissionDto>> getClientGroups(
            @ModelAttribute PermissionsForRoleInDomainRequestDto dto) {
        return ResponseEntity.ok(permissionService.permissionsForRoleInDomain(dto));
    }

    @PostMapping(path = "create")
    public ResponseEntity<ApiResponse<?>> createClient(
            HttpServletRequest request,
            @RequestBody CreatePermissionRequestDto createPermissionRequestDto) {
        ApiResponse<?> apiResponse = permissionService.create(
                createPermissionRequestDto.getName(), createPermissionRequestDto.getDescription());
        return ResponseEntity.ok(apiResponse);
    }
}
