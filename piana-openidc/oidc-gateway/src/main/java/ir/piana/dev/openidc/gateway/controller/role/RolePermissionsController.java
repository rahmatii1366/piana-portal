package ir.piana.dev.openidc.gateway.controller.role;

import ir.piana.dev.openidc.core.dto.ApiResponse;
import ir.piana.dev.openidc.core.dto.PaginationRequest;
import ir.piana.dev.openidc.core.dto.PaginationResponse;
import ir.piana.dev.openidc.core.service.role.dto.RoleDto;
import ir.piana.dev.openidc.core.service.role.RoleService;
import ir.piana.dev.openidc.gateway.controller.role.dto.CreateRoleRequestDto;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${oidc-ui.controller.base-url:oidc-ui}/api/v1/piana/oidc/role-permissions")
@RequiredArgsConstructor
public class RolePermissionsController {
    private final RoleService roleService;

    @PostConstruct
    public void init() {
        System.out.println();
    }

    @GetMapping(path = "all")
    public ResponseEntity<PaginationResponse<RoleDto>> getRoles(
            HttpServletRequest request, @ModelAttribute PaginationRequest paginationRequest) {
        return ResponseEntity.ok(roleService.roles(
                paginationRequest.getOffset(), paginationRequest.getSize()));
    }

    @PostMapping(path = "create")
    public ResponseEntity<ApiResponse<?>> createRole(
            HttpServletRequest request,
            @RequestBody CreateRoleRequestDto createRoleRequestDto) {
        ApiResponse<?> apiResponse = roleService.create(
                createRoleRequestDto.getName(), createRoleRequestDto.getDescription());
        return ResponseEntity.ok(apiResponse);
    }
}
