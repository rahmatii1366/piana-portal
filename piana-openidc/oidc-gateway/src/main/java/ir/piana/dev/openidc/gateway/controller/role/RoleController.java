package ir.piana.dev.openidc.gateway.controller.role;

import ir.piana.dev.openidc.core.dto.*;
import ir.piana.dev.openidc.core.service.role.RoleService;
import ir.piana.dev.openidc.core.service.role.dto.RoleDto;
import ir.piana.dev.openidc.core.service.role.dto.RolePerUserOnDomainRequestDto;
import ir.piana.dev.openidc.core.service.role.dto.RolesForDomainDto;
import ir.piana.dev.openidc.core.service.role.dto.RolesForDomainRequestDto;
import ir.piana.dev.openidc.gateway.controller.role.dto.CreateRoleRequestDto;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${oidc-ui.controller.base-url:oidc-ui}/api/v1/piana/oidc/role")
@RequiredArgsConstructor
public class RoleController {
    private final RoleService roleService;

    @PostMapping(path = "create")
    public ResponseEntity<ApiResponse<?>> createRole(
            HttpServletRequest request,
            @RequestBody CreateRoleRequestDto createRoleRequestDto) {
        ApiResponse<?> apiResponse = roleService.create(
                createRoleRequestDto.getName(), createRoleRequestDto.getDescription());
        return ResponseEntity.ok(apiResponse);
    }
}
